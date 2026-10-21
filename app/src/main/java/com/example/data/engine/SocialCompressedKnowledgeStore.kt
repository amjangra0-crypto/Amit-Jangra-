package com.example.data.engine

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

data class LearnedSocialKnowledgeNode(
    val id: String,
    val topicOrCommand: String,
    val sourcePlatform: String, // "Google", "Instagram Reels", "YouTube", "TikTok", "AnimeWiki", "X/Twitter"
    val narrativeGenre: String, // "Shonen Action", "Cyberpunk", "Dark Fantasy", "Slice of Life", "Isekai", "Webseries Drama"
    val visualArtDirection: String, // Cinematography, camera motions, dynamic lighting cues
    val editingTechnique: String, // Pace, transitions, color grading, VFX
    val soundAndBgmDesign: String, // Recommended voice mood, background track cues
    val viralHookPattern: String, // Pacing hook for the first 3 seconds
    val learnedTimestamp: Long = System.currentTimeMillis(),
    val timesReusedInCreations: Int = 0
)

data class KnowledgeStorageStats(
    val totalNodesCount: Int = 0,
    val uncompressedBytes: Long = 0L,
    val compressedBytes: Long = 0L,
    val compressionRatioPercent: Int = 0,
    val localFilePath: String = ""
)

/**
 * SocialCompressedKnowledgeStore
 * Manages the self-learning knowledge bank gathered from Google, Instagram, and social media.
 * Stores all acquired rules and cinematography wisdom on the user's LOCAL DEVICE STORAGE
 * using GZIP byte compression to eliminate storage bloat (< 20 KB footprint).
 *
 * When creating or editing subsequent anime videos, webseries, or movies, this stored
 * knowledge is retrieved and used to autonomously enhance visualizations, camera beats,
 * and dialogue direction.
 */
class SocialCompressedKnowledgeStore private constructor(private val context: Context) {

    private val vaultDirectory = File(context.filesDir, "anime_knowledge_vault").apply { mkdirs() }
    private val compressedFile = File(vaultDirectory, "social_knowledge_compressed.bin.gz")

    private val _knowledgeNodes = MutableStateFlow<List<LearnedSocialKnowledgeNode>>(emptyList())
    val knowledgeNodes: StateFlow<List<LearnedSocialKnowledgeNode>> = _knowledgeNodes.asStateFlow()

    private val _storageStats = MutableStateFlow(KnowledgeStorageStats())
    val storageStats: StateFlow<KnowledgeStorageStats> = _storageStats.asStateFlow()

    companion object {
        private const val TAG = "SocialKnowledgeStore"

        @Volatile
        private var instance: SocialCompressedKnowledgeStore? = null

        fun getInstance(context: Context): SocialCompressedKnowledgeStore {
            return instance ?: synchronized(this) {
                instance ?: SocialCompressedKnowledgeStore(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        loadKnowledgeFromCompressedLocalFile()
    }

    /**
     * Loads and decompresses knowledge from the local device storage.
     */
    fun loadKnowledgeFromCompressedLocalFile() {
        try {
            if (!compressedFile.exists() || compressedFile.length() == 0L) {
                val seedNodes = createInitialSocialKnowledgeSeeds()
                saveKnowledgeToCompressedFile(seedNodes)
                return
            }

            val compressedBytes = compressedFile.readBytes()
            val decompressedBytes = decompressGzip(compressedBytes)
            val jsonString = String(decompressedBytes, Charsets.UTF_8)
            val parsedNodes = parseNodesFromJson(jsonString)

            _knowledgeNodes.value = parsedNodes

            val savedRatio = if (decompressedBytes.isNotEmpty()) {
                (((decompressedBytes.size - compressedBytes.size).toDouble() / decompressedBytes.size) * 100).toInt().coerceAtLeast(0)
            } else 0

            _storageStats.value = KnowledgeStorageStats(
                totalNodesCount = parsedNodes.size,
                uncompressedBytes = decompressedBytes.size.toLong(),
                compressedBytes = compressedBytes.size.toLong(),
                compressionRatioPercent = savedRatio,
                localFilePath = compressedFile.absolutePath
            )
            Log.d(TAG, "Loaded ${parsedNodes.size} learned knowledge nodes (Compressed size: ${compressedBytes.size} bytes, Savings: $savedRatio%)")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading compressed knowledge, bootstrapping defaults", e)
            val seedNodes = createInitialSocialKnowledgeSeeds()
            saveKnowledgeToCompressedFile(seedNodes)
        }
    }

    /**
     * Learns a new insight from a command or social platform and appends it to the compressed vault.
     */
    suspend fun learnAndCompressInsight(
        commandOrTopic: String,
        platformSource: String,
        genre: String,
        visualArtDirection: String,
        editingTechnique: String,
        soundDesign: String,
        viralHook: String
    ): LearnedSocialKnowledgeNode = withContext(Dispatchers.IO) {
        val newNode = LearnedSocialKnowledgeNode(
            id = "kn_${System.currentTimeMillis()}_${(100..999).random()}",
            topicOrCommand = commandOrTopic,
            sourcePlatform = platformSource,
            narrativeGenre = genre,
            visualArtDirection = visualArtDirection,
            editingTechnique = editingTechnique,
            soundAndBgmDesign = soundDesign,
            viralHookPattern = viralHook,
            learnedTimestamp = System.currentTimeMillis(),
            timesReusedInCreations = 0
        )

        val currentList = _knowledgeNodes.value.toMutableList()
        // Replace if already exists with same topic, or prepend
        val existingIndex = currentList.indexOfFirst { it.topicOrCommand.equals(commandOrTopic, ignoreCase = true) }
        if (existingIndex >= 0) {
            currentList[existingIndex] = newNode
        } else {
            currentList.add(0, newNode)
        }

        saveKnowledgeToCompressedFile(currentList)
        newNode
    }

    /**
     * Retrieves the best matching learned knowledge nodes to improve a new anime/video/webseries command.
     */
    fun findApplicableKnowledge(prompt: String): List<LearnedSocialKnowledgeNode> {
        val lower = prompt.lowercase()
        val all = _knowledgeNodes.value
        val matched = all.filter { node ->
            lower.contains(node.topicOrCommand.lowercase()) ||
            lower.contains(node.narrativeGenre.lowercase()) ||
            lower.contains(node.sourcePlatform.lowercase()) ||
            node.topicOrCommand.lowercase().split(" ").any { word -> word.length > 3 && lower.contains(word) }
        }

        return if (matched.isNotEmpty()) matched else all.take(3)
    }

    /**
     * Records that an insight was reused in generating or editing a video/webseries/movie.
     */
    fun recordKnowledgeReused(nodeId: String) {
        val list = _knowledgeNodes.value.map {
            if (it.id == nodeId) it.copy(timesReusedInCreations = it.timesReusedInCreations + 1) else it
        }
        _knowledgeNodes.value = list
        saveKnowledgeToCompressedFile(list)
    }

    /**
     * Serializes knowledge nodes into JSON, compresses via GZIP, and writes to local disk.
     */
    private fun saveKnowledgeToCompressedFile(nodes: List<LearnedSocialKnowledgeNode>) {
        try {
            val jsonArray = JSONArray()
            for (n in nodes) {
                val obj = JSONObject()
                obj.put("id", n.id)
                obj.put("topic", n.topicOrCommand)
                obj.put("platform", n.sourcePlatform)
                obj.put("genre", n.narrativeGenre)
                obj.put("visual", n.visualArtDirection)
                obj.put("editing", n.editingTechnique)
                obj.put("sound", n.soundAndBgmDesign)
                obj.put("hook", n.viralHookPattern)
                obj.put("timestamp", n.learnedTimestamp)
                obj.put("reused", n.timesReusedInCreations)
                jsonArray.put(obj)
            }

            val uncompressedString = jsonArray.toString()
            val uncompressedBytes = uncompressedString.toByteArray(Charsets.UTF_8)
            val compressedBytes = compressGzip(uncompressedBytes)

            FileOutputStream(compressedFile).use { fos ->
                fos.write(compressedBytes)
                fos.flush()
            }

            _knowledgeNodes.value = nodes

            val savings = if (uncompressedBytes.isNotEmpty()) {
                (((uncompressedBytes.size - compressedBytes.size).toDouble() / uncompressedBytes.size) * 100).toInt().coerceAtLeast(0)
            } else 0

            _storageStats.value = KnowledgeStorageStats(
                totalNodesCount = nodes.size,
                uncompressedBytes = uncompressedBytes.size.toLong(),
                compressedBytes = compressedBytes.size.toLong(),
                compressionRatioPercent = savings,
                localFilePath = compressedFile.absolutePath
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to compress and save knowledge", e)
        }
    }

    private fun compressGzip(data: ByteArray): ByteArray {
        val byteStream = ByteArrayOutputStream()
        GZIPOutputStream(byteStream).use { gzip ->
            gzip.write(data)
            gzip.finish()
        }
        return byteStream.toByteArray()
    }

    private fun decompressGzip(data: ByteArray): ByteArray {
        val byteStream = ByteArrayInputStream(data)
        val outStream = ByteArrayOutputStream()
        GZIPInputStream(byteStream).use { gzip ->
            val buffer = ByteArray(1024)
            var len: Int
            while (gzip.read(buffer).also { len = it } > 0) {
                outStream.write(buffer, 0, len)
            }
        }
        return outStream.toByteArray()
    }

    private fun parseNodesFromJson(json: String): List<LearnedSocialKnowledgeNode> {
        val arr = JSONArray(json)
        val list = mutableListOf<LearnedSocialKnowledgeNode>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                LearnedSocialKnowledgeNode(
                    id = obj.getString("id"),
                    topicOrCommand = obj.getString("topic"),
                    sourcePlatform = obj.getString("platform"),
                    narrativeGenre = obj.getString("genre"),
                    visualArtDirection = obj.getString("visual"),
                    editingTechnique = obj.getString("editing"),
                    soundAndBgmDesign = obj.getString("sound"),
                    viralHookPattern = obj.getString("hook"),
                    learnedTimestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    timesReusedInCreations = obj.optInt("reused", 0)
                )
            )
        }
        return list
    }

    private fun createInitialSocialKnowledgeSeeds(): List<LearnedSocialKnowledgeNode> {
        return listOf(
            LearnedSocialKnowledgeNode(
                id = "kn_ig_reels_cinematic",
                topicOrCommand = "Instagram Reels Dynamic Anime Transitions",
                sourcePlatform = "Instagram Reels",
                narrativeGenre = "Viral Shonen Reels",
                visualArtDirection = "Fast 180-degree camera spin, neon chromatic aberration, high-contrast Rim Lighting with cherry blossom embers",
                editingTechnique = "Beat-drop whip pans every 1.5 seconds, keyframed zoom on character eye glance",
                soundAndBgmDesign = "Heavy 808 sub-bass hit paired with Japanese Shamisen & futuristic synth riser",
                viralHookPattern = "Immediate freeze-frame character quote in the first 0.8 seconds to stop scroll"
            ),
            LearnedSocialKnowledgeNode(
                id = "kn_google_webseries_pacing",
                topicOrCommand = "Google Search Top Trending Webseries Pacing",
                sourcePlatform = "Google Trends",
                narrativeGenre = "Dark Fantasy Webseries",
                visualArtDirection = "Atmospheric volumetric fog, dual-tone lighting (Cyber Cyan vs Crimson Red), low-angle hero framing",
                editingTechnique = "Slow-burn suspense intro transitioning into explosive 60fps sakuga combat sequence",
                soundAndBgmDesign = "Orchestral choir crescendo transitioning into solitary breath audio sync",
                viralHookPattern = "Dilemma question posed directly by protagonist facing the screen"
            ),
            LearnedSocialKnowledgeNode(
                id = "kn_youtube_shorts_retention",
                topicOrCommand = "YouTube Viral Anime Shorts Pacing",
                sourcePlatform = "YouTube Viral",
                narrativeGenre = "Cyberpunk Samurai",
                visualArtDirection = "Reflective rain puddles, holographic billboards, dynamic blade trail motion blur",
                editingTechnique = "Match-cut on blade swing, sudden speed-ramp slow motion during sword clash",
                soundAndBgmDesign = "Metallic katana reverberation with low-frequency heartbeat pulse",
                viralHookPattern = "Mystery reveal of character's true identity in scene 1"
            ),
            LearnedSocialKnowledgeNode(
                id = "kn_tiktok_viral_color_palette",
                topicOrCommand = "TikTok Trending Aesthetic Color Grading",
                sourcePlatform = "TikTok",
                narrativeGenre = "Isekai Fantasy",
                visualArtDirection = "Warm golden hour sunlight, pastel sakura skies, soft bloom filter on character eyes",
                editingTechnique = "Smooth cross-dissolve with floating particles overlay",
                soundAndBgmDesign = "Melodic Lo-Fi anime piano with subtle vinyl crackle and soothing female voiceover",
                viralHookPattern = "Relatable internal monologue with subtitle pop animation"
            )
        )
    }
}
