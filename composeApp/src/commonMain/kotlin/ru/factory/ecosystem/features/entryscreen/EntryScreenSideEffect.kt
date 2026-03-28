package ru.factory.ecosystem.features.entryscreen

import ru.factory.ecosystem.core.ui.viewmodel.UiSideEffect

sealed class EntryScreenSideEffect : UiSideEffect {
    data object Start : EntryScreenSideEffect()
}