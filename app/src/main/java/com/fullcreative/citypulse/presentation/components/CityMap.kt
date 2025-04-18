package com.fullcreative.citypulse.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fullcreative.citypulse.domain.model.CityEvent
import com.fullcreative.citypulse.utils.fetchCoordinatesForCity
import com.fullcreative.citypulse.utils.formatTimestamp
import com.fullcreative.citypulse.utils.getMarkerHue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@Composable
fun CityMapView(city: CityEvent) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isInSplitScreen = configuration.screenWidthDp < 600

    var cityLocation by rememberSaveable { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()


    LaunchedEffect(key1 = city.name + isInSplitScreen) {
        cityLocation = null
        cityLocation = fetchCoordinatesForCity(context, city.name)
    }


    LaunchedEffect(cityLocation) {
        cityLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 12f))
        }
    }

    if (cityLocation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            cityLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = city.name,
                    snippet = "Visited at ${formatTimestamp(city.timestamp)}",
                    icon = BitmapDescriptorFactory.defaultMarker(getMarkerHue(city.color))
                )
            }
        }
    }
}