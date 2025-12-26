package com.example.octofit.features.metadata

import com.example.octofit.core.ui.BaseViewModel

class MetadataViewModel : BaseViewModel<MetadataUiState>(
    initialState = MetadataUiState(
        appVersion = "1.0.0",
        lastSyncLabel = "Never",
        isRefreshing = false,
    ),
) {
    fun updateMetadata(appVersion: String, lastSyncLabel: String, isRefreshing: Boolean) {
        updateState {
            copy(
                appVersion = appVersion,
                lastSyncLabel = lastSyncLabel,
                isRefreshing = isRefreshing,
            )
        }
    }
}
