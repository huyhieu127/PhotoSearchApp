package com.huyhieu.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huyhieu.base.contract.Contract
import com.huyhieu.base.contract.EffectHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseVmEffectSimple<EF : Contract.Effect> : ViewModel(), EffectHandler<EF> {
    /**
     * Effect
     * */
    private val _effect = MutableSharedFlow<EF>()

    //val effect = _effect.asSharedFlow()

    override fun onEffect(effect: EF) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    override suspend fun onEffect(effect: (EF) -> Unit) {
        _effect.collect {
            effect(it)
        }
    }
}