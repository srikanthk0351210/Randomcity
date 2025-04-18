package com.fullcreative.citypulse.presentation.viewmodel.cityfeed

import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.domain.repository.CityEventRepository
import com.fullcreative.citypulse.domain.usecase.GenerateCityEventUseCase
import com.fullcreative.citypulse.domain.usecase.GetCitiesUseCase
import com.fullcreative.citypulse.presentation.screens.cityfeed.CityFeedState
import com.fullcreative.citypulse.presentation.screens.cityfeed.CityFeedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CityFeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockGetCitiesUseCase: GetCitiesUseCase

    @Mock
    private lateinit var mockGenerateEventUseCase: GenerateCityEventUseCase

    @Mock
    private lateinit var mockRepository: CityEventRepository

    private lateinit var viewModel: CityFeedViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Use the standard `Mockito.when` syntax
        `when`(mockGetCitiesUseCase()).thenReturn(flowOf(emptyList()))
        `when`(mockGenerateEventUseCase()).thenReturn(
            CityEvent(id = 0, name = "Default", timestamp = 0L, color = "gray")
        )

        viewModel = CityFeedViewModel(
            getCities = mockGetCitiesUseCase,
            generateEvent = mockGenerateEventUseCase,
            repository = mockRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load cities and start producing`() = runTest {
        val mockCities = listOf(CityEvent(id = 1, name = "City1", timestamp = 123456789, color = "red"))
        `when`(mockGetCitiesUseCase()).thenReturn(flowOf(mockCities))
        `when`(mockGenerateEventUseCase()).thenReturn(CityEvent(id = 2, name = "City2", timestamp = 123456790, color = "blue"))

        viewModel = CityFeedViewModel(mockGetCitiesUseCase, mockGenerateEventUseCase, mockRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CityFeedState.Success(mockCities), viewModel.cities.value)
        Mockito.verify(mockGetCitiesUseCase).invoke()
        Mockito.verify(mockRepository).insertCity(Mockito.any())
    }

    @Test
    fun `loadCities should update state to Loading initially`() = runTest {
        val mockCities = listOf(CityEvent(id = 1, name = "City1", timestamp = 123456789, color = "red"))
        `when`(mockGetCitiesUseCase()).thenReturn(flow { emit(mockCities) })

        viewModel.loadCities()

        assertEquals(CityFeedState.Loading, viewModel.cities.value)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(CityFeedState.Success(mockCities), viewModel.cities.value)
    }

    @Test
    fun `loadCities should handle errors`() = runTest {
        val errorMessage = "Test error"
        `when`(mockGetCitiesUseCase()).thenReturn(flow { throw RuntimeException(errorMessage) })

        viewModel.loadCities()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CityFeedState.Error(errorMessage), viewModel.cities.value)
    }

    @Test
    fun `refreshData should reload cities`() = runTest {
        val mockCities1 = listOf(CityEvent(id = 1, name = "City1", timestamp = 123456789, color = "red"))
        val mockCities2 = listOf(CityEvent(id = 2, name = "City2", timestamp = 123456790, color = "blue"))

        `when`(mockGetCitiesUseCase())
            .thenReturn(flowOf(mockCities1))
            .thenReturn(flowOf(mockCities2))

        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(CityFeedState.Success(mockCities2), viewModel.cities.value)

        Mockito.verify(mockGetCitiesUseCase, Mockito.times(2)).invoke()
    }

    @Test
    fun `startProducing should generate events when app is in foreground`() = runTest {
        val mockEvent = CityEvent(id = 1, name = "City1", timestamp = 123456789, color = "red")
        `when`(mockGenerateEventUseCase()).thenReturn(mockEvent)

        viewModel.startProducing(true)
        testDispatcher.scheduler.advanceTimeBy(6000)

        Mockito.verify(mockGenerateEventUseCase, Mockito.atLeastOnce()).invoke()
        Mockito.verify(mockRepository, Mockito.atLeastOnce()).insertCity(mockEvent)
    }

    @Test
    fun `startProducing should not generate events when app is in background`() = runTest {
        viewModel.startProducing(false)
        testDispatcher.scheduler.advanceTimeBy(6000)

        Mockito.verify(mockGenerateEventUseCase, Mockito.never()).invoke()
        Mockito.verify(mockRepository, Mockito.never()).insertCity(Mockito.any())
    }

    @Test
    fun `stopProducing should cancel the producer job`() = runTest {
        val mockEvent = CityEvent(id = 1, name = "City1", timestamp = 123456789, color = "red")
        `when`(mockGenerateEventUseCase()).thenReturn(mockEvent)

        viewModel.startProducing(true)
        viewModel.stopProducing()
        testDispatcher.scheduler.advanceTimeBy(6000)

        Mockito.verify(mockGenerateEventUseCase, Mockito.atMostOnce()).invoke()
        Mockito.verify(mockRepository, Mockito.atMostOnce()).insertCity(mockEvent)
    }
}
