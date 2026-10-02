package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.ui.components.AppPermissionsOnboardingDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.DownloadProjectDialog
import com.example.ui.components.ExportShareSheetDialog
import com.example.ui.components.LocalStorageVaultDialog
import com.example.localization.AppLocaleStrings
import com.example.ui.components.CountryCodePickerBottomSheet
import com.example.ui.components.StorageDestinationDialog
import com.example.data.model.StorageTargetType
import com.example.ui.screens.AnimePlayerScreen
import com.example.ui.screens.AppSettingsScreen
import com.example.ui.screens.CharacterBuilderScreen
import com.example.ui.screens.CharacterStudioScreen
import com.example.ui.screens.MyProjectsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ProjectHistoryScreen
import com.example.ui.screens.RecentVideoProjectsScreen
import com.example.ui.screens.StudioCreateScreen
import com.example.ui.screens.SubscriptionAdminScreen
import com.example.ui.screens.UpdateSettingsScreen
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: AnimeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.ui.theme.AppThemeController.initialize(this)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val selectedCountry by viewModel.selectedCountry.collectAsState()
            var isShowingProfileSettings by remember { mutableStateOf(false) }
            var showCountryPickerSheet by remember { mutableStateOf(false) }

            // Strictly one-time permission request upon installation/first launch only
            val installPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _: Boolean ->
                com.example.util.PermissionPreferenceManager.markPermissionRequestedOnInstall(this@MainActivity)
            }

            LaunchedEffect(Unit) {
                if (com.example.util.PermissionPreferenceManager.shouldTriggerInitialPermissionRequest(this@MainActivity)) {
                    com.example.util.PermissionPreferenceManager.markPermissionRequestedOnInstall(this@MainActivity)
                    installPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                }
            }

            val isFollowSystem = com.example.ui.theme.AppThemeController.isFollowSystemTheme
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val effectiveDark = if (isFollowSystem) systemDark else com.example.ui.theme.AppThemeController.isDarkMode

            MyApplicationTheme(
                darkTheme = effectiveDark,
                vibrantTheme = com.example.ui.theme.AppThemeController.currentThemeKey,
                dynamicColor = isFollowSystem
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.setTab(AppTab.STUDIO) }
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AnimePink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = AppLocaleStrings.get("app_title", state.selectedLanguage),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (currentUser.isOwner) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AnimeGold.copy(alpha = 0.2f))
                                                .border(1.dp, AnimeGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "👑 OWNER",
                                                color = AnimeGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            },
                            actions = {
                                // Global Country & Language Quick Selector Chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, AnimeCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable { showCountryPickerSheet = true }
                                        .padding(horizontal = 7.dp, vertical = 4.dp)
                                        .testTag("top_bar_country_btn")
                                ) {
                                    val displayCountry = com.example.data.model.CountryCodeProvider.findByLanguage(state.selectedLanguage) ?: selectedCountry
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = displayCountry.flagEmoji, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = displayCountry.languageCode.uppercase(),
                                            color = AnimeCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // NOTE: Color & Theme option has been removed from header bar per user request.
                                // It is exclusively housed inside Settings.

                                IconButton(
                                    onClick = { viewModel.setTab(AppTab.PROJECTS) },
                                    modifier = Modifier.testTag("top_bar_projects_btn")
                                ) {
                                    Icon(Icons.Default.History, contentDescription = "Project History", tint = AnimeCyan)
                                }
                                IconButton(
                                    onClick = { viewModel.setTab(AppTab.SETTINGS) },
                                    modifier = Modifier.testTag("top_bar_settings_btn")
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                                }
                                // Profile Avatar button in top bar
                                IconButton(
                                    onClick = {
                                        isShowingProfileSettings = false
                                        viewModel.setTab(AppTab.PROFILE)
                                    },
                                    modifier = Modifier.testTag("top_bar_profile_btn")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (currentUser.isOwner) AnimeGold else AnimePurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = "Profile",
                                            tint = if (currentUser.isOwner) Color.Black else Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        val lang = state.selectedLanguage
                        val studioLabel = AppLocaleStrings.get("tab_studio", lang)
                        val playerLabel = AppLocaleStrings.get("tab_player", lang)
                        val builderLabel = AppLocaleStrings.get("tab_builder", lang)
                        val charactersLabel = AppLocaleStrings.get("tab_characters", lang)
                        val vipLabel = AppLocaleStrings.get("tab_vip", lang)
                        val profileLabel = AppLocaleStrings.get("tab_profile", lang)

                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("main_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = state.currentTab == AppTab.STUDIO,
                                onClick = { viewModel.setTab(AppTab.STUDIO) },
                                icon = {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = "Studio", modifier = Modifier.size(20.dp))
                                },
                                label = { Text(studioLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimePurple,
                                    selectedTextColor = AnimePurple,
                                    indicatorColor = AnimePurple.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_studio")
                            )

                            NavigationBarItem(
                                selected = state.currentTab == AppTab.PLAYER,
                                onClick = { viewModel.setTab(AppTab.PLAYER) },
                                icon = {
                                    Icon(Icons.Default.PlayCircle, contentDescription = "Player", modifier = Modifier.size(20.dp))
                                },
                                label = { Text(playerLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimeCyan,
                                    selectedTextColor = AnimeCyan,
                                    indicatorColor = AnimeCyan.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_player")
                            )

                            NavigationBarItem(
                                selected = state.currentTab == AppTab.BUILDER,
                                onClick = { viewModel.setTab(AppTab.BUILDER) },
                                icon = {
                                    Icon(Icons.Default.Palette, contentDescription = "Builder", modifier = Modifier.size(20.dp))
                                },
                                label = { Text(builderLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimePink,
                                    selectedTextColor = AnimePink,
                                    indicatorColor = AnimePink.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_builder")
                            )

                            NavigationBarItem(
                                selected = state.currentTab == AppTab.CHARACTERS,
                                onClick = { viewModel.setTab(AppTab.CHARACTERS) },
                                icon = {
                                    Icon(Icons.Default.Face, contentDescription = "Characters", modifier = Modifier.size(20.dp))
                                },
                                label = { Text(charactersLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimePink,
                                    selectedTextColor = AnimePink,
                                    indicatorColor = AnimePink.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_characters")
                            )

                            NavigationBarItem(
                                selected = state.currentTab == AppTab.SUBSCRIPTION,
                                onClick = { viewModel.setTab(AppTab.SUBSCRIPTION) },
                                icon = {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP Admin", modifier = Modifier.size(20.dp))
                                },
                                label = { Text(vipLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimeGold,
                                    selectedTextColor = AnimeGold,
                                    indicatorColor = AnimeGold.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_subscription")
                            )

                            NavigationBarItem(
                                selected = state.currentTab == AppTab.PROFILE || state.currentTab == AppTab.SETTINGS,
                                onClick = {
                                    isShowingProfileSettings = false
                                    viewModel.setTab(AppTab.PROFILE)
                                },
                                icon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(20.dp))
                                },
                                label = { Text(profileLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimeGreen,
                                    selectedTextColor = AnimeGreen,
                                    indicatorColor = AnimeGreen.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_profile")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (state.currentTab) {
                            AppTab.STUDIO -> StudioCreateScreen(viewModel)
                            AppTab.PLAYER -> AnimePlayerScreen(viewModel)
                            AppTab.PROJECTS -> RecentVideoProjectsScreen(viewModel)
                            AppTab.BUILDER -> CharacterBuilderScreen(viewModel)
                            AppTab.CHARACTERS -> CharacterStudioScreen(viewModel)
                            AppTab.SUBSCRIPTION -> SubscriptionAdminScreen(viewModel)
                            AppTab.UPDATES -> UpdateSettingsScreen(viewModel)
                            AppTab.PROFILE -> {
                                if (isShowingProfileSettings) {
                                    ProfileSettingsScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { isShowingProfileSettings = false }
                                    )
                                } else {
                                    ProfileScreen(
                                        viewModel = viewModel,
                                        onNavigateToProfileSettings = { isShowingProfileSettings = true },
                                        onNavigateToAppSettings = { viewModel.setTab(AppTab.SETTINGS) }
                                    )
                                }
                            }
                            AppTab.SETTINGS -> {
                                AppSettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.setTab(AppTab.PROFILE) }
                                )
                            }
                        }
                    }

                    // Global Dialog Overlays
                    if (state.showStorageDestinationDialog) {
                        StorageDestinationDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.toggleStorageDialog(false) }
                        )
                    }

                    if (state.showLocalStorageVault) {
                        LocalStorageVaultDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.toggleLocalStorageVault(false) }
                        )
                    }

                    if (state.showExportShareDialog) {
                        ExportShareSheetDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.toggleExportShareDialog(false) }
                        )
                    }

                    if (state.showDownloadDialog) {
                        DownloadProjectDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.toggleDownloadDialog(false) }
                        )
                    }

                    if (showCountryPickerSheet) {
                        CountryCodePickerBottomSheet(
                            selectedCountry = selectedCountry,
                            onCountrySelected = { country ->
                                viewModel.selectCountry(country)
                                showCountryPickerSheet = false
                            },
                            onDismiss = { showCountryPickerSheet = false }
                        )
                    }
                }
            }
        }
    }
}
