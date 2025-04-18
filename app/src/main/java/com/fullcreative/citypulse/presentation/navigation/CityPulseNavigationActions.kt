package com.fullcreative.citypulse.presentation.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.fullcreative.citypulse.domain.model.CityEvent

class CityPulseNavigationActions(private val navController: NavHostController) {

    fun navigateToCityFeed() {
        navController.navigate(CityPulseDestinations.CITY_FEED_ROUTE) {
            popUpTo(CityPulseDestinations.SPLASH_ROUTE) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToCityDetail(city: CityEvent) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(CityPulseDestinations.CITY_EVENT_ARG, city)

        navController.navigate(CityPulseDestinations.CITY_DETAIL_ROUTE)
    }

    fun popBack() {
        navController.popBackStack()
    }
}
