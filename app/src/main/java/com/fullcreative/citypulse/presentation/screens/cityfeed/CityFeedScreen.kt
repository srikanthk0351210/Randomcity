package com.fullcreative.citypulse.presentation.screens.cityfeed
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fullcreative.citypulse.R
import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.presentation.components.ErrorView
import com.fullcreative.citypulse.presentation.components.LoadingView
import com.fullcreative.citypulse.presentation.components.CityListContent
import com.fullcreative.citypulse.presentation.components.CityMapView

@Composable
fun CityFeedScreen(
    viewModel: CityFeedViewModel = hiltViewModel(),
    onItemClick: (CityEvent) -> Unit,
    isTabletLandscape: Boolean = false,
    selectedCity: CityEvent? = null
) {
    val state by viewModel.cities.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is CityFeedState.Loading -> LoadingView()
            is CityFeedState.Error -> ErrorView(
                message = (state as CityFeedState.Error).message,
                onRetry = { viewModel.refreshData() }
            )
            is CityFeedState.Success -> {
                val cities = (state as CityFeedState.Success).cities

                if (isTabletLandscape) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Master list takes 40% width
                        CityListContent(
                            modifier = Modifier.weight(0.4f),
                            cities = cities,
                            selectedCity = selectedCity,
                            onItemClick = onItemClick
                        )

                        // Actual Map View takes 60% width
                        Box(modifier = Modifier.weight(0.6f)) {
                            selectedCity?.let { city ->
                                CityMapView(
                                    city = city
                                )
                            } ?: Text(
                                text = stringResource(id = R.string.select_city_details),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    CityListContent(
                        modifier = Modifier.fillMaxSize(),
                        cities = cities,
                        selectedCity = selectedCity,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}