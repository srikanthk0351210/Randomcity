package com.fullcreative.citypulse.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fullcreative.citypulse.data.local.database.AppDatabase
import com.fullcreative.citypulse.data.local.entity.CityEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CityEventDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var cityEventDao: CityEventDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            AppDatabase::class.java
        ).build()

        cityEventDao = db.cityEventDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insertCity should insert city into database`() = runTest {
        val city = CityEvent(
            id = 1,
            name = "Test City",
            color = "red",
            timestamp = System.currentTimeMillis()
        )

        cityEventDao.insert(city)
        val insertedCity = cityEventDao.getById("1")

        assertNotNull(insertedCity)
        assertEquals(city, insertedCity)
    }

    @Test
    fun `getAllCities should return all cities ordered by name`() = runTest {
        val city1 = CityEvent(1, "City B", "red", System.currentTimeMillis())
        val city2 = CityEvent(2, "City A", "blue", System.currentTimeMillis())

        cityEventDao.insert(city1)
        cityEventDao.insert(city2)

        val cities = cityEventDao.getAll().first()

        assertEquals(2, cities.size)
        assertEquals("City A", cities[0].name)
        assertEquals("City B", cities[1].name)
    }

    @Test
    fun `getCityById should return the correct city by id`() = runTest {
        val city = CityEvent(1, "Test City", "red", System.currentTimeMillis())

        cityEventDao.insert(city)
        val retrievedCity = cityEventDao.getById("1")

        assertNotNull(retrievedCity)
        assertEquals(city, retrievedCity)
    }

    @Test
    fun `getCityById should return null if city does not exist`() = runTest {
        val retrievedCity = cityEventDao.getById("nonexistent_id")
        assertNull(retrievedCity)
    }
}
