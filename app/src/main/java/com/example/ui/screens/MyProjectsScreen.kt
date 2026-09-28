package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.AnimeViewModel

@Composable
fun MyProjectsScreen(viewModel: AnimeViewModel) {
    RecentVideoProjectsScreen(viewModel = viewModel)
}
