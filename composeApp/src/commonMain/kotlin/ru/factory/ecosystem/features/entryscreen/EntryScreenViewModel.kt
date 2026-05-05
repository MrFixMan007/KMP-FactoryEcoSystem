package ru.factory.ecosystem.features.entryscreen

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.factory.ecosystem.FactoryApi
import ru.factory.ecosystem.core.ui.viewmodel.BaseViewModel

class EntryScreenViewModel(
    val factoryApi: FactoryApi = FactoryApi
) : BaseViewModel<EntryScreenState, EntryScreenSideEffect>(EntryScreenState()) {

    init {
        loadGreeting()
    }

    fun loadGreeting() {
        viewModelScope.launch {
            try {
                val greeting = factoryApi.getGreeting()
                setState { it.copy(label = greeting) }
            } catch (e: Exception) {
                println("loadGreeting error: $e")
            }
        }
    }
}