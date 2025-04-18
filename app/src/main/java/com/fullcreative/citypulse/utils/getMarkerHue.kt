package com.fullcreative.citypulse.utils

import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getMarkerHue(color: String): Float {
    return when (color.lowercase()) {
        "yellow" -> BitmapDescriptorFactory.HUE_YELLOW
        "white" -> BitmapDescriptorFactory.HUE_AZURE
        "green" -> BitmapDescriptorFactory.HUE_GREEN
        "blue" -> BitmapDescriptorFactory.HUE_BLUE
        "red" -> BitmapDescriptorFactory.HUE_RED
        "black" -> BitmapDescriptorFactory.HUE_VIOLET
        else -> BitmapDescriptorFactory.HUE_ORANGE
    }
}
