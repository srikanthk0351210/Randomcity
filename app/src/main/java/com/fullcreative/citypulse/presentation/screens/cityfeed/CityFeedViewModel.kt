package com.fullcreative.citypulse.presentation.screens.cityfeed

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullcreative.citypulse.domain.repository.CityEventRepository
import com.fullcreative.citypulse.domain.usecase.GenerateCityEventUseCase
import com.fullcreative.citypulse.domain.usecase.GetCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityFeedViewModel @Inject constructor(
    private val getCities: GetCitiesUseCase,
    private val generateEvent: GenerateCityEventUseCase,
    private val repository: CityEventRepository,
) : ViewModel() {

    private val _cities = MutableStateFlow<CityFeedState>(CityFeedState.Loading)
    val cities: StateFlow<CityFeedState> = _cities.asStateFlow()

    private var citiesJob: Job? = null
    private var producerJob: Job? = null
    private val lifecycleObserver = AppLifecycleObserver()

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
        loadCities()
    }

    inner class AppLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            startProducing(true)
        }

        override fun onStop(owner: LifecycleOwner) {
            stopProducing()
        }
    }

    fun loadCities() {
        citiesJob?.cancel()
        citiesJob = viewModelScope.launch {
            _cities.value = CityFeedState.Loading
            try {
                getCities()
                    .catch { e ->
                        _cities.value = CityFeedState.Error(
                            e.message ?: "Failed to load cities"
                        )
                    }
                    .collect { cities ->
                        _cities.value = if (cities.isNotEmpty()) {
                            CityFeedState.Success(cities)
                        } else {
                            CityFeedState.Error("No cities available")
                        }
                    }
            } catch (e: Exception) {
                _cities.value = CityFeedState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun refreshData() {
        loadCities()
    }

    fun startProducing(shouldProduce: Boolean) {
        producerJob?.cancel()
        if (shouldProduce) {
            producerJob = viewModelScope.launch {
                delay(1000) // Initial delay
                while (isActive) {
                    val newEvent = generateEvent()
                    repository.insertCity(newEvent)
                    delay(5000)
                }
            }
        }
    }

    fun stopProducing() {
        producerJob?.cancel()
        producerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
        citiesJob?.cancel()
        stopProducing()
    }
}

