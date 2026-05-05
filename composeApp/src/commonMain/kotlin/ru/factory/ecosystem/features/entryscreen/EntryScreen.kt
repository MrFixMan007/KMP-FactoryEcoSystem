package ru.factory.ecosystem.features.entryscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.factory.ecosystem.core.di.InjectorClient

@Composable
fun EntryScreen() {
    val viewModel = InjectorClient.inject<EntryScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    EntryScreenContent(
        label = state.label,
        onMainButonClick = {
            viewModel.loadGreeting()
        }
    )
}