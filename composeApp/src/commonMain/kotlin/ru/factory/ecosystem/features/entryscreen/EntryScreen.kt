package ru.factory.ecosystem.features.entryscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.factory.ecosystem.core.di.InjectorClient
import ru.factory.ecosystem.features._mainscreen.ui.MainScreenViewModel

@Composable
fun EntryScreen() {

    val viewModel = InjectorClient.inject<EntryScreenViewModel>()
    val mainViewModel = InjectorClient.inject<MainScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()

    EntryScreenContent(
        label = state.label,
        onMainButonClick = {}
    )

}