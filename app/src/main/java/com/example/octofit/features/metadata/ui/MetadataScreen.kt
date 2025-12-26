package com.example.octofit.features.metadata.ui

import android.app.Application
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.octofit.features.ads.AdMobBanner
import com.example.octofit.features.ads.rememberInterstitialAdController
import com.example.octofit.features.metadata.MetadataSectionUiState
import com.example.octofit.features.metadata.MetadataUiState
import com.example.octofit.features.metadata.MetadataViewModel
import com.example.octofit.features.metadata.MetadataViewModelFactory
import com.example.octofit.features.metadata.data.MetadataEntry

@Composable
fun MetadataScreen(
    viewModel: MetadataViewModel = viewModel(
        factory = MetadataViewModelFactory(
            application = LocalContext.current.applicationContext as Application,
        ),
    ),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val clipboardManager = LocalClipboardManager.current
    val interstitialController = rememberInterstitialAdController()
    val picker = rememberLauncherForActivityResult(OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    MetadataContent(
        uiState = uiState,
        onPickImage = { picker.launch(arrayOf("image/*")) },
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onToggleSection = viewModel::onToggleSection,
        onToggleRedaction = viewModel::onToggleRedaction,
        onDismissError = viewModel::onDismissError,
        onCopyAllMetadata = {
            val payload = viewModel.onCopyAllMetadata()
            clipboardManager.copyToClipboard(payload)
            activity?.let { interstitialController.showIfReady(it) }
        },
        onShareAllMetadata = {
            val payload = viewModel.onShareAllMetadata()
            context.startActivity(payload.toShareIntent())
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetadataContent(
    uiState: MetadataUiState,
    onPickImage: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleSection: (String) -> Unit,
    onToggleRedaction: (String) -> Unit,
    onDismissError: () -> Unit,
    onCopyAllMetadata: () -> Unit,
    onShareAllMetadata: () -> Unit,
) {
    val hasSharableEntries = uiState.metadataEntries.any { entry ->
        entry.id !in uiState.redactedEntryIds
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Image Metadata") },
                actions = {
                    IconButton(onClick = onCopyAllMetadata, enabled = hasSharableEntries) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy metadata")
                    }
                    IconButton(onClick = onShareAllMetadata, enabled = hasSharableEntries) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share metadata")
                    }
                },
            )
        },
        bottomBar = {
            AdMobBanner(
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Select an image to inspect metadata.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(onClick = onPickImage) {
                            Icon(imageVector = Icons.Default.FileOpen, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Choose Image")
                        }
                        if (uiState.isLoading) {
                            Spacer(modifier = Modifier.width(16.dp))
                            CircularProgressIndicator(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Privacy notice",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = "• Images are selected using the system file picker (Storage Access Framework only).",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "• Metadata is extracted on-device and stays local unless you copy or share it.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "• Review entries for sensitive data (GPS, device identifiers) and redact before sharing.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Search metadata") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                singleLine = true,
                enabled = uiState.metadataEntries.isNotEmpty(),
            )

            MetadataSections(
                sections = uiState.sections,
                onToggleSection = onToggleSection,
                redactedEntryIds = uiState.redactedEntryIds,
                onToggleRedaction = onToggleRedaction,
                emptyStateText = if (uiState.metadataEntries.isEmpty()) {
                    "No metadata loaded yet."
                } else {
                    "No metadata matches your search."
                },
            )
        }

        if (uiState.errorMessage != null) {
            AlertDialog(
                onDismissRequest = onDismissError,
                confirmButton = {
                    TextButton(onClick = onDismissError) {
                        Text(text = "OK")
                    }
                },
                title = { Text(text = "Metadata error") },
                text = { Text(text = uiState.errorMessage) },
            )
        }
    }
}

@Composable
private fun MetadataSections(
    sections: List<MetadataSectionUiState>,
    onToggleSection: (String) -> Unit,
    redactedEntryIds: Set<String>,
    onToggleRedaction: (String) -> Unit,
    emptyStateText: String,
) {
    if (sections.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = emptyStateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(sections, key = { it.id }) { section ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(
                        title = section.title,
                        count = section.entries.size,
                        isExpanded = section.isExpanded,
                        onToggle = { onToggleSection(section.id) },
                    )
                    if (section.isExpanded) {
                        Divider()
                        SectionEntries(
                            entries = section.entries,
                            redactedEntryIds = redactedEntryIds,
                            onToggleRedaction = onToggleRedaction,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "$count entries",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse section" else "Expand section",
            )
        }
    }
}

@Composable
private fun SectionEntries(
    entries: List<MetadataEntry>,
    redactedEntryIds: Set<String>,
    onToggleRedaction: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        entries.forEach { entry ->
            val isRedacted = redactedEntryIds.contains(entry.id)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.key,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            text = if (isRedacted) "Redacted" else entry.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    TextButton(onClick = { onToggleRedaction(entry.id) }) {
                        Text(text = if (isRedacted) "Restore" else "Redact")
                    }
                }
            }
        }
    }
}

private fun ClipboardManager.copyToClipboard(value: String) {
    setText(AnnotatedString(value))
}

private fun String.toShareIntent(): Intent {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, this@toShareIntent)
    }
    return Intent.createChooser(intent, "Share metadata")
}

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}
