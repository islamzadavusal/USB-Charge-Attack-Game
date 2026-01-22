package com.islamzada.usbchargeattack.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * @param STATE The state type representing UI state
 * @param INTENT The intent type representing user actions
 * @param EFFECT The effect type representing one-time side effects
 */
abstract class BaseViewModel<STATE, INTENT, EFFECT>(
    initialState: STATE
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _effect = Channel<EFFECT>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: STATE
        get() = _state.value

    protected fun setState(reducer: STATE.() -> STATE) {
        _state.value = currentState.reducer()
    }

    protected fun sendEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    abstract fun handleIntent(intent: INTENT)
}