package com.fullcreative.citypulse.data.local.repository

import com.fullcreative.citypulse.data.local.dao.CityEventDao
import com.fullcreative.citypulse.data.local.entity.CityEvent
import com.fullcreative.citypulse.domain.model.CityEvent as DomainCityEvent
import com.fullcreative.citypulse.domain.repository.CityEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityEventRepositoryImpl @Inject constructor(
    private val dao: CityEventDao
) : CityEventRepository {

    override fun getAllCities(): Flow<List<DomainCityEvent>> =
        dao.getAll().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun insertCity(event: DomainCityEvent) {
        dao.insert(
            CityEvent(
                id = event.id,
                name = event.name,
                color = event.color,
                timestamp = event.timestamp
            )
        )
    }

    override suspend fun getCityById(id: String): DomainCityEvent? {
        return dao.getById(id)?.toDomain()
    }
}