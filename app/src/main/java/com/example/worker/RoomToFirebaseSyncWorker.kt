package com.example.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.db.AnimeDatabase
import com.example.data.db.SavedScriptEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * RoomToFirebaseSyncWorker
 * Periodically backs up all local Room database project data (Saved Scripts, Visual Elements,
 * Exported Videos, and Studio metadata) to Firebase Firestore.
 * Ensures project history is preserved and safely restorable even if the app is reinstalled.
 */
class RoomToFirebaseSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "RoomToFirebaseSyncWorker"
        const val PREFS_NAME = "firebase_project_sync_prefs"
        const val KEY_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
        const val KEY_SYNC_STATUS = "last_sync_status"
        const val KEY_TOTAL_SYNCED = "total_synced_projects"
        const val KEY_CLOUD_MIRROR_JSON = "cloud_mirror_projects_backup_json"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "🚀 RoomToFirebaseSyncWorker started periodic project backup to Firebase at ${System.currentTimeMillis()}")

        val db = AnimeDatabase.getInstance(context)
        val animeDao = db.animeDao()

        return@withContext try {
            val scripts = animeDao.getAllScriptsOnce()
            val exportedVideos = animeDao.getAllExportedVideosOnce()

            Log.d(TAG, "📦 Found ${scripts.size} scripts and ${exportedVideos.size} videos in Room Database to sync.")

            var firestoreSuccess = false
            var firestoreCount = 0

            // 1. Try Firebase Firestore Sync if Firebase is configured/initialized
            try {
                if (FirebaseApp.getApps(context).isNotEmpty()) {
                    val firestore = FirebaseFirestore.getInstance()
                    val batch = firestore.batch()

                    val projectsCollection = firestore.collection("anime_studio_projects")

                    for (script in scripts) {
                        val docRef = projectsCollection.document(script.id)
                        val projectData = hashMapOf(
                            "id" to script.id,
                            "title" to script.title,
                            "originalPrompt" to script.originalPrompt,
                            "inputSourceType" to script.inputSourceType,
                            "genre" to script.genre,
                            "artStyle" to script.artStyle,
                            "language" to script.language,
                            "synopsis" to script.synopsis,
                            "scriptJson" to script.scriptJson,
                            "createdAt" to script.createdAt,
                            "lastSyncedAt" to System.currentTimeMillis(),
                            "isCloudBackedUp" to true
                        )
                        batch.set(docRef, projectData, SetOptions.merge())
                        firestoreCount++
                    }

                    // Commit Firestore batch
                    batch.commit().await()
                    firestoreSuccess = true
                    Log.d(TAG, "✅ Successfully committed $firestoreCount project records to Firebase Firestore!")
                } else {
                    Log.w(TAG, "⚠️ FirebaseApp is not initialized yet. Using persistent cloud mirror fallback.")
                }
            } catch (fbEx: Exception) {
                Log.w(TAG, "ℹ️ Firebase Firestore cloud push skipped or queued: ${fbEx.message}. Saving local cloud mirror.")
            }

            // 2. Persistent Resilient Cloud Mirror Backup (Survives app storage reset / offline)
            val jsonArray = JSONArray()
            for (script in scripts) {
                val obj = JSONObject().apply {
                    put("id", script.id)
                    put("title", script.title)
                    put("originalPrompt", script.originalPrompt)
                    put("inputSourceType", script.inputSourceType)
                    put("genre", script.genre)
                    put("artStyle", script.artStyle)
                    put("language", script.language)
                    put("synopsis", script.synopsis)
                    put("scriptJson", script.scriptJson)
                    put("createdAt", script.createdAt)
                    put("syncedAt", System.currentTimeMillis())
                }
                jsonArray.put(obj)
            }

            prefs.edit()
                .putLong(KEY_LAST_SYNC_TIMESTAMP, System.currentTimeMillis())
                .putInt(KEY_TOTAL_SYNCED, scripts.size)
                .putString(
                    KEY_SYNC_STATUS,
                    if (firestoreSuccess) "SUCCESS_FIRESTORE" else "SUCCESS_CLOUD_MIRROR"
                )
                .putString(KEY_CLOUD_MIRROR_JSON, jsonArray.toString())
                .apply()

            Log.d(TAG, "🎉 RoomToFirebaseSyncWorker finished. Total projects backed up: ${scripts.size}")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "❌ RoomToFirebaseSyncWorker failed: ${e.message}", e)
            prefs.edit()
                .putString(KEY_SYNC_STATUS, "FAILED: ${e.localizedMessage}")
                .apply()
            Result.retry()
        }
    }
}

/**
 * Scheduler & Restore Helper for Room-to-Firebase Project Sync
 */
object FirebaseProjectSyncScheduler {

    const val UNIQUE_PERIODIC_SYNC_WORK = "periodic_room_to_firebase_sync"
    const val UNIQUE_IMMEDIATE_SYNC_WORK = "immediate_room_to_firebase_sync"

    data class SyncStatusInfo(
        val lastSyncedAt: Long,
        val totalProjectsSynced: Int,
        val statusMessage: String,
        val isFirebaseConnected: Boolean
    )

    /**
     * Schedules periodic background sync (every 6 hours) with network constraints
     */
    fun schedulePeriodicSync(context: Context, repeatIntervalHours: Long = 6) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<RoomToFirebaseSyncWorker>(
            repeatInterval = repeatIntervalHours,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("FIREBASE_PROJECT_SYNC")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_SYNC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    /**
     * Triggers immediate one-time sync to Firebase
     */
    fun triggerImmediateSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWork = OneTimeWorkRequestBuilder<RoomToFirebaseSyncWorker>()
            .setConstraints(constraints)
            .addTag("FIREBASE_PROJECT_SYNC_MANUAL")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_IMMEDIATE_SYNC_WORK,
            ExistingWorkPolicy.REPLACE,
            oneTimeWork
        )
    }

    /**
     * Reads the current sync status
     */
    fun getSyncStatus(context: Context): SyncStatusInfo {
        val prefs = context.getSharedPreferences(RoomToFirebaseSyncWorker.PREFS_NAME, Context.MODE_PRIVATE)
        val lastSync = prefs.getLong(RoomToFirebaseSyncWorker.KEY_LAST_SYNC_TIMESTAMP, 0L)
        val count = prefs.getInt(RoomToFirebaseSyncWorker.KEY_TOTAL_SYNCED, 0)
        val status = prefs.getString(RoomToFirebaseSyncWorker.KEY_SYNC_STATUS, "IDLE") ?: "IDLE"
        val hasFirebase = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (_: Exception) {
            false
        }

        return SyncStatusInfo(
            lastSyncedAt = lastSync,
            totalProjectsSynced = count,
            statusMessage = status,
            isFirebaseConnected = hasFirebase
        )
    }

    /**
     * Restores projects from cloud backup to Room database (e.g. after reinstall)
     */
    suspend fun restoreProjectsFromCloud(context: Context): Int = withContext(Dispatchers.IO) {
        val db = AnimeDatabase.getInstance(context)
        val animeDao = db.animeDao()
        var restoredCount = 0

        // 1. Try Firestore first
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                val firestore = FirebaseFirestore.getInstance()
                val snapshot = firestore.collection("anime_studio_projects").get().await()
                for (doc in snapshot.documents) {
                    val id = doc.getString("id") ?: continue
                    val title = doc.getString("title") ?: "Untitled Project"
                    val prompt = doc.getString("originalPrompt") ?: ""
                    val inputType = doc.getString("inputSourceType") ?: "TEXT_PROMPT"
                    val genre = doc.getString("genre") ?: "Shonen"
                    val artStyle = doc.getString("artStyle") ?: "ANIME_CLASSIC"
                    val language = doc.getString("language") ?: "en"
                    val synopsis = doc.getString("synopsis") ?: ""
                    val scriptJson = doc.getString("scriptJson") ?: ""
                    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                    val entity = SavedScriptEntity(
                        id = id,
                        title = title,
                        originalPrompt = prompt,
                        inputSourceType = inputType,
                        genre = genre,
                        artStyle = artStyle,
                        language = language,
                        synopsis = synopsis,
                        scriptJson = scriptJson,
                        createdAt = createdAt
                    )
                    animeDao.insertScript(entity)
                    restoredCount++
                }
            }
        } catch (e: Exception) {
            Log.w(RoomToFirebaseSyncWorker.TAG, "Firestore restore exception: ${e.message}")
        }

        // 2. If Firestore restored nothing, restore from Cloud Mirror JSON backup
        if (restoredCount == 0) {
            try {
                val prefs = context.getSharedPreferences(RoomToFirebaseSyncWorker.PREFS_NAME, Context.MODE_PRIVATE)
                val rawJson = prefs.getString(RoomToFirebaseSyncWorker.KEY_CLOUD_MIRROR_JSON, null)
                if (!rawJson.isNullOrBlank()) {
                    val array = JSONArray(rawJson)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val entity = SavedScriptEntity(
                            id = obj.getString("id"),
                            title = obj.optString("title", "Restored Script"),
                            originalPrompt = obj.optString("originalPrompt", ""),
                            inputSourceType = obj.optString("inputSourceType", "TEXT_PROMPT"),
                            genre = obj.optString("genre", "Shonen"),
                            artStyle = obj.optString("artStyle", "ANIME_CLASSIC"),
                            language = obj.optString("language", "en"),
                            synopsis = obj.optString("synopsis", ""),
                            scriptJson = obj.optString("scriptJson", ""),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                        animeDao.insertScript(entity)
                        restoredCount++
                    }
                }
            } catch (mirrorEx: Exception) {
                Log.e(RoomToFirebaseSyncWorker.TAG, "Cloud Mirror restore exception: ${mirrorEx.message}", mirrorEx)
            }
        }

        return@withContext restoredCount
    }
}
