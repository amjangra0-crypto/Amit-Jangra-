package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

/**
 * System push notifications for:
 * 1. High-potential 'Viral' trend identification (alerts owner for 1-click video creation).
 * 2. Automated publishing status and Owner Verification permission requests.
 */
object NotificationHelper {

    private const val TAG = "NotificationHelper"
    const val CHANNEL_VIRAL_TRENDS = "channel_viral_trends"
    const val CHANNEL_AUTOMATION = "channel_automation_uploads"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val viralChannel = NotificationChannel(
                CHANNEL_VIRAL_TRENDS,
                "Viral Trend Alerts (AI Agent)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts the owner when AI Research Hub detects high-potential viral social trends"
                enableVibration(true)
            }

            val autoChannel = NotificationChannel(
                CHANNEL_AUTOMATION,
                "Multi-Platform Publishing & Verification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts for YouTube, Instagram & Facebook automated uploads and owner review approvals"
            }

            notificationManager.createNotificationChannel(viralChannel)
            notificationManager.createNotificationChannel(autoChannel)
        }
    }

    fun sendViralTrendAlert(
        context: Context,
        topicTitle: String,
        viralityScore: Int,
        prompt: String
    ) {
        try {
            initNotificationChannels(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(TAG, "Notification permission not granted yet")
                    return
                }
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "open_viral_studio")
                putExtra("viral_topic", topicTitle)
                putExtra("viral_prompt", prompt)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_VIRAL_TRENDS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🔥 Viral Trend Alert ($viralityScore% Score): $topicTitle")
                .setContentText("Tap to trigger 1-Click Video Generation in Studio!")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("AI Agent discovered a high-potential trend '$topicTitle' ($viralityScore% Virality Score).\n\nTap to launch 1-Click Video Generation with auto-synced character voice & visual storyboard!")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(
                    android.R.drawable.ic_media_play,
                    "🎬 Generate 1-Click Video",
                    pendingIntent
                )
                .build()

            NotificationManagerCompat.from(context).notify(topicTitle.hashCode(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send viral trend notification: ${e.message}")
        }
    }

    fun sendUploadAlert(
        context: Context,
        episodeTitle: String,
        channelName: String,
        platformName: String,
        requiresOwnerVerification: Boolean
    ) {
        try {
            initNotificationChannels(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("action", "open_automation_hub")
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = if (requiresOwnerVerification) {
                "⏳ Permission Needed: Verify Video before Upload"
            } else {
                "🚀 Auto-Uploaded: $episodeTitle"
            }

            val body = if (requiresOwnerVerification) {
                "'$episodeTitle' is ready for $platformName ($channelName). Tap to review and give Green Signal before upload."
            } else {
                "Successfully uploaded directly to $platformName ($channelName) according to scheduled AI pipeline!"
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_AUTOMATION)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            NotificationManagerCompat.from(context).notify(episodeTitle.hashCode(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send upload notification: ${e.message}")
        }
    }
}
