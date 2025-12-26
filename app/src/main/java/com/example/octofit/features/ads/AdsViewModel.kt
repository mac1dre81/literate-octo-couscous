package com.example.octofit.features.ads

import com.example.octofit.core.ui.BaseViewModel

class AdsViewModel : BaseViewModel<AdsUiState>(
    initialState = AdsUiState(
        isAdLoaded = false,
        lastLoadErrorMessage = null,
    ),
) {
    fun onAdLoaded() {
        updateState { copy(isAdLoaded = true, lastLoadErrorMessage = null) }
    }

    fun onAdFailed(errorMessage: String) {
        updateState { copy(isAdLoaded = false, lastLoadErrorMessage = errorMessage) }
    }

    fun onAdConsumed() {
        updateState { copy(isAdLoaded = false) }
    }
}
