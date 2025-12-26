package com.example.octofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.octofit.core.ui.theme.OctofitTheme
import com.example.octofit.features.metadata.ui.MetadataScreen

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
