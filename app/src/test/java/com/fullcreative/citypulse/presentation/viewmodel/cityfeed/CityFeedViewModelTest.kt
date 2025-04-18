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
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

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
    fun `loadCities should update state to Success when cities are returned`() = runTest {
        val mockCities = listOf(CityEvent(1, "City1",  "red",123456789))
        `when`(mockGetCitiesUseCase()).thenReturn(flowOf(mockCities))

        viewModel.loadCities()

        assertEquals(CityFeedState.Loading, viewModel.cities.value)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CityFeedState.Success(mockCities), viewModel.cities.value)
    }

    @Test
    fun `loadCities should emit Error state when exception is thrown`() = runTest {
        val errorMsg = "Test error"
        `when`(mockGetCitiesUseCase()).thenReturn(flow { throw RuntimeException(errorMsg) })

        viewModel.loadCities()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CityFeedState.Error(errorMsg), viewModel.cities.value)
    }

    @Test
    fun `refreshData should trigger loadCities`() = runTest {
        val mockCities = listOf(CityEvent(2, "City2", "blue",123456790))
        `when`(mockGetCitiesUseCase()).thenReturn(flowOf(mockCities))

        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CityFeedState.Success(mockCities), viewModel.cities.value)
    }

    @Test
    fun `startProducing true should generate and insert city events`() = runTest {
        val mockEvent = CityEvent(3, "City3", "green",123456791)
        `when`(mockGenerateEventUseCase()).thenReturn(mockEvent)

        viewModel.startProducing(true)
        testDispatcher.scheduler.advanceTimeBy(6000)

        Mockito.verify(mockGenerateEventUseCase, Mockito.atLeastOnce()).invoke()
        Mockito.verify(mockRepository, Mockito.atLeastOnce()).insertCity(mockEvent)
    }

    @Test
    fun `startProducing false should not generate events`() = runTest {
        viewModel.startProducing(false)
        testDispatcher.scheduler.advanceTimeBy(6000)

        Mockito.verify(mockGenerateEventUseCase, Mockito.never()).invoke()
        Mockito.verify(mockRepository, Mockito.never()).insertCity(Mockito.any())
    }

    @Test
    fun `stopProducing should cancel the job`() = runTest {
        val mockEvent = CityEvent(4, "City4", "orange",123456792)
        `when`(mockGenerateEventUseCase()).thenReturn(mockEvent)

        viewModel.startProducing(true)
        viewModel.stopProducing()
        testDispatcher.scheduler.advanceTimeBy(6000)

        Mockito.verify(mockGenerateEventUseCase, Mockito.atMostOnce()).invoke()
        Mockito.verify(mockRepository, Mockito.atMostOnce()).insertCity(mockEvent)
    }
}
