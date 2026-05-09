package ru.factory.ecosystem.features._mainscreen.ui

import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import ru.factory.ecosystem.SERVER_HOST_FOR_ANDROID_EMULATOR
import ru.factory.ecosystem.SERVER_PORT
import ru.factory.ecosystem.core.ui.GlobalState
import ru.factory.ecosystem.core.ui.viewmodel.BaseViewModel
import ru.factory.ecosystem.createHttpClient
import ru.factory.ecosystem.features.alertscreen.AlertScreenState
import ru.factory.ecosystem.getPlatform

/**
 * Класс Вью-модель для навигации
 */

class MainScreenViewModel :
    BaseViewModel<MainScreenState, MainScreenSideEffect>(MainScreenState()) {

    private val client = createHttpClient()

    init {
        reconnect()
    }

    fun reconnect() {
        viewModelScope.launch {
            setState { it.copy(isConnecting = true) }
            connectToNotifications()
        }
    }

    private suspend fun connectToNotifications() {
        val platform = getPlatform()
        val host =
            if (platform.name.contains("Android")) SERVER_HOST_FOR_ANDROID_EMULATOR else "localhost"

        try {
            client.webSocket(host = host, port = SERVER_PORT, path = "/notifications") {
                setState { it.copy(isServerConnected = true, isConnecting = false) }
                tryPostSideEffect {
                    MainScreenSideEffect.ShowNotification("К серверу подключен")
                }

                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .collect { frame ->
                        processNotification(frame.readText())
                    }
            }
            // Если вышли из блока webSocket, значит соединение закрыто
            setState { it.copy(isServerConnected = false, isConnecting = false) }
        } catch (e: Exception) {
            println("WebSocket error: ${e.message}")
            setState { it.copy(isServerConnected = false, isConnecting = false) }
            tryPostSideEffect {
                MainScreenSideEffect.ShowNotification("Ошибка подключения к серверу: ${e.message}")
            }
        }
    }

    private fun processNotification(text: String) {
        if (text.contains("detected")) {
            if (text.contains("danger")) {
                // Отправляем SideEffect для показа уведомления в UI
                navigateTo(
                    GlobalState.AlertState(
                        screenState = AlertScreenState(
                            title = "Внимание, тревога!",
                            descriptionText = "Покиньте помещение!",
                            bottomButonText = "Принято",
                        )
                    )
                )
                tryPostSideEffect {
                    MainScreenSideEffect.ShowNotification("Обнаружена опасность")
                }
            } else if (text.contains("warning")) {
                tryPostSideEffect {
                    MainScreenSideEffect.ShowNotification("Внимание")
                }
            } else if (text.contains("good")) {
                tryPostSideEffect {
                    MainScreenSideEffect.ShowNotification("Опасности нет")
                }
            }
        }
    }

    fun navigateTo(newState: GlobalState) {
        viewModelScope.launch {
            setState {
                it.copy(globalState = newState)
            }
        }
    }

}