package com.example.octofit.features.ads

import androidx.compose.runtime.Immutable
import com.example.octofit.core.ui.UiState

@Immutable
data class AdsUiState(
    val isAdLoaded: Boolean,
    val lastLoadErrorMessage: String?,
) : UiState
