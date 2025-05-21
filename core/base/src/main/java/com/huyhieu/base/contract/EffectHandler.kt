package com.huyhieu.base.contract

interface EffectHandler<EF : Contract.Effect> {
    fun onEffect(effect: EF)
    suspend fun onEffect(effect: (EF) -> Unit)
}