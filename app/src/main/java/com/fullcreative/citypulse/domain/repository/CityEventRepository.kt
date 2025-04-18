package com.fullcreative.citypulse.domain.repository

import com.fullcreative.citypulse.domain.model.CityEvent
import kotlinx.coroutines.flow.Flow

interface CityEventRepository {
    fun getAllCities(): Flow<List<CityEvent>>
    suspend fun insertCity(event: CityEvent)
    suspend fun getCityById(id: String): CityEvent?
}