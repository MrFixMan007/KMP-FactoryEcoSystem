package ru.factory.ecosystem.core.ui

import ru.factory.ecosystem.core.ui.viewmodel.UiState
import ru.factory.ecosystem.features.alertscreen.AlertScreenState

/**
 * Глобальное состояние приложения. Каждое состояние соответвует каждому экран.
 * MainScreen по состояниям отрисовывает экраны.
 * EntryState - EntryScreen
 * AlertState - AlertScreen
 */

sealed class GlobalState : UiState {

    data object EntryState : GlobalState()
    data class AlertState(
        val screenState: AlertScreenState
    ) : GlobalState()

}