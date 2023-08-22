package com.stebitto.uploadimages.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.statemachines.AppStateMachine
import com.stebitto.uploadimages.states.AppState
import com.stebitto.uploadimages.states.CountryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Used only as UI state holder.
 * Business logic and state management are responsibilities of [AppStateMachine]
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val appStateMachine: AppStateMachine
) : ViewModel() {

    private val _state: MutableStateFlow<AppState> = MutableStateFlow(CountryState.Loading)
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            appStateMachine.state.collect { appState -> _state.value = appState }
        }
    }

    fun dispatch(action : Action) {
        viewModelScope.launch {
            appStateMachine.dispatch(action)
        }
    }
}