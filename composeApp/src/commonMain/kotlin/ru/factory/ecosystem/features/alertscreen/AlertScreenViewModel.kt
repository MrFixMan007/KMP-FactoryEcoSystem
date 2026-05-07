package ru.factory.ecosystem.features.alertscreen

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.factory.ecosystem.FactoryApi
import ru.factory.ecosystem.core.ui.GlobalState
import ru.factory.ecosystem.core.ui.viewmodel.BaseViewModel

class AlertScreenViewModel(
    val factoryApi: FactoryApi = FactoryApi,
) : BaseViewModel<AlertScreenState, AlertScreenSideEffect>(AlertScreenState()) {

//    fun loadGreeting() {
//        viewModelScope.launch {
//            try {
//                val greeting = factoryApi.getGreeting()
//                setState { it.copy(title = greeting) }
//            } catch (e: Exception) {
//                println("loadGreeting error: $e")
//            }
//        }
//    }

    internal fun init(state: AlertScreenState) {
        viewModelScope.launch {
            setState { state }
        }
    }

    fun onBottomButtonClicked() {
        viewModelScope.launch {
            postSideEffect(effect = AlertScreenSideEffect.NavigateTo(newState = GlobalState.EntryState))
        }
    }
}