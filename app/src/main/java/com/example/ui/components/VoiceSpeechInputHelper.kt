package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.localization.AppLocaleStrings
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimePink
import java.util.Locale

/**
 * Speech Recognition Mic Button for Jetpack Compose
 * Allows users to speak in their chosen language and automatically types the command or prompt.
 */
@Composable
fun MicVoiceInputButton(
    language: String,
    onSpeechResult: (String) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "mic_voice_input_btn"
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }

    val localeTag = remember(language) {
        val normalized = AppLocaleStrings.normalizeLanguage(language)
        when (normalized) {
            "Hindi" -> "hi-IN"
            "English" -> "en-US"
            "Japanese" -> "ja-JP"
            "Korean" -> "ko-KR"
            "Spanish" -> "es-ES"
            "German" -> "de-DE"
            "French" -> "fr-FR"
            "Chinese" -> "zh-CN"
            "Arabic" -> "ar-SA"
            "Russian" -> "ru-RU"
            "Portuguese" -> "pt-BR"
            "Indonesian" -> "id-ID"
            else -> Locale.getDefault().toLanguageTag()
        }
    }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenTextList?.firstOrNull()?.trim()
            if (!spokenText.isNullOrBlank()) {
                onSpeechResult(spokenText)
                Toast.makeText(context, "🎙️ \"$spokenText\"", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchSpeechIntent(context, localeTag, speechRecognizerLauncher)
            isListening = true
        } else {
            Toast.makeText(
                context,
                AppLocaleStrings.get("mic_permission_needed", language),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    IconButton(
        onClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                isListening = true
                launchSpeechIntent(context, localeTag, speechRecognizerLauncher)
            } else {
                // Do not re-request permission repeatedly anywhere in the app
                val msg = if (AppLocaleStrings.isHindi(language)) {
                    "माइक अनुमति वैकल्पिक है। आप टेक्स्ट लिखकर भी प्रॉम्प्ट बना सकते हैं।"
                } else {
                    "Microphone is optional. You can enter your text prompt directly."
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier.testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .scale(if (isListening) pulseScale else 1.0f)
                .clip(CircleShape)
                .background(if (isListening) AnimePink else AnimeCyan.copy(alpha = 0.2f))
                .border(
                    1.dp,
                    if (isListening) AnimePink else AnimeCyan.copy(alpha = 0.6f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = AppLocaleStrings.get("mic_tap_to_speak", language),
                tint = if (isListening) Color.White else AnimeCyan,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun launchSpeechIntent(
    context: Context,
    localeTag: String,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeTag)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, localeTag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "🎙️ Speak anime prompt / command...")
        }
        launcher.launch(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Voice speech recognition unavailable on this device", Toast.LENGTH_SHORT).show()
    }
}
