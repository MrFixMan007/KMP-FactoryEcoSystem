package ru.factory.ecosystem.features.alertscreen

import ru.factory.ecosystem.core.ui.viewmodel.UiState

data class AlertScreenState(
    val title: String = "!",
    val descriptionText: String = "!",
    val bottomButonText: String = "!",
) : UiState