package ru.factory.ecosystem.core.ui

import ru.factory.ecosystem.core.ui.viewmodel.UiState

/**
 * Глобальное состояние приложения. Каждое состояние соответвует каждому экран.
 * MainScreen по состояниям отрисовывает экраны.
 * EntryState - EntryScreen // TODO добавить EntryScreen
 */

sealed class GlobalState : UiState {

    data object EntryState : GlobalState()

}