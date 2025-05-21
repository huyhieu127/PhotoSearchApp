package com.huyhieu.library.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.huyhieu.base.contract.Contract
import com.huyhieu.base.BaseVm
import com.huyhieu.base.BaseVmEffectSimple

@Composable
fun <EF : Contract.Effect> LaunchEffectContract(viewmodel: BaseVmEffectSimple<EF>, effect: (EF) -> Unit) {
    LaunchedEffect(Unit) {
        viewmodel.onEffect(effect)
    }
}
@Composable
fun <S : Contract.State, EV : Contract.Event, EF : Contract.Effect> LaunchEffectContract(viewmodel: BaseVm<S, EV, EF>, effect: (EF) -> Unit) {
    LaunchedEffect(Unit) {
        viewmodel.onEffect(effect)
    }
}