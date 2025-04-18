package com.fullcreative.citypulse.data.local.repository

import com.fullcreative.citypulse.data.local.dao.CityEventDao
import com.fullcreative.citypulse.data.local.entity.CityEvent
import com.fullcreative.citypulse.domain.model.CityEvent as DomainCityEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CityEventRepositoryImplTest {

    private lateinit var dao: CityEventDao
    private lateinit var repository: CityEventRepositoryImpl

    @Before
    fun setUp() {
        dao = mock(CityEventDao::class.java)
        repository = CityEventRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        // No specific teardown required
    }

    @Test
    fun testGetAllCitiesReturnsFlowOfDomainEvents() = runTest {
        val cityEntities = listOf(
            CityEvent(1, "City A", "red", 123456L),
            CityEvent(2, "City B", "blue", 123457L)
        )
        `when`(dao.getAll()).thenReturn(flowOf(cityEntities))

        val resultFlow = repository.getAllCities()

        resultFlow.collect { domainCities ->
            assertEquals(2, domainCities.size)
            assertEquals("City A", domainCities[0].name)
            assertEquals("City B", domainCities[1].name)
        }
    }

    @Test
    fun testInsertCityInsertsDomainEventIntoDao() = runTest {
        val domainCity = DomainCityEvent(1, "City A", "red", 123456L)

        repository.insertCity(domainCity)

        val argumentCaptor = ArgumentCaptor.forClass(CityEvent::class.java)
        verify(dao).insert(argumentCaptor.capture())
        val inserted = argumentCaptor.value

        assertEquals("1", inserted.id)
        assertEquals("City A", inserted.name)
        assertEquals("red", inserted.color)
        assertEquals(123456L, inserted.timestamp)
    }

    @Test
    fun testGetCityByIdReturnsMappedDomainCityWhenFound() = runTest {
        val cityEntity = CityEvent(1, "City A", "red", 123456L)
        `when`(dao.getById("1")).thenReturn(cityEntity)

        val result = repository.getCityById("1")

        assertNotNull(result)
        assertEquals("1", result?.id)
        assertEquals("City A", result?.name)
    }

    @Test
    fun testGetCityByIdReturnsNullWhenCityNotFound() = runTest {
        `when`(dao.getById("999")).thenReturn(null)

        val result = repository.getCityById("999")

        assertNull(result)
    }
}
