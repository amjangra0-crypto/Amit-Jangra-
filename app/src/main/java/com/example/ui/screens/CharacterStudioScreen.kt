package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.ResourceHelpers
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterStudioScreen(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()
    val context = LocalContext.current

    val draft = state.characterDraft

    // Tab state: 0 = AI Prompt & Visual Designer, 1 = Voices & Dialogue Sync, 2 = Character Roster
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .padding(bottom = 76.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "🎭 एनिमे करैक्टर क्रिएटर (Character Studio)",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "विजुअल एट्रिब्यूट्स और विविध AI आवाजों (Male, Female, Child, Accents) के साथ डिजाइन करें",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Quick Link to Character Builder
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTab(AppTab.BUILDER) },
                    colors = CardDefaults.cardColors(containerColor = AnimePurple.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = AnimePink, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("🎨 करैक्टर बिल्डर (Character Builder)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("बाल, आँखों का रंग व पोशाक कस्टमाइज करें", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                        Button(
                            onClick = { viewModel.setTab(AppTab.BUILDER) },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("बिल्डर खोलें", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Studio Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = AnimeSurface,
                    contentColor = AnimeCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AnimePurple
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("विजुअल डिजाइन", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI वॉइस व लिप-सिंक", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("रोस्टर (${state.customCharacters.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Character Visual Card (always visible at top of tabs 0 and 1)
        if (selectedTab != 2) {
            item {
                LiveCharacterPreviewCard(
                    character = draft,
                    isSpeaking = speakingState.isSpeaking && speakingState.activeCharacter == draft.name,
                    mouthOpenAmount = speakingState.mouthOpenAmount,
                    audioWave = speakingState.audioWaveAmplitude,
                    onAudition = { viewModel.auditionDraftVoice() },
                    onStop = { viewModel.voiceSyncEngine.stop() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // TAB 0: Visual Attribute Designer & Text Prompt
        if (selectedTab == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✨ AI विजुअल प्रॉम्प्ट से करैक्टर बनाएं (Text Prompt to Visuals)",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.characterPromptInput,
                            onValueChange = { viewModel.setCharacterPromptInput(it) },
                            placeholder = { Text("उदा: Cyber samurai girl with neon cyan twin tails, dragon kimono and glowing katana...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("char_visual_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimePurple,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Prompt Presets
                        Text("त्वरित प्रॉम्प्ट्स (Quick Presets):", color = TextSecondary, fontSize = 11.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val presets = listOf(
                                "⚡ Cyber Shinobi Blade (Male)",
                                "🌸 Shrine Maiden Fox Aura (Female)",
                                "🔥 Dragon Fire Shonen (Male)",
                                "✨ Chibi Spirit Mascot (Child)"
                            )
                            presets.forEach { p ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AnimeSurfaceVariant)
                                        .clickable { viewModel.setCharacterPromptInput(p) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(p, color = AnimeCyanLight, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.generateCharacterWithAi() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_generate_char_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !state.isAiDesigningCharacter
                        ) {
                            if (state.isAiDesigningCharacter) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI विजुअल और वॉइस तैयार कर रहा है...", fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("✨ AI से विजुअल व वॉइस जनरेट करें", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Visual Attributes Customizer Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎨 करैक्टर विजुअल एट्रिब्यूट्स कस्टमाइज करें (Visual Attributes)",
                            color = AnimeCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Name and Role inputs
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = draft.name,
                                onValueChange = { viewModel.updateCharacterDraft(draft.copy(name = it)) },
                                label = { Text("नाम") },
                                modifier = Modifier.weight(1f).testTag("draft_char_name"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeCyan, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = draft.role,
                                onValueChange = { viewModel.updateCharacterDraft(draft.copy(role = it)) },
                                label = { Text("रोल / क्लास") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeCyan, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hair Style
                        VisualAttributeSelector(
                            title = "हेयर स्टाइल (Hair Style)",
                            options = listOf("Spiky Shonen Action", "Kawaii Twin Tails", "Long Flowing Celestial", "Modern Anime Bob", "Samurai Ponytail"),
                            selected = draft.hairStyle,
                            onSelect = { viewModel.updateCharacterDraft(draft.copy(hairStyle = it)) }
                        )

                        // Hair Color
                        VisualAttributeSelector(
                            title = "हेयर कलर (Hair Color)",
                            options = listOf("Silver Starlight", "Sakura Rose Pink", "Neon Electric Cyan", "Crimson Blaze", "Golden Celestial Amber", "Obsidian Midnight Black"),
                            selected = draft.hairColor,
                            onSelect = { viewModel.updateCharacterDraft(draft.copy(hairColor = it)) }
                        )

                        // Eye Color
                        VisualAttributeSelector(
                            title = "आंखों का रंग (Eye Color)",
                            options = listOf("Sapphire Neon Blue", "Crimson Ruby Flame", "Amethyst Mystic Violet", "Emerald Forest Glow", "Golden Topaz"),
                            selected = draft.eyeColor,
                            onSelect = { viewModel.updateCharacterDraft(draft.copy(eyeColor = it)) }
                        )

                        // Outfit
                        VisualAttributeSelector(
                            title = "कॉस्ट्यूम / आउटफिट (Outfit)",
                            options = listOf("Cyber Shinobi Exo-Suit", "Traditional Ronin Kimono", "High Academy Uniform", "Enchanted Starlight Cloak", "Battle Armor"),
                            selected = draft.outfit,
                            onSelect = { viewModel.updateCharacterDraft(draft.copy(outfit = it)) }
                        )

                        // Accessory Aura
                        VisualAttributeSelector(
                            title = "औरा / इफेक्ट (Accessory & Aura)",
                            options = listOf("Crackling Blue Lightning Sparks", "Dancing Sakura Blossom Blizzard", "Swirling Dragon Fire Aura", "Holographic Glitch Particles", "Celestial Golden Particles"),
                            selected = draft.accessoryAura,
                            onSelect = { viewModel.updateCharacterDraft(draft.copy(accessoryAura = it)) }
                        )

                        // Expression
                        VisualAttributeSelector(
                            title = "चेहरे का भाव (Expression)",
                            options = listOf("Fierce Determined Stare", "Warm Confident Smile", "Calm Mysterious Smirk", "Playful Kawaii Winking"),
                            selected = draft.expression,
                            onSelect = { viewModel.updateCharacterDraft(draft.copy(expression = it)) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.saveDraftCharacter()
                                selectedTab = 2
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_draft_char_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("करैक्टर सेव करें (Save to Anime Roster)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 1: Diverse AI Voices & Dialogue Sync Station
        if (selectedTab == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = AnimePink, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎙️ AI वॉइस चयन (Diverse Voices: Male, Female, Child, Accents)",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Voice Gender Selection
                        Text("1. वॉइस जेंडर (Voice Gender):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("Male", "Female", "Child").forEach { g ->
                                val isSelected = draft.voiceGender.equals(g, ignoreCase = true)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val newType = when (g) {
                                            "Male" -> "Boy"
                                            "Child" -> "Mascot"
                                            else -> "Girl"
                                        }
                                        val newAvatar = when (g) {
                                            "Male" -> "char_shonen_hero"
                                            "Child" -> "char_chibi_mascot"
                                            else -> "char_anime_heroine"
                                        }
                                        val defaultPersona = when (g) {
                                            "Male" -> "Deep Shonen Hero"
                                            "Child" -> "Cheerful Playful Kid"
                                            else -> "Sweet Kawaii Heroine"
                                        }
                                        val defaultPitch = when (g) {
                                            "Male" -> 0.88f
                                            "Child" -> 1.70f
                                            else -> 1.35f
                                        }
                                        viewModel.updateCharacterDraft(
                                            draft.copy(
                                                voiceGender = g,
                                                voiceType = newType,
                                                avatarDrawableName = newAvatar,
                                                voicePersona = defaultPersona,
                                                voicePitch = defaultPitch
                                            )
                                        )
                                    },
                                    label = {
                                        val icon = when (g) {
                                            "Male" -> "👨 Male"
                                            "Child" -> "🧒 Child"
                                            else -> "👩 Female"
                                        }
                                        Text(icon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AnimePurple.copy(alpha = 0.35f),
                                        selectedLabelColor = AnimeCyan
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Voice Persona Options based on selected gender
                        Text("2. AI वॉइस परसोना (Voice Persona):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        val personas = when (draft.voiceGender) {
                            "Male" -> listOf(
                                "Deep Shonen Hero" to 0.88f,
                                "Calm Master Sensei" to 0.78f,
                                "Fierce Antagonist" to 0.72f,
                                "Cyber Renegade" to 0.84f
                            )
                            "Child" -> listOf(
                                "Cheerful Playful Kid" to 1.65f,
                                "Wondering Apprentice" to 1.55f,
                                "Kawaii Chibi Fairy" to 1.82f
                            )
                            else -> listOf(
                                "Sweet Kawaii Heroine" to 1.38f,
                                "Tsundere Rival" to 1.45f,
                                "Regal Priestess" to 1.18f,
                                "Stealth Cyber Agent" to 1.24f
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            personas.forEach { (pName, pitch) ->
                                val selected = draft.voicePersona == pName
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        viewModel.updateCharacterDraft(
                                            draft.copy(voicePersona = pName, voicePitch = pitch)
                                        )
                                    },
                                    label = { Text(pName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AnimePink.copy(alpha = 0.35f),
                                        selectedLabelColor = AnimeGold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. Accent & Cadence selection
                        Text("3. एक्सेंट और उच्चारण (Accent & Dialect):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        val accents = listOf(
                            "Standard Anime (Japanese Cadence)",
                            "English (British Posh)",
                            "English (American Casual)",
                            "Hindi Dub (Heroic Bollywood Anime)",
                            "Cybernetic / Vocoded Synth",
                            "Ethereal / Soft Whisper"
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            accents.forEach { acc ->
                                val selected = draft.voiceAccent == acc
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        viewModel.updateCharacterDraft(draft.copy(voiceAccent = acc))
                                    },
                                    label = { Text(acc, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AnimeCyan.copy(alpha = 0.3f),
                                        selectedLabelColor = AnimeCyanLight
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sliders for pitch and speed fine-tuning
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("पिच मॉड्यूलेशन (Pitch): ${"%.2f".format(draft.voicePitch)}x", color = AnimeGold, fontSize = 12.sp)
                            Text("गति (Speed): ${"%.2f".format(draft.voiceSpeed)}x", color = AnimeCyan, fontSize = 12.sp)
                        }
                        Slider(
                            value = draft.voicePitch,
                            onValueChange = { viewModel.updateCharacterDraft(draft.copy(voicePitch = it)) },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = AnimeGold, activeTrackColor = AnimePurple)
                        )
                        Slider(
                            value = draft.voiceSpeed,
                            onValueChange = { viewModel.updateCharacterDraft(draft.copy(voiceSpeed = it)) },
                            valueRange = 0.6f..1.6f,
                            colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimePink)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dialogue Sync Test Section
                        Text("4. डायलॉग सिंक्रोनाइजेशन टेस्ट (Dialogue Sync Audition):", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = draft.sampleDialogue,
                            onValueChange = { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = it)) },
                            label = { Text("टेस्ट डायलॉग (डायलॉग यहां टाइप करें या प्रीसेट चुनें)") },
                            modifier = Modifier.fillMaxWidth().testTag("char_dialogue_test_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimePurple, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Dialogue Samples in JP, Hindi, EN
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeSurfaceVariant)
                                    .clickable { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = "私を信じて！一緒に未来を変えよう！")) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("🎌 Japanese", color = AnimeCyanLight, fontSize = 10.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeSurfaceVariant)
                                    .clickable { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = "मुझ पर विश्वास रखो! हम दोनों मिलकर इस दुनिया को बचाएंगे!")) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("🇮🇳 Hindi", color = AnimeCyanLight, fontSize = 10.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeSurfaceVariant)
                                    .clickable { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = "Believe in my blade! Our destiny begins now!")) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("🇬🇧 English", color = AnimeCyanLight, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { viewModel.auditionDraftVoice() },
                                modifier = Modifier.weight(1f).testTag("audition_dialogue_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🎙️ वॉइस सिंक सुनें", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.saveDraftCharacter()
                                    selectedTab = 2
                                },
                                modifier = Modifier.weight(1f).testTag("save_char_dialogue_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("करैक्टर सेव करें", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: Character Roster
        if (selectedTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "सेव किए गए एनिमे करैक्टर्स (Saved Anime Roster)",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { selectedTab = 0 },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ नया डिजाइन", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(state.customCharacters) { char ->
                RosterCharacterCard(
                    character = char,
                    onAudition = {
                        viewModel.voiceSyncEngine.speakDialogue(
                            characterName = char.name,
                            dialogueText = char.sampleDialogue.ifBlank { "नमस्ते! मैं ${char.name} हूँ!" },
                            emotion = "Happy",
                            voiceType = char.voiceType,
                            pitch = char.voicePitch,
                            speed = char.voiceSpeed,
                            voiceGender = char.voiceGender,
                            voicePersona = char.voicePersona,
                            voiceAccent = char.voiceAccent
                        )
                    },
                    onEdit = {
                        viewModel.updateCharacterDraft(char)
                        selectedTab = 0
                    },
                    onDelete = { viewModel.deleteCustomCharacter(char.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun LiveCharacterPreviewCard(
    character: CharacterProfile,
    isSpeaking: Boolean,
    mouthOpenAmount: Float,
    audioWave: Float,
    onAudition: () -> Unit,
    onStop: () -> Unit
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking) 1.06f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(240, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speakingScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(AnimePurple, AnimePink, AnimeCyan))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character Avatar with Animated Speaking Glow & Mouth Flap Effect
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(76.dp)
                ) {
                    // Outer aura glow ring
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (isSpeaking) AnimePink else AnimePurple.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Image(
                        painter = painterResource(id = ResourceHelpers.getDrawableId(context, character.avatarDrawableName)),
                        contentDescription = character.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSpeaking) AnimePink else AnimeCyan, CircleShape)
                    )

                    // Real-time lip-flap / mouth opening visual indicator
                    if (isSpeaking) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 6.dp)
                                .size(width = 16.dp, height = (8.dp * mouthOpenAmount).coerceAtLeast(3.dp))
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Red.copy(alpha = 0.85f))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character.name,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimePurple.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(character.voiceGender, color = AnimeCyanLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "🎭 ${character.role}",
                        color = AnimeGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "🎙️ ${character.voicePersona} • ${character.voiceAccent.take(22)}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✨ ${character.hairColor} Hair • ${character.eyeColor} Eyes",
                        color = AnimeCyanLight,
                        fontSize = 10.sp
                    )
                }

                // Play / Stop Audition Button
                IconButton(
                    onClick = { if (isSpeaking) onStop() else onAudition() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSpeaking) AnimePink else AnimePurple)
                ) {
                    Icon(
                        if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = "Audition",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Speaking Audio Waveform Bar
            if (isSpeaking) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "आवाज़ सिंक्रोनाइज हो रही है: \"${character.sampleDialogue.take(35)}...\"",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VisualAttributeSelector(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            options.forEach { opt ->
                val isSel = selected == opt
                FilterChip(
                    selected = isSel,
                    onClick = { onSelect(opt) },
                    label = { Text(opt, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimePurple.copy(alpha = 0.35f),
                        selectedLabelColor = AnimeCyan
                    )
                )
            }
        }
    }
}

@Composable
private fun RosterCharacterCard(
    character: CharacterProfile,
    onAudition: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = ResourceHelpers.getDrawableId(context, character.avatarDrawableName)),
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, AnimePurple, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = character.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnimePurple.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(character.voicePersona.take(16), color = AnimeCyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "${character.role} • ${character.hairColor} Hair",
                    color = AnimeGold,
                    fontSize = 11.sp
                )
                Text(
                    text = "🎙️ ${character.voiceAccent} | पिच: ${"%.2f".format(character.voicePitch)}x",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            IconButton(onClick = onAudition) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Audition", tint = AnimeCyan, modifier = Modifier.size(20.dp))
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Tune, contentDescription = "Edit", tint = AnimeGold, modifier = Modifier.size(18.dp))
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
