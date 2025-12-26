package com.example.octofit.features.metadata

import androidx.compose.runtime.Immutable
import com.example.octofit.core.ui.UiState

@Immutable
data class MetadataUiState(
    val appVersion: String,
    val lastSyncLabel: String,
    val isRefreshing: Boolean,
) : UiState
