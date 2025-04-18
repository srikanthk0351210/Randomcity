package com.fullcreative.citypulse.presentation.screens.citylocationdetail

import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.notifications.NotificationScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class CityLocationDetailViewModelTest {

    private lateinit var viewModel: CityLocationDetailViewModel
    private lateinit var notificationScheduler: NotificationScheduler

    @Before
    fun setup() {
        notificationScheduler = mock()
        viewModel = CityLocationDetailViewModel(notificationScheduler)
    }

    @Test
    fun `scheduleWelcomeNotification should not schedule notification if state is not Success`() = runTest {
        // Given
        val cityEvent = CityEvent(id = 1, name = "Test City", timestamp = 123456789L, color = "red")


        viewModel.loadCityFromArgument(cityEvent)
        viewModel.setStateForTesting(CityLocationDetailUiState.Loading)

        viewModel.scheduleWelcomeNotification("Test City")

        verify(notificationScheduler, never()).scheduleWelcomeNotification(
            cityId = "1",
            cityName = "Test City"
        )
    }

}
