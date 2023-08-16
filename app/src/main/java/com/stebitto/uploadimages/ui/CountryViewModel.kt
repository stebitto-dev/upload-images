package com.stebitto.uploadimages.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stebitto.uploadimages.actions.Action
import com.stebitto.uploadimages.statemachines.AppStateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val appStateMachine: AppStateMachine
) : ViewModel() {

    val state = appStateMachine.state

    fun dispatch(action : Action) {
        viewModelScope.launch {
            appStateMachine.dispatch(action)
        }
    }
}