package com.fullcreative.citypulse.domain.usecases

import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.domain.repository.CityEventRepository
import com.fullcreative.citypulse.domain.usecase.GetCitiesUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.IOException

class GetCitiesUseCaseTest {

    private lateinit var repository: CityEventRepository
    private lateinit var useCase: GetCitiesUseCase

    private val testCities = listOf(
        CityEvent(id = 1, name = "New York", color = "Blue"),
        CityEvent(id = 2, name = "Los Angeles", color = "Red")
    )

    @Before
    fun setUp() {
        repository = mock(CityEventRepository::class.java)
        useCase = GetCitiesUseCase(repository)
    }

    @Test
    fun `invoke should return cities from repository`() = runTest {
        `when`(repository.getAllCities()).thenReturn(flowOf(testCities))

        val result = useCase().toList()

        assertEquals(1, result.size)
        assertEquals(testCities, result[0])
    }

    @Test
    fun `should emit empty list when repository returns empty`() = runTest {
        `when`(repository.getAllCities()).thenReturn(flowOf(emptyList()))

        val result = useCase().toList()

        assertEquals(1, result.size)
        assertTrue(result[0].isEmpty())
    }

    @Test
    fun `should propagate multiple emissions from repository`() = runTest {
        val updatedCities = testCities + CityEvent(id = 3 ,name = "Chicago", color = "White")
        `when`(repository.getAllCities()).thenReturn(flowOf(testCities, updatedCities))

        val result = useCase().toList()

        assertEquals(2, result.size)
        assertEquals(testCities, result[0])
        assertEquals(updatedCities, result[1])
    }

    @Test
    fun `should propagate errors from repository`() = runTest {
        val testError = IOException("Database error")
        `when`(repository.getAllCities()).thenReturn(flow { throw testError })

        try {
            useCase().toList()
            fail("Expected exception to be thrown")
        } catch (e: IOException) {
            assertEquals(testError.message, e.message)
        }
    }

    @Test
    fun `should return cold flow that starts on collection`() = runTest {
        var collected = false
        `when`(repository.getAllCities()).thenReturn(flow {
            collected = true
            emit(testCities)
        })

        val flow = useCase()

        assertFalse(collected)

        flow.toList()

        assertTrue(collected)
    }
}
