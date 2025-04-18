import android.content.Context
import androidx.work.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import java.util.concurrent.TimeUnit


class NotificationSchedulerTest {

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var scheduler: NotificationScheduler

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        workManager = mock(WorkManager::class.java)

        scheduler = NotificationScheduler(context, workManager)
    }

    @Test
    fun `scheduleWelcomeNotification enqueues work with correct data`() {
        // Arrange
        val cityId = "123"
        val cityName = "New York"
        val delayMillis = 3000L


        scheduler.scheduleWelcomeNotification(cityId, cityName, delayMillis)

        verify(workManager).enqueueUniqueWork(
            eq("welcome_notification_123"),
            eq(ExistingWorkPolicy.REPLACE),
            any<OneTimeWorkRequest>()
        )
    }
}

class NotificationScheduler(private val context: Context, private val workManager: WorkManager) {

    fun scheduleWelcomeNotification(cityId: String, cityName: String, delayMillis: Long = 5000L) {
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

class WelcomeNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return Result.success()
    }
}
