package com.test.myapplication

import android.content.Context
import androidx.work.*
import com.test.myapplication.data.repository.BibleRepository
import com.test.myapplication.data.local.MvmSettings
import com.test.myapplication.data.local.BibleDatabaseProvider
import com.test.myapplication.data.local.getDatabaseBuilder
import java.util.*
import java.util.concurrent.TimeUnit

class VerseWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        BibleRepository.initializeDatabase(BibleDatabaseProvider.getDatabase(getDatabaseBuilder(applicationContext)))
        val todayVerse = BibleRepository.getVerseOfTheDay(false)
        
        if (todayVerse != null) {
            MvmSettings.saveDailyVerse(
                todayVerse.text,
                todayVerse.reference,
                System.currentTimeMillis()
            )

            NotificationHelper.showVerseNotification(
                applicationContext,
                todayVerse.text,
                todayVerse.reference
            )
        }

        scheduleNextWorker(applicationContext)
        return Result.success()
    }

    companion object {
        fun scheduleNextWorker(context: Context) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 6)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val delay = calendar.timeInMillis - System.currentTimeMillis()

            val workRequest = OneTimeWorkRequestBuilder<VerseWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "DailyVerseWork",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
