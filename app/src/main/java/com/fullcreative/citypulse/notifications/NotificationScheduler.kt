package com.fullcreative.citypulse.notifications

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit
import kotlin.time.toJavaDuration


class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleWelcomeNotification(
        cityId: String,
        cityName: String,
        delayMillis: Long = 5000L
    ) {
        val inputData = Data.Builder()
            .putString("cityName", cityName)
            .putInt("notificationId", cityId.hashCode())
            .build()

        val request = OneTimeWorkRequestBuilder<WelcomeNotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "welcome_notification_$cityId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}

@HiltWorker
class WelcomeNotificationWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val cityName = inputData.getString("cityName") ?: return Result.failure()
            
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext,
                    "Welcome to $cityName",
                    Toast.LENGTH_LONG
                ).show()
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}