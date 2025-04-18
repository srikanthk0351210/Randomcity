package com.fullcreative.citypulse.presentation.screens.cityfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.domain.repository.CityEventRepository
import com.fullcreative.citypulse.domain.usecase.GenerateCityEventUseCase
import com.fullcreative.citypulse.domain.usecase.GetCitiesUseCase
import com.fullcreative.citypulse.presentation.screens.citylocationdetail.CityLocationDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityFeedViewModel @Inject constructor(
    private val getCities: GetCitiesUseCase,
    private val generateEvent: GenerateCityEventUseCase,
    private val repository: CityEventRepository
) : ViewModel() {

    private val _cities= MutableStateFlow<CityFeedState>(CityFeedState.Loading)
    val cities: StateFlow<CityFeedState> = _cities



    private var producerJob: Job? = null

    init {
        loadCities()
        startProducing(true)
    }



    fun loadCities() {
        viewModelScope.launch {
            _cities.value = CityFeedState.Loading
            try {
                getCities()
                    .collect { cities ->
                        _cities.value = CityFeedState.Success(cities)
                    }
            } catch (e: Exception) {
                _cities.value = CityFeedState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun refreshData() {
        loadCities()
    }

    fun startProducing(isAppInForeground: Boolean) {
        producerJob?.cancel()
        if (isAppInForeground) {
            producerJob = viewModelScope.launch {
                delay(1000) // Initial delay
                while (true) {
                    val newEvent = generateEvent()
                    repository.insertCity(newEvent)
                    repeat(5) {
                        if (producerJob?.isActive == true) delay(1000)
                    }
                }
            }
        }
    }

    fun stopProducing() {
        producerJob?.cancel()
    }
}

