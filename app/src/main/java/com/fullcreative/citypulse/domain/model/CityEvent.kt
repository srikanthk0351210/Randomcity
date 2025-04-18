package com.fullcreative.citypulse.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CityEvent(
    val id: Int,
    val name: String,
    val color: String,
    val timestamp: Long = System.currentTimeMillis()
): Parcelable