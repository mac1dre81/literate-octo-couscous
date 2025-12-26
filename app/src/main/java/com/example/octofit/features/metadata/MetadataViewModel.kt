package com.example.octofit.features.metadata

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.octofit.core.ui.BaseViewModel
import com.example.octofit.features.metadata.data.MetadataEntry
import com.example.octofit.features.metadata.data.MetadataExtractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MetadataViewModel(
    application: Application,
) : BaseViewModel<MetadataUiState>(
    initialState = MetadataUiState(
        isLoading = false,
        errorMessage = null,
        metadataEntries = emptyList(),
        searchQuery = "",
        sections = emptyList(),
        expandedSectionIds = emptySet(),
        redactedEntryIds = emptySet(),
    ),
) {
    private val extractor = MetadataExtractor(application.applicationContext)
    private var loadJob: Job? = null

    fun onImageSelected(uri: Uri) {
        loadJob?.cancel()
        updateState {
            copy(isLoading = true, errorMessage = null)
        }
        loadJob = viewModelScope.launch {
            try {
                val entries = extractor.extract(uri)
                updateState { buildState(entries = entries, redactedEntryIds = emptySet()) }
            } catch (error: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = "Unable to read metadata. Please try a different image.",
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        updateState { buildState(searchQuery = query) }
    }

    fun onToggleSection(sectionId: String) {
        updateState {
            val updated = expandedSectionIds.toMutableSet().apply {
                if (contains(sectionId)) remove(sectionId) else add(sectionId)
            }
            copy(expandedSectionIds = updated).let { it.buildState() }
        }
    }

    fun onDismissError() {
        updateState { copy(errorMessage = null) }
    }

    fun onCopyAllMetadata(): String = formatAsJson(state.value.metadataEntries)

    fun onShareAllMetadata(): String = formatAsJson(state.value.metadataEntries)

    fun onToggleRedaction(entryId: String) {
        updateState {
            val updated = redactedEntryIds.toMutableSet().apply {
                if (contains(entryId)) remove(entryId) else add(entryId)
            }
            copy(redactedEntryIds = updated)
        }
    }

    private fun MetadataUiState.buildState(
        entries: List<MetadataEntry> = metadataEntries,
        searchQuery: String = this.searchQuery,
        redactedEntryIds: Set<String> = this.redactedEntryIds,
    ): MetadataUiState {
        val trimmedQuery = searchQuery.trim()
        val filteredEntries = if (trimmedQuery.isBlank()) {
            entries
        } else {
            val query = trimmedQuery.lowercase()
            entries.filter {
                it.key.lowercase().contains(query) ||
                    it.value.lowercase().contains(query) ||
                    it.sourceTag.lowercase().contains(query)
            }
        }
        val grouped = filteredEntries.groupBy { it.sourceTag }
        val sections = grouped.entries.sortedBy { it.key }.map { (tag, entriesForTag) ->
            MetadataSectionUiState(
                id = tag,
                title = tag,
                entries = entriesForTag,
                isExpanded = expandedSectionIds.contains(tag),
            )
        }
        val validRedactions = redactedEntryIds.intersect(entries.map { it.id }.toSet())
        return copy(
            isLoading = false,
            metadataEntries = entries,
            searchQuery = searchQuery,
            sections = sections,
            redactedEntryIds = validRedactions,
        )
    }

    private fun formatAsJson(entries: List<MetadataEntry>): String {
        val payload = JSONArray()
        val redactions = state.value.redactedEntryIds
        entries.filterNot { redactions.contains(it.id) }.forEach { entry ->
            val item = JSONObject()
            item.put("key", entry.key)
            item.put("value", entry.value)
            item.put("source", entry.sourceTag)
            payload.put(item)
        }
        val envelope = JSONObject()
        envelope.put("exportedAt", nowLabel())
        envelope.put("entries", payload)
        return envelope.toString(2)
    }

    private fun nowLabel(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
        return formatter.format(Instant.now())
    }
}
