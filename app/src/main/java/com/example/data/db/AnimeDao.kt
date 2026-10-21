package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM saved_scripts ORDER BY createdAt DESC")
    fun getAllScripts(): Flow<List<SavedScriptEntity>>

    @Query("SELECT * FROM saved_scripts ORDER BY createdAt DESC")
    suspend fun getAllScriptsOnce(): List<SavedScriptEntity>

    @Query("SELECT * FROM saved_scripts WHERE id = :id LIMIT 1")
    suspend fun getScriptById(id: String): SavedScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: SavedScriptEntity)

    @Query("DELETE FROM saved_scripts WHERE id = :id")
    suspend fun deleteScriptById(id: String)

    @Query("SELECT * FROM custom_characters ORDER BY name ASC")
    fun getAllCharacters(): Flow<List<CustomCharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CustomCharacterEntity)

    @Query("DELETE FROM custom_characters WHERE id = :id")
    suspend fun deleteCharacterById(id: String)

    @Query("SELECT * FROM saved_visual_elements ORDER BY createdAt DESC")
    fun getAllVisualElements(): Flow<List<SavedVisualElementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisualElement(element: SavedVisualElementEntity)

    @Query("DELETE FROM saved_visual_elements WHERE id = :id")
    suspend fun deleteVisualElementById(id: String)

    @Query("SELECT * FROM admin_access_settings WHERE id = 1")
    fun getAdminSettings(): Flow<AdminAccessEntity?>

    @Query("SELECT * FROM admin_access_settings WHERE id = 1")
    suspend fun getAdminSettingsOnce(): AdminAccessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAdminSettings(settings: AdminAccessEntity)

    @Query("SELECT * FROM exported_videos ORDER BY createdAt DESC")
    fun getAllExportedVideos(): Flow<List<ExportedVideoEntity>>

    @Query("SELECT * FROM exported_videos ORDER BY createdAt DESC")
    suspend fun getAllExportedVideosOnce(): List<ExportedVideoEntity>

    @Query("SELECT * FROM exported_videos WHERE id = :id LIMIT 1")
    suspend fun getExportedVideoById(id: String): ExportedVideoEntity?

    @Query("SELECT * FROM exported_videos WHERE scriptId = :scriptId LIMIT 1")
    suspend fun getExportedVideoByScriptId(scriptId: String): ExportedVideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExportedVideo(video: ExportedVideoEntity)

    @Query("DELETE FROM exported_videos WHERE id = :id")
    suspend fun deleteExportedVideoById(id: String)

    // --- Project Metadata & File Links (Script history, character configs, audio/video links) ---
    @Query("SELECT * FROM project_metadata ORDER BY updatedAt DESC")
    fun getAllProjectMetadata(): Flow<List<ProjectMetadataEntity>>

    @Query("SELECT * FROM project_metadata ORDER BY updatedAt DESC")
    suspend fun getAllProjectMetadataOnce(): List<ProjectMetadataEntity>

    @Query("SELECT * FROM project_metadata WHERE id = :id LIMIT 1")
    suspend fun getProjectMetadataById(id: String): ProjectMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectMetadata(project: ProjectMetadataEntity)

    @Query("DELETE FROM project_metadata WHERE id = :id")
    suspend fun deleteProjectMetadataById(id: String)

    @Query("UPDATE project_metadata SET videoFilePath = :videoPath, audioFilePath = :audioPath, srtSubtitlePath = :srtPath, isExported = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateProjectMediaLinks(id: String, videoPath: String, audioPath: String, srtPath: String, updatedAt: Long = System.currentTimeMillis())

    // --- FAQ Support Incidents & Owner Notifications ---
    @Query("SELECT * FROM faq_support_incidents ORDER BY timestamp DESC")
    fun getAllFaqIncidents(): Flow<List<FaqSupportIncidentEntity>>

    @Query("SELECT * FROM faq_support_incidents WHERE isBugReport = 1 ORDER BY timestamp DESC")
    fun getBugNotificationsForOwner(): Flow<List<FaqSupportIncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaqIncident(incident: FaqSupportIncidentEntity)

    @Query("DELETE FROM faq_support_incidents WHERE id = :id")
    suspend fun deleteFaqIncident(id: String)
}
