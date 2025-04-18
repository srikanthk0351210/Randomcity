package com.fullcreative.citypulse.presentation.screens.cityfeed
import com.fullcreative.citypulse.domain.model.CityEvent

sealed class CityFeedState {
    object Loading : CityFeedState()
    data class Success(val cities: List<CityEvent>) : CityFeedState()
    data class Error(val message: String) : CityFeedState()
}