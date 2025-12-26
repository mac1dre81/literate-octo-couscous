package com.example.octofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.octofit.core.ui.theme.OctofitTheme
import com.example.octofit.features.metadata.MetadataUiState
import com.example.octofit.features.metadata.MetadataViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OctofitTheme(useDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MetadataScreen()
                }
            }
        }
    }
}

@Composable
private fun MetadataScreen(
    viewModel: MetadataViewModel = viewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    MetadataContent(
        uiState = uiState,
    )
}

@Composable
private fun MetadataContent(
    uiState: MetadataUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "App version: ${uiState.appVersion}",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Metadata sync: ${uiState.lastSyncLabel}",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
