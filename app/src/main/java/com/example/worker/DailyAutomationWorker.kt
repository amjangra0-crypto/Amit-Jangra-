package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.engine.AutomationChannelManager

/**
 * WorkManager-based Background Worker for Daily Anime Content Generation & YouTube Publishing
 * Runs automatically at the user's scheduled daily time in the background.
 */
class DailyAutomationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "DailyAutomationWorker"
        const val NOTIFICATION_CHANNEL_ID = "anime_daily_automation_channel"
        const val NOTIFICATION_ID = 2099
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "⚡ DailyAutomationWorker started background task at ${System.currentTimeMillis()}")

        return try {
            val channelManager = AutomationChannelManager.getInstance(context)
            val schedule = channelManager.webSeriesSchedule.value
            val channel = channelManager.connectedChannel.value

            // Step 1: Check and auto-publish any pending reviews that passed scheduled deadline
            channelManager.checkAndAutoPublishExpiredReviews()

            // Step 2: Trigger daily episode generation based on user's daily command count
            val episodesToGenerate = schedule.episodesPerDay.coerceIn(1, 3)
            channelManager.triggerDailyEpisodeGeneration(episodesToGenerate)

            // Step 3: Send Local Android Notification to notify user for Review / Green Signal
            sendEpisodeReadyNotification(
                title = "🔔 आज का एनिमे एपिसोड तैयार है! (${schedule.seriesTitle})",
                message = "प्रतिदिन कमांड अनुसार $episodesToGenerate एपिसोड तैयार। YouTube (${channel.channelHandle}) पर अपलोड से पहले रिव्यू करें और ग्रीन सिग्नल (OK) दें।"
            )

            Log.d(TAG, "✅ DailyAutomationWorker completed successfully. Generated $episodesToGenerate episodes.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "❌ DailyAutomationWorker failed with error: ${e.message}", e)
            Result.retry()
        }
    }

    private fun sendEpisodeReadyNotification(title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Anime Daily Automation & Uploads",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when daily anime episodes are generated and ready for YouTube upload review"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
