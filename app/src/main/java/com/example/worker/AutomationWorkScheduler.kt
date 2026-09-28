package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * WorkManager Background Job Scheduler for Daily Automated Anime Generation & Upload
 * Allows users to set any daily time (e.g. 09:00, 18:00, 21:00) with automatic 24-hour cycle.
 */
object AutomationWorkScheduler {

    private const val TAG = "AutomationWorkScheduler"
    const val UNIQUE_PERIODIC_WORK_NAME = "daily_anime_series_worker"
    const val UNIQUE_IMMEDIATE_WORK_NAME = "immediate_anime_series_worker"

    /**
     * Schedules a daily recurring WorkManager task at target hour and minute
     */
    fun scheduleDailyAutomation(
        context: Context,
        targetHour: Int,
        targetMinute: Int,
        episodesCount: Int = 1
    ) {
        val workManager = WorkManager.getInstance(context)

        val initialDelayMillis = calculateInitialDelayMillis(targetHour, targetMinute)
        val initialDelayMinutes = TimeUnit.MILLISECONDS.toMinutes(initialDelayMillis)

        Log.d(
            TAG,
            "Scheduling daily WorkManager task at %02d:%02d (initial delay: %d minutes)".format(
                targetHour,
                targetMinute,
                initialDelayMinutes
            )
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyAutomationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag("DAILY_ANIME_AUTOMATION")
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    /**
     * Instantly triggers a 1-time run of the automation worker in background
     */
    fun triggerImmediateRun(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val oneTimeWork = OneTimeWorkRequestBuilder<DailyAutomationWorker>()
            .addTag("IMMEDIATE_ANIME_AUTOMATION")
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWork
        )
    }

    /**
     * Cancels the active daily WorkManager schedule
     */
    fun cancelDailyAutomation(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
        Log.d(TAG, "Cancelled unique periodic work: $UNIQUE_PERIODIC_WORK_NAME")
    }

    /**
     * Calculates the millisecond delay until the next target hour and minute
     */
    fun calculateInitialDelayMillis(targetHour: Int, targetMinute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time already passed today, advance to tomorrow
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }

    /**
     * Returns human-friendly remaining time text
     */
    fun getFormattedDelayRemaining(targetHour: Int, targetMinute: Int): String {
        val delayMillis = calculateInitialDelayMillis(targetHour, targetMinute)
        val hours = TimeUnit.MILLISECONDS.toHours(delayMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(delayMillis) % 60
        return if (hours > 0) {
            "${hours} घंटे ${minutes} मिनट बाद"
        } else {
            "${minutes} मिनट बाद"
        }
    }
}
