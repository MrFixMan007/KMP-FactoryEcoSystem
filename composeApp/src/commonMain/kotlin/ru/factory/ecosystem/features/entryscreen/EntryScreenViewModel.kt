package ru.factory.ecosystem.features.entryscreen

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.factory.ecosystem.FactoryApi
import ru.factory.ecosystem.core.ui.viewmodel.BaseViewModel

class EntryScreenViewModel(
    val factoryApi: FactoryApi = FactoryApi
) : BaseViewModel<EntryScreenState, EntryScreenSideEffect>(EntryScreenState()) {

    init {
        viewModelScope.launch {
            setGreetings()
        }
    }

    private suspend fun setGreetings() {
        try {
            val greeting = factoryApi.getLol()

            setState {
                it.copy(label = greeting)
            }
        } catch (e: Exception) {
            println("setGreetings error: $e")
        }
    }
}