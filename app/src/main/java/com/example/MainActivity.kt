package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.ui.screens.AnimePlayerScreen
import com.example.ui.screens.CharacterStudioScreen
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
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AnimePink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Anime Studio AI",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AnimeGold.copy(alpha = 0.2f))
                                            .border(1.dp, AnimeGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "VIP OWNER",
                                            color = AnimeGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = AnimeSurface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = AnimeSurface,
                            contentColor = TextPrimary,
                            modifier = Modifier.testTag("main_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = state.currentTab == AppTab.STUDIO,
                                onClick = { viewModel.setTab(AppTab.STUDIO) },
                                icon = {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = "Studio", modifier = Modifier.size(20.dp))
                                },
                                label = { Text("स्टूडियो", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
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
                                label = { Text("वीडियो", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
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
                                selected = state.currentTab == AppTab.CHARACTERS,
                                onClick = { viewModel.setTab(AppTab.CHARACTERS) },
                                icon = {
                                    Icon(Icons.Default.Face, contentDescription = "Characters", modifier = Modifier.size(20.dp))
                                },
                                label = { Text("करैक्टर", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
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
                                label = { Text("ओनर व VIP", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
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
                                selected = state.currentTab == AppTab.UPDATES,
                                onClick = { viewModel.setTab(AppTab.UPDATES) },
                                icon = {
                                    Icon(Icons.Default.SystemUpdate, contentDescription = "Updates", modifier = Modifier.size(20.dp))
                                },
                                label = { Text("अपडेट", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnimeGreen,
                                    selectedTextColor = AnimeGreen,
                                    indicatorColor = AnimeGreen.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_updates")
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
                            AppTab.CHARACTERS -> CharacterStudioScreen(viewModel)
                            AppTab.SUBSCRIPTION -> SubscriptionAdminScreen(viewModel)
                            AppTab.UPDATES -> UpdateSettingsScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
