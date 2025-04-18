package com.fullcreative.citypulse.presentation.screens.citylocationdetail


import com.fullcreative.citypulse.domain.model.CityEvent

sealed class CityLocationDetailUiState {
    object Loading : CityLocationDetailUiState()
    data class Success(val city: CityEvent) : CityLocationDetailUiState()
    data class Error(val message: String) : CityLocationDetailUiState()
}