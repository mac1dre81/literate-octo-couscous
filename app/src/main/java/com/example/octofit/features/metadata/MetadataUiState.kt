package com.example.octofit.features.metadata

import androidx.compose.runtime.Immutable
import com.example.octofit.core.ui.UiState
import com.example.octofit.features.metadata.data.MetadataEntry

@Immutable
data class MetadataUiState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val metadataEntries: List<MetadataEntry>,
    val searchQuery: String,
    val sections: List<MetadataSectionUiState>,
    val expandedSectionIds: Set<String>,
    val redactedEntryIds: Set<String>,
) : UiState

@Immutable
data class MetadataSectionUiState(
    val id: String,
    val title: String,
    val entries: List<MetadataEntry>,
    val isExpanded: Boolean,
)
