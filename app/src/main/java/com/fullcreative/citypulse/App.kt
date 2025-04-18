package com.fullcreative.citypulse

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RandomCityApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}