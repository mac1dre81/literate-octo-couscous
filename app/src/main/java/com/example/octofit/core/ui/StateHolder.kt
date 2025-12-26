package com.example.octofit.core.ui

import kotlinx.coroutines.flow.StateFlow

interface StateHolder<S : UiState> {
    val state: StateFlow<S>
}
