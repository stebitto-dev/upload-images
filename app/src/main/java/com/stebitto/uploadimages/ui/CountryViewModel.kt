package com.stebitto.uploadimages.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.statemachines.AppStateMachine
import com.stebitto.uploadimages.states.CountryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val appStateMachine: AppStateMachine
) : ViewModel() {

    private val _state: MutableStateFlow<CountryState> = MutableStateFlow(CountryState.Loading)
    val state: StateFlow<CountryState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            appStateMachine.state.collect {
                _state.value = it
            }
        }
    }

    fun dispatch(action : Action) {
        viewModelScope.launch {
            appStateMachine.dispatch(action)
        }
    }
}