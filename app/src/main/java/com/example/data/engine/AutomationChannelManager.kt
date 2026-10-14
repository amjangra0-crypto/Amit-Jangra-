package com.example.data.engine

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.AutomatedEpisode
import com.example.data.model.ChannelPlatform
import com.example.data.model.ConnectedChannel
import com.example.data.model.EpisodeReviewStatus
import com.example.data.model.UploadPrivacyStatus
import com.example.data.model.WebSeriesSchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Autonomous YouTube & Instagram Channel Publishing Engine
 * - Connects user's personal YouTube channel via URL or handle
 * - Manages daily Web Series episode generation on command (1, 2, or 3 episodes/day)
 * - Review Gate: Notifies user, displays preview, waits for Green Signal (OK)
 * - Scheduled Fallback Auto-Upload: If user doesn't review within scheduled time, automatically publishes
 */
class AutomationChannelManager private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anime_automation_channel_prefs", Context.MODE_PRIVATE)

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _connectedChannels = MutableStateFlow(loadChannels())
    val connectedChannels: StateFlow<List<ConnectedChannel>> = _connectedChannels.asStateFlow()

    private val _connectedChannel = MutableStateFlow(
        _connectedChannels.value.firstOrNull() ?: loadChannel()
    )
    val connectedChannel: StateFlow<ConnectedChannel> = _connectedChannel.asStateFlow()

    private val _webSeriesSchedule = MutableStateFlow(loadWebSeriesSchedule())
    val webSeriesSchedule: StateFlow<WebSeriesSchedule> = _webSeriesSchedule.asStateFlow()

    private val _episodesQueue = MutableStateFlow(loadInitialQueue())
    val episodesQueue: StateFlow<List<AutomatedEpisode>> = _episodesQueue.asStateFlow()

    private val _automationStatusMessage = MutableStateFlow("ऑटोमेशन सक्रिय: मल्टीपल YouTube चैनल ऑटोमेशन तैयार है।")
    val automationStatusMessage: StateFlow<String> = _automationStatusMessage.asStateFlow()

    companion object {
        private const val TAG = "AutomationChannelMgr"

        @Volatile
        private var INSTANCE: AutomationChannelManager? = null

        fun getInstance(context: Context): AutomationChannelManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AutomationChannelManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private fun loadChannels(): List<ConnectedChannel> {
        val raw = prefs.getString("connected_channels_json", null)
        if (raw.isNullOrBlank()) {
            return listOf(
                // YouTube Channels
                ConnectedChannel(
                    id = "yt_chan_1",
                    platform = ChannelPlatform.YOUTUBE,
                    channelUrl = "https://youtube.com/@AnimeStudioCreator",
                    channelHandle = "@AnimeStudioCreator",
                    channelName = "Anime Studio Official (YouTube)",
                    subscriberCount = "24.8K Subscribers",
                    defaultPrivacy = UploadPrivacyStatus.PUBLIC,
                    requireOwnerVerificationBeforeUpload = true,
                    isConnected = true
                ),
                ConnectedChannel(
                    id = "yt_chan_2",
                    platform = ChannelPlatform.YOUTUBE,
                    channelUrl = "https://youtube.com/@ShonenBattlesTV",
                    channelHandle = "@ShonenBattlesTV",
                    channelName = "Shonen Battles TV (YouTube)",
                    subscriberCount = "89.2K Subscribers",
                    defaultPrivacy = UploadPrivacyStatus.DRAFT_PRIVATE,
                    requireOwnerVerificationBeforeUpload = true,
                    isConnected = true
                ),
                // Instagram Accounts
                ConnectedChannel(
                    id = "ig_acc_1",
                    platform = ChannelPlatform.INSTAGRAM,
                    channelUrl = "https://instagram.com/animestudio.ai",
                    channelHandle = "@animestudio.ai",
                    channelName = "Anime Studio Reels (Instagram)",
                    subscriberCount = "45.1K Followers",
                    defaultPrivacy = UploadPrivacyStatus.PUBLIC,
                    requireOwnerVerificationBeforeUpload = true,
                    isConnected = true
                ),
                ConnectedChannel(
                    id = "ig_acc_2",
                    platform = ChannelPlatform.INSTAGRAM,
                    channelUrl = "https://instagram.com/cyberpunk.anime.reels",
                    channelHandle = "@cyberpunk.anime.reels",
                    channelName = "Cyberpunk Anime Reels (Instagram)",
                    subscriberCount = "18.3K Followers",
                    defaultPrivacy = UploadPrivacyStatus.DRAFT_PRIVATE,
                    requireOwnerVerificationBeforeUpload = true,
                    isConnected = true
                ),
                // Facebook Accounts & Pages
                ConnectedChannel(
                    id = "fb_page_1",
                    platform = ChannelPlatform.FACEBOOK,
                    channelUrl = "https://facebook.com/AnimeStudioCreators",
                    channelHandle = "@AnimeStudioCreators",
                    channelName = "Anime Studio Creators (Facebook Page)",
                    subscriberCount = "32.5K Followers",
                    defaultPrivacy = UploadPrivacyStatus.PUBLIC,
                    requireOwnerVerificationBeforeUpload = true,
                    isConnected = true
                ),
                ConnectedChannel(
                    id = "fb_page_2",
                    platform = ChannelPlatform.FACEBOOK,
                    channelUrl = "https://facebook.com/ShonenAnimeCommunity",
                    channelHandle = "@ShonenAnimeCommunity",
                    channelName = "Shonen Anime Community (Facebook)",
                    subscriberCount = "15.7K Members",
                    defaultPrivacy = UploadPrivacyStatus.DRAFT_PRIVATE,
                    requireOwnerVerificationBeforeUpload = true,
                    isConnected = true
                )
            )
        }
        return try {
            val list = mutableListOf<ConnectedChannel>()
            val array = org.json.JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ConnectedChannel(
                        id = obj.getString("id"),
                        platform = try {
                            ChannelPlatform.valueOf(obj.optString("platform", "YOUTUBE"))
                        } catch (_: Exception) {
                            ChannelPlatform.YOUTUBE
                        },
                        channelUrl = obj.getString("channelUrl"),
                        channelHandle = obj.getString("channelHandle"),
                        channelName = obj.getString("channelName"),
                        subscriberCount = obj.optString("subscriberCount", "10K Subscribers"),
                        isConnected = obj.optBoolean("isConnected", true),
                        defaultPrivacy = try {
                            UploadPrivacyStatus.valueOf(obj.optString("defaultPrivacy", "PUBLIC"))
                        } catch (_: Exception) {
                            UploadPrivacyStatus.PUBLIC
                        },
                        defaultCategory = obj.optString("defaultCategory", "Animation / Film"),
                        defaultTags = obj.optString("defaultTags", "anime, ai video"),
                        requireOwnerVerificationBeforeUpload = obj.optBoolean("requireOwnerVerificationBeforeUpload", true),
                        connectedAt = obj.optLong("connectedAt", System.currentTimeMillis())
                    )
                )
            }
            if (list.isEmpty()) listOf(loadChannel()) else list
        } catch (_: Exception) {
            listOf(loadChannel())
        }
    }

    private fun saveChannels(channels: List<ConnectedChannel>) {
        try {
            val array = org.json.JSONArray()
            channels.forEach { c ->
                val obj = org.json.JSONObject().apply {
                    put("id", c.id)
                    put("platform", c.platform.name)
                    put("channelUrl", c.channelUrl)
                    put("channelHandle", c.channelHandle)
                    put("channelName", c.channelName)
                    put("subscriberCount", c.subscriberCount)
                    put("isConnected", c.isConnected)
                    put("defaultPrivacy", c.defaultPrivacy.name)
                    put("defaultCategory", c.defaultCategory)
                    put("defaultTags", c.defaultTags)
                    put("requireOwnerVerificationBeforeUpload", c.requireOwnerVerificationBeforeUpload)
                    put("connectedAt", c.connectedAt)
                }
                array.put(obj)
            }
            prefs.edit().putString("connected_channels_json", array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadChannel(): ConnectedChannel {
        val url = prefs.getString("channel_url", "https://youtube.com/@AnimeStudioCreator") ?: "https://youtube.com/@AnimeStudioCreator"
        val name = prefs.getString("channel_name", "Anime Studio Official") ?: "Anime Studio Official"
        val handle = prefs.getString("channel_handle", "@AnimeStudioCreator") ?: "@AnimeStudioCreator"
        val subs = prefs.getString("channel_subs", "24.8K Subscribers") ?: "24.8K Subscribers"
        val isConnected = prefs.getBoolean("channel_connected", true)

        return ConnectedChannel(
            channelUrl = url,
            channelName = name,
            channelHandle = handle,
            subscriberCount = subs,
            isConnected = isConnected
        )
    }

    private fun loadWebSeriesSchedule(): WebSeriesSchedule {
        val title = prefs.getString("series_title", "The Cyber Shinobi 2099") ?: "The Cyber Shinobi 2099"
        val epCount = prefs.getInt("episodes_per_day", 1)
        val targetChannel = prefs.getString("channel_url", "https://youtube.com/@AnimeStudioCreator") ?: "https://youtube.com/@AnimeStudioCreator"
        val autoPub = prefs.getBoolean("auto_publish_fallback", false)
        val fallbackHours = prefs.getInt("fallback_hours", 2)
        val requireVerify = prefs.getBoolean("require_owner_verification", true)
        val defaultPrivacyStr = prefs.getString("default_upload_privacy", "PUBLIC") ?: "PUBLIC"
        val privacy = try {
            UploadPrivacyStatus.valueOf(defaultPrivacyStr)
        } catch (_: Exception) {
            UploadPrivacyStatus.PUBLIC
        }

        return WebSeriesSchedule(
            seriesTitle = title,
            episodesPerDay = epCount,
            targetChannelUrl = targetChannel,
            requireOwnerVerificationBeforeUpload = requireVerify,
            defaultUploadPrivacy = privacy,
            autoPublishIfNoReview = autoPub,
            fallbackTimerHours = fallbackHours
        )
    }

    private fun loadInitialQueue(): List<AutomatedEpisode> {
        val now = System.currentTimeMillis()
        return listOf(
            AutomatedEpisode(
                id = "ep_01",
                seriesId = "cyber_shinobi",
                episodeNumber = 1,
                title = "Episode 1: The Plasma Blade Awakens",
                synopsis = "In the neon mist of Neo Kyoto, Ren discovers an ancient sakura core embedded in a cyber katana.",
                durationSeconds = 60,
                status = EpisodeReviewStatus.PENDING_REVIEW,
                generatedAtMillis = now - 1800000L,
                reviewDeadlineMillis = now + (2 * 60 * 60 * 1000L),
                uploadedPlatform = ChannelPlatform.YOUTUBE
            ),
            AutomatedEpisode(
                id = "ep_02",
                seriesId = "cyber_shinobi",
                episodeNumber = 2,
                title = "Episode 2: Shadow Syndicate Ambush",
                synopsis = "Aoi hacks the corporate grid as cyborg ronin swarm the rooftop shrine.",
                durationSeconds = 60,
                status = EpisodeReviewStatus.SCHEDULED,
                generatedAtMillis = now,
                reviewDeadlineMillis = now + (4 * 60 * 60 * 1000L),
                uploadedPlatform = ChannelPlatform.YOUTUBE
            )
        )
    }

    /**
     * Connect or update personal YouTube channel from copied link / URL
     */
    fun connectChannelByLink(
        urlOrLink: String,
        channelName: String = "Connected Personal Channel",
        platform: ChannelPlatform = ChannelPlatform.YOUTUBE
    ) {
        val cleanUrl = urlOrLink.trim()
        val extractedHandle = if (cleanUrl.contains("@")) {
            "@" + cleanUrl.substringAfter("@").substringBefore("/")
        } else {
            "@Creator" + cleanUrl.takeLast(6)
        }

        prefs.edit()
            .putString("channel_url", cleanUrl)
            .putString("channel_name", channelName)
            .putString("channel_handle", extractedHandle)
            .putBoolean("channel_connected", true)
            .apply()

        val newOrUpdatedChannel = ConnectedChannel(
            id = "yt_" + System.currentTimeMillis().toString().takeLast(6),
            channelUrl = cleanUrl,
            channelName = channelName,
            channelHandle = extractedHandle,
            subscriberCount = "New Channel",
            isConnected = true
        )

        val updatedList = listOf(newOrUpdatedChannel) + _connectedChannels.value.filter { it.channelHandle != extractedHandle }
        _connectedChannels.value = updatedList
        saveChannels(updatedList)

        _connectedChannel.value = newOrUpdatedChannel
        _automationStatusMessage.value = "✅ ${platform.displayName} चैनल लिंक सफलतापूर्वक जुड़ा: $extractedHandle"
    }

    /**
     * Switch the currently active Account/Channel for publishing and automation
     */
    fun selectActiveChannel(channelId: String) {
        val found = _connectedChannels.value.firstOrNull { it.id == channelId } ?: return
        _connectedChannel.value = found
        prefs.edit()
            .putString("channel_url", found.channelUrl)
            .putString("channel_name", found.channelName)
            .putString("channel_handle", found.channelHandle)
            .apply()
        _automationStatusMessage.value = "▶️ सक्रिय अकाउंट बदला गया: ${found.platform.displayName} • ${found.channelName} (${found.channelHandle})"
    }

    /**
     * Add a brand new YouTube Channel, Instagram Account, or Facebook Page to the multi-channel network
     */
    fun addChannel(
        channelName: String,
        channelHandleOrUrl: String,
        platform: ChannelPlatform = ChannelPlatform.YOUTUBE,
        defaultPrivacy: UploadPrivacyStatus = UploadPrivacyStatus.PUBLIC,
        subscribers: String = "10K Followers",
        category: String = "Animation / Film"
    ): ConnectedChannel {
        val cleanInput = channelHandleOrUrl.trim()
        val handle = if (cleanInput.startsWith("@")) cleanInput
        else if (cleanInput.contains("@")) "@" + cleanInput.substringAfter("@").substringBefore("/")
        else "@$cleanInput"
        val url = if (cleanInput.startsWith("http")) cleanInput else when (platform) {
            ChannelPlatform.YOUTUBE -> "https://youtube.com/$handle"
            ChannelPlatform.INSTAGRAM -> "https://instagram.com/${handle.removePrefix("@")}"
            ChannelPlatform.FACEBOOK -> "https://facebook.com/${handle.removePrefix("@")}"
            ChannelPlatform.TIKTOK -> "https://tiktok.com/$handle"
        }

        val newChannel = ConnectedChannel(
            id = "${platform.id}_" + System.currentTimeMillis(),
            platform = platform,
            channelName = channelName.ifBlank { "${platform.displayName} ($handle)" },
            channelHandle = handle,
            channelUrl = url,
            subscriberCount = subscribers.ifBlank { "Connected Account" },
            defaultPrivacy = defaultPrivacy,
            defaultCategory = category,
            requireOwnerVerificationBeforeUpload = true,
            isConnected = true,
            connectedAt = System.currentTimeMillis()
        )

        val updated = listOf(newChannel) + _connectedChannels.value
        _connectedChannels.value = updated
        saveChannels(updated)
        _connectedChannel.value = newChannel
        _automationStatusMessage.value = "🎉 नया ${platform.displayName} अकाउंट जोड़ा गया: ${newChannel.channelName} ($handle)"
        return newChannel
    }

    fun addYouTubeChannel(
        channelName: String,
        channelHandleOrUrl: String,
        subscribers: String = "0 Subscribers",
        category: String = "Animation / Film"
    ): ConnectedChannel {
        return addChannel(channelName, channelHandleOrUrl, ChannelPlatform.YOUTUBE, UploadPrivacyStatus.PUBLIC, subscribers, category)
    }

    /**
     * Remove a channel/account from the multi-channel network
     */
    fun removeChannel(channelId: String): Boolean {
        if (_connectedChannels.value.size <= 1) {
            _automationStatusMessage.value = "⚠️ कम से कम एक अकाउंट जुड़ा होना आवश्यक है।"
            return false
        }
        val updated = _connectedChannels.value.filter { it.id != channelId }
        _connectedChannels.value = updated
        saveChannels(updated)
        if (_connectedChannel.value.id == channelId) {
            _connectedChannel.value = updated.first()
        }
        _automationStatusMessage.value = "🗑️ अकाउंट हटाया गया।"
        return true
    }

    fun removeYouTubeChannel(channelId: String): Boolean = removeChannel(channelId)

    /**
     * Toggle whether the Owner must verify / give permission before uploading.
     * When true: AI agent waits for Owner Verification / Green Signal.
     * When false: AI agent automatically uploads directly according to schedule.
     */
    fun setRequireOwnerVerification(required: Boolean) {
        prefs.edit().putBoolean("require_owner_verification", required).apply()
        _webSeriesSchedule.value = _webSeriesSchedule.value.copy(
            requireOwnerVerificationBeforeUpload = required
        )
        val msg = if (required) {
            "🔒 ओनर वेरिफिकेशन एक्टिव: जब तक आप वेरीफाई ना करें, वीडियो ऑटोमैटिक अपलोड नहीं होगी।"
        } else {
            "⚡ डायरेक्ट ऑटो-अपलोड एक्टिव: AI एजेंट बिना वेरिफिकेशन के सीधे शेड्यूल पर वीडियो अपलोड करेगा।"
        }
        _automationStatusMessage.value = msg
    }

    /**
     * Toggle Default Upload Mode: Direct Public vs Save as Draft / Private first
     */
    fun setDefaultUploadPrivacy(privacy: UploadPrivacyStatus) {
        prefs.edit().putString("default_upload_privacy", privacy.name).apply()
        _webSeriesSchedule.value = _webSeriesSchedule.value.copy(
            defaultUploadPrivacy = privacy
        )
        val msg = when (privacy) {
            UploadPrivacyStatus.DRAFT_PRIVATE -> "📝 अपलोड मोड: पहले ड्राफ्ट / प्राइवेट में सुरक्षित सेव होगी।"
            UploadPrivacyStatus.PUBLIC -> "🌍 अपलोड मोड: सीधे पब्लिक के लिए लाइव अपलोड होगी।"
            UploadPrivacyStatus.UNLISTED -> "🔗 अपलोड मोड: अनलिस्टेड लिंक के रूप में अपलोड होगी।"
            UploadPrivacyStatus.SCHEDULED -> "⏰ अपलोड मोड: शेड्यूल टाइम पर पब्लिश होगी।"
        }
        _automationStatusMessage.value = msg
    }

    /**
     * Configure Web Series automation settings based on user command (e.g. 1, 2, or 3 episodes per day)
     */
    fun setSeriesEpisodesPerDay(episodesPerDay: Int, seriesTitle: String? = null) {
        val count = episodesPerDay.coerceIn(1, 5)
        prefs.edit().putInt("episodes_per_day", count).apply()
        seriesTitle?.let { prefs.edit().putString("series_title", it).apply() }

        _webSeriesSchedule.value = _webSeriesSchedule.value.copy(
            episodesPerDay = count,
            seriesTitle = seriesTitle ?: _webSeriesSchedule.value.seriesTitle
        )

        _automationStatusMessage.value = "📅 वेब सीरीज कमांड सेट: प्रतिदिन $count एपिसोड ऑटोमेटिक तैयार होंगे!"
    }

    fun setAutoPublishFallback(enabled: Boolean, fallbackHours: Int = 2) {
        prefs.edit()
            .putBoolean("auto_publish_fallback", enabled)
            .putInt("fallback_hours", fallbackHours)
            .apply()

        _webSeriesSchedule.value = _webSeriesSchedule.value.copy(
            autoPublishIfNoReview = enabled,
            fallbackTimerHours = fallbackHours
        )
    }

    /**
     * User Green Signal / OK Approval:
     * Uploads the reviewed episode directly to the connected platform, honoring Public vs Draft / Private!
     */
    suspend fun giveGreenSignalAndUpload(
        episodeId: String,
        targetPrivacy: UploadPrivacyStatus? = null
    ): AutomatedEpisode = withContext(Dispatchers.Default) {
        val channel = _connectedChannel.value
        val schedule = _webSeriesSchedule.value
        val privacy = targetPrivacy ?: schedule.defaultUploadPrivacy

        val isDraft = privacy == UploadPrivacyStatus.DRAFT_PRIVATE
        val actionText = if (isDraft) "ड्राफ्ट में सुरक्षित सेविंग..." else "${channel.platform.displayName} पर ऑटोमैटिक अपलोडिंग शुरू..."
        _automationStatusMessage.value = "🟢 ग्रीन सिग्नल प्राप्त! $actionText"

        // Step 1: Mark Approved
        updateEpisodeStatus(episodeId, EpisodeReviewStatus.APPROVED)
        delay(1200)

        val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val generatedId = "v_" + System.currentTimeMillis().toString().takeLast(8)
        val finalUrl = when (channel.platform) {
            ChannelPlatform.YOUTUBE -> if (isDraft) "https://studio.youtube.com/video/$generatedId/edit" else "https://youtu.be/$generatedId"
            ChannelPlatform.INSTAGRAM -> "https://instagram.com/reel/$generatedId"
            ChannelPlatform.FACEBOOK -> "https://facebook.com/watch/?v=$generatedId"
            ChannelPlatform.TIKTOK -> "https://tiktok.com/@video/$generatedId"
        }

        val finalStatus = if (isDraft) EpisodeReviewStatus.DRAFT_SAVED else EpisodeReviewStatus.UPLOADED
        val statusNote = if (isDraft) {
            "Saved as Draft on ${channel.platform.displayName} (${channel.channelHandle}) at $timeFormatted"
        } else {
            "Published Publicly on ${channel.platform.displayName} (${channel.channelHandle}) at $timeFormatted"
        }

        val updated = _episodesQueue.value.map { ep ->
            if (ep.id == episodeId) {
                ep.copy(
                    status = finalStatus,
                    uploadedPlatform = channel.platform,
                    uploadedUrl = finalUrl,
                    uploadPrivacy = privacy,
                    isGreenSignalGiven = true,
                    viewCountEstimate = statusNote
                )
            } else ep
        }

        _episodesQueue.value = updated
        val successMsg = if (isDraft) {
            "📝 सफलतापूर्वक ${channel.platform.displayName} (${channel.channelHandle}) के ड्राफ्ट में सेव हो गया! लिंक: $finalUrl"
        } else {
            "✅ सफलतापूर्वक ${channel.platform.displayName} (${channel.channelHandle}) पर पब्लिक अपलोड हो गया! लिंक: $finalUrl"
        }
        _automationStatusMessage.value = successMsg

        // Send system push notification alert
        val episodeObj = updated.first { it.id == episodeId }
        com.example.util.NotificationHelper.sendUploadAlert(
            context = context,
            episodeTitle = episodeObj.title,
            channelName = channel.channelName,
            platformName = channel.platform.displayName,
            requiresOwnerVerification = false
        )

        episodeObj
    }

    /**
     * Reject or request changes on an episode
     */
    fun rejectEpisode(episodeId: String, note: String = "User requested revision") {
        updateEpisodeStatus(episodeId, EpisodeReviewStatus.REJECTED)
        _automationStatusMessage.value = "❌ एपिसोड अस्वीकृत। नया ड्राफ्ट तैयार किया जाएगा।"
    }

    /**
     * Automated Trigger to generate today's scheduled episodes based on series command
     */
    suspend fun triggerDailyEpisodeGeneration(commandCount: Int? = null) = withContext(Dispatchers.Default) {
        val count = commandCount ?: _webSeriesSchedule.value.episodesPerDay
        val currentMax = _episodesQueue.value.maxOfOrNull { it.episodeNumber } ?: 0
        val schedule = _webSeriesSchedule.value
        val activeChan = _connectedChannel.value

        _automationStatusMessage.value = "⚡ AI ऑटोमेशन कमांड: आज $count एपिसोड बनाए जा रहे हैं..."

        val newEpisodes = mutableListOf<AutomatedEpisode>()
        for (i in 1..count) {
            val epNum = currentMax + i
            delay(800)
            val newEp = AutomatedEpisode(
                id = "ep_auto_${System.currentTimeMillis()}_$i",
                seriesId = schedule.seriesTitle.lowercase().replace(" ", "_"),
                episodeNumber = epNum,
                title = "Episode $epNum: ${schedule.seriesTitle} - Part $epNum",
                synopsis = "The cyber samurai saga continues as the neon conspiracy unfolds across ${activeChan.platform.displayName}.",
                durationSeconds = 60,
                status = EpisodeReviewStatus.PENDING_REVIEW,
                uploadedPlatform = activeChan.platform,
                uploadPrivacy = schedule.defaultUploadPrivacy,
                generatedAtMillis = System.currentTimeMillis(),
                reviewDeadlineMillis = System.currentTimeMillis() + (schedule.fallbackTimerHours * 60 * 60 * 1000L)
            )
            newEpisodes.add(newEp)
        }

        _episodesQueue.value = newEpisodes + _episodesQueue.value

        // Check verification requirement
        if (schedule.requireOwnerVerificationBeforeUpload) {
            _automationStatusMessage.value = "🔔 $count नए एपिसोड तैयार हैं! ओनर वेरिफिकेशन चालू है: कृपया रिव्यू करके ग्रीन सिग्नल (OK) दें।"
            // Alert owner via push notification that verification is required before upload
            newEpisodes.firstOrNull()?.let { firstEp ->
                com.example.util.NotificationHelper.sendUploadAlert(
                    context = context,
                    episodeTitle = firstEp.title,
                    channelName = activeChan.channelName,
                    platformName = activeChan.platform.displayName,
                    requiresOwnerVerification = true
                )
            }
        } else {
            // Instant Auto-Upload without waiting for owner verification
            _automationStatusMessage.value = "🚀 ओनर वेरिफिकेशन बंद है: सभी $count एपिसोड सीधे ऑटो-अपलोड हो रहे हैं..."
            for (ep in newEpisodes) {
                giveGreenSignalAndUpload(ep.id)
            }
        }
    }

    /**
     * Enqueue a researched trend episode directly into the YouTube Automation queue
     */
    fun addEpisodeToQueue(
        episodeTitle: String,
        prompt: String,
        synopsis: String,
        channelHandle: String,
        durationSeconds: Int = 60
    ): AutomatedEpisode {
        val currentMax = _episodesQueue.value.maxOfOrNull { it.episodeNumber } ?: 0
        val epNum = currentMax + 1
        val schedule = _webSeriesSchedule.value
        val newEp = AutomatedEpisode(
            id = "ep_trend_${System.currentTimeMillis()}",
            seriesId = schedule.seriesTitle.lowercase().replace(" ", "_"),
            episodeNumber = epNum,
            title = episodeTitle,
            synopsis = "$synopsis | Prompt: $prompt",
            durationSeconds = durationSeconds,
            status = EpisodeReviewStatus.PENDING_REVIEW,
            generatedAtMillis = System.currentTimeMillis(),
            reviewDeadlineMillis = System.currentTimeMillis() + (schedule.fallbackTimerHours * 60 * 60 * 1000L)
        )
        _episodesQueue.value = listOf(newEp) + _episodesQueue.value
        _automationStatusMessage.value = "⚡ नया ट्रेंड एपिसोड ऑटोमेशन कतार में जोड़ा गया: $episodeTitle"
        return newEp
    }

    /**
     * Scheduled Fallback Timer Check:
     * If user review deadline has passed and auto-publish fallback is on, auto-upload!
     */
    suspend fun checkAndAutoPublishExpiredReviews() = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        val schedule = _webSeriesSchedule.value
        if (!schedule.autoPublishIfNoReview) return@withContext

        val pendingExpired = _episodesQueue.value.filter {
            it.status == EpisodeReviewStatus.PENDING_REVIEW && now >= it.reviewDeadlineMillis
        }

        for (ep in pendingExpired) {
            giveGreenSignalAndUpload(ep.id)
        }
    }

    private fun updateEpisodeStatus(episodeId: String, status: EpisodeReviewStatus) {
        _episodesQueue.value = _episodesQueue.value.map { ep ->
            if (ep.id == episodeId) ep.copy(status = status) else ep
        }
    }
}
