package com.huyhieu.base

import androidx.lifecycle.ViewModel
import com.huyhieu.base.contract.Contract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseVmCompact<S : Contract.State, EV : Contract.Event>(initialState: S) : ViewModel() {

    /**
     * State
     * */
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected fun updateState(onState: (S.() -> S)) {
        _state.update {
            onState(it)
        }
    }

    /**
     * Event
     * */
    abstract fun onEvent(event: EV)
}