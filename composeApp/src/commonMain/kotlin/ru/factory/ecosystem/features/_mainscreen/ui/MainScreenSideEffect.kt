package ru.factory.ecosystem.features._mainscreen.ui

import ru.factory.ecosystem.core.ui.viewmodel.UiSideEffect

sealed class MainScreenSideEffect : UiSideEffect {
    data class ShowNotification(val message: String) : MainScreenSideEffect()
}