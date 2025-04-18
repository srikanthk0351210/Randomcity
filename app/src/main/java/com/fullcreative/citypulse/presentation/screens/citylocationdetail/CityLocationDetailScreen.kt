package com.fullcreative.citypulse.presentation.screens.citylocationdetail


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fullcreative.citypulse.R
import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.presentation.components.CityMapView
import com.fullcreative.citypulse.presentation.components.ErrorView
import com.fullcreative.citypulse.presentation.components.LoadingView
import com.fullcreative.citypulse.utils.fetchCoordinatesForCity
import com.fullcreative.citypulse.utils.formatTimestamp

import com.fullcreative.citypulse.utils.getMarkerHue
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    city: CityEvent?,
    onBackClick: () -> Unit,
    viewModel: CityLocationDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(city) {
        city?.let { viewModel.loadCityFromArgument(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = (state as? CityLocationDetailUiState.Success)?.city?.name
                            ?: stringResource(id = R.string.city_details),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = (state as? CityLocationDetailUiState.Success)
                        ?.city?.color?.let { Color(android.graphics.Color.parseColor(it)) }
                        ?: MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (state) {
                is CityLocationDetailUiState.Loading -> LoadingView()
                is CityLocationDetailUiState.Error -> ErrorView(
                    message = (state as CityLocationDetailUiState.Error).message,
                    onRetry = {  }
                )
                is CityLocationDetailUiState.Success -> {
                    val cityData = (state as CityLocationDetailUiState.Success).city
                    CityMapView(
                        city = cityData
                    )
                }
            }
        }
    }
}
