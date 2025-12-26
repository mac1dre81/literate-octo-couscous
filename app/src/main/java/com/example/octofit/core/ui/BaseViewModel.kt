package com.example.octofit.core.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : UiState>(initialState: S) : ViewModel(), StateHolder<S> {
    private val mutableState = MutableStateFlow(initialState)

    override val state: StateFlow<S> = mutableState.asStateFlow()

    protected fun updateState(reducer: S.() -> S) {
        mutableState.update { currentState -> currentState.reducer() }
    }
}
