package ru.factory.ecosystem.features.alertscreen

import ru.factory.ecosystem.core.ui.GlobalState
import ru.factory.ecosystem.core.ui.viewmodel.UiSideEffect

sealed class AlertScreenSideEffect : UiSideEffect {
    data class NavigateTo(val newState: GlobalState) : AlertScreenSideEffect()
}