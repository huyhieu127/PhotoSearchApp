package com.huyhieu.base

import androidx.lifecycle.viewModelScope
import com.huyhieu.base.contract.Contract
import com.huyhieu.base.contract.EffectHandler
import com.huyhieu.domain.common.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseVm<S : Contract.State, EV : Contract.Event, EF : Contract.Effect>(initialState: S) : BaseVmCompact<S, EV>(initialState), EffectHandler<EF> {
    /**
     * Effect
     * */
    private val _effect = MutableSharedFlow<EF>()
    //val effect = _effect.asSharedFlow()

    final override fun onEffect(effect: EF) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    final override suspend fun onEffect(effect: (EF) -> Unit) {
        _effect.collect {
            effect(it)
        }
    }

    fun <T> Flow<DataResult<T>>.collectDataVM(
        onLoading: (Boolean) -> Unit = {},
        onSuccess: (T) -> Unit,
        onError: (code: Int, message: String) -> Unit = { _, _ -> },
    ) {
        viewModelScope.launch {
            this@collectDataVM
                .collect {
                    when (it) {
                        is DataResult.Loading<*> -> {
                            onLoading(true)
                        }

                        is DataResult.Success -> {
                            onSuccess(it.data)
                        }

                        is DataResult.Error -> {
                            onError(it.code, it.message)
                        }

                        is DataResult.Complete -> {
                            onLoading(false)
                        }
                    }
                }
        }
    }
}