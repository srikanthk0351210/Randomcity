
package com.fullcreative.citypulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fullcreative.citypulse.data.local.entity.CityEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CityEventDao {
    @Query("SELECT * FROM city_events ORDER BY name ASC")
    fun getAll(): Flow<List<CityEvent>>

    @Insert
    suspend fun insert(event: CityEvent)

    @Query("SELECT * FROM city_events WHERE id = :id")
    suspend fun getById(id: String): CityEvent?
}