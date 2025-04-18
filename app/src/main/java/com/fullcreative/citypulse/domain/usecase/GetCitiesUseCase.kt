package com.fullcreative.citypulse.domain.usecase

import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.domain.repository.CityEventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val repository: CityEventRepository
) {
    operator fun invoke(): Flow<List<CityEvent>> = repository.getAllCities()
}