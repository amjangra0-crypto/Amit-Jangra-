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
}
