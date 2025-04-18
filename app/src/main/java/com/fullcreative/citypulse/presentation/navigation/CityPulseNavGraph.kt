package com.fullcreative.citypulse.presentation.navigation

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.presentation.screens.citylocationdetail.CityDetailScreen
import com.fullcreative.citypulse.presentation.screens.cityfeed.CityFeedScreen
import com.fullcreative.citypulse.presentation.screens.splash.SplashScreen

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CityPulseNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val activity = LocalContext.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTallEnough = configuration.screenHeightDp >= 600

    val isTabletLandscape = isLandscape &&
            isTallEnough &&
            windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium


    val navActions = remember(navController) {
        CityPulseNavigationActions(navController)
    }

    var selectedCity by remember { mutableStateOf<CityEvent?>(null) }

    NavHost(
        navController = navController,
        startDestination = CityPulseDestinations.SPLASH_ROUTE,
        modifier = modifier
    ) {
        composable(CityPulseDestinations.SPLASH_ROUTE) {
            SplashScreen(
                onNavigate = { navActions.navigateToCityFeed() }
            )
        }

        composable(CityPulseDestinations.CITY_FEED_ROUTE) {
            if (isTabletLandscape) {
                Row {
                    CityFeedScreen(
                        onItemClick = { selectedCity = it },
                        isTabletLandscape = true,
                        selectedCity = selectedCity
                    )
                    CityDetailScreen(
                        city = selectedCity,
                        onBackClick = { selectedCity = null }
                    )
                }
            } else {
                CityFeedScreen(
                    onItemClick = { navActions.navigateToCityDetail(it) }
                )
            }
        }

        composable(CityPulseDestinations.CITY_DETAIL_ROUTE) {
            val city = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<CityEvent>(CityPulseDestinations.CITY_EVENT_ARG)

            CityDetailScreen(
                city = city,
                onBackClick = { navActions.popBack() }
            )
        }
    }
}
