package com.fullcreative.citypulse.presentation.screens.citylocationdetail
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CityLocationDetailViewModel @Inject constructor(
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    private val _state = MutableStateFlow<CityLocationDetailUiState>(CityLocationDetailUiState.Loading)
    val state: StateFlow<CityLocationDetailUiState> = _state.asStateFlow()


 fun loadCityFromArgument(city: CityEvent) {
     _state.value = CityLocationDetailUiState.Success(city)
     scheduleWelcomeNotification(city.name)
 }

    fun setStateForTesting(state: CityLocationDetailUiState) {
        _state.value = state
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleWelcomeNotification(name: String) {
        val city = (state.value as? CityLocationDetailUiState.Success)?.city ?: return
        notificationScheduler.scheduleWelcomeNotification(
            cityId = city.id.toString(),
            cityName = city.name
        )
    }
}