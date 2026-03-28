package ru.factory.ecosystem.features._mainscreen.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.factory.ecosystem.core.di.InjectorClient
import ru.factory.ecosystem.core.ui.GlobalState
import ru.factory.ecosystem.features.entryscreen.EntryScreen

@Composable
fun MainScreen() {

    val viewModel = InjectorClient.inject<MainScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state.globalState) {
        is GlobalState.EntryState -> {
            EntryScreen()
        }
    }

}