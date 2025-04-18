package com.fullcreative.citypulse.domain.usecase

import com.fullcreative.citypulse.domain.model.CityEvent
import javax.inject.Inject


class GenerateCityEventUseCase @Inject constructor() {
    private val cities = listOf("New York", "Los Angeles", "Scranton","Philapedia","Nashville","Saint Louis","Miami")
    private val colors = listOf("Yellow", "White", "Green","Blue", "Red", "Black")

    operator fun invoke(): CityEvent {
        return CityEvent(
            id = 0,
            color = colors.random(),
            name = cities.random(),
            timestamp = System.currentTimeMillis(),
        )
    }
}