package ru.factory.ecosystem.features._mainscreen.ui

import ru.factory.ecosystem.core.ui.GlobalState
import ru.factory.ecosystem.core.ui.viewmodel.UiState

data class MainScreenState(
    val label: String = "Start",
    val globalState: GlobalState = GlobalState.EntryState,
    val isServerConnected: Boolean = false,
    val isConnecting: Boolean = false
) : UiState