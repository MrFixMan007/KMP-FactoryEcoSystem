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
import ru.factory.ecosystem.core.ui.viewmodel.BaseViewModel
import ru.factory.ecosystem.createHttpClient
import ru.factory.ecosystem.getPlatform

/**
 * Класс Вью-модель для навигации
 */

class MainScreenViewModel(

) : BaseViewModel<MainScreenState, MainScreenSideEffect>(MainScreenState()) {

    private val client = createHttpClient()

    init {
        // Подключаемся к WebSocket для получения уведомлений
        viewModelScope.launch {
            connectToNotifications()
        }
    }

    private suspend fun connectToNotifications() {
        val platform = getPlatform()
        val host =
            if (platform.name.contains("Android")) SERVER_HOST_FOR_ANDROID_EMULATOR else "localhost"

        try {
            client.webSocket(host = host, port = SERVER_PORT, path = "/notifications") {
                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .collect { frame ->
                        val text = frame.readText()
                        if (text.startsWith("NOTIFICATION:")) {
                            // Отправляем SideEffect для показа уведомления в UI
                            tryPostSideEffect {
                                MainScreenSideEffect.ShowNotification(text.removePrefix("NOTIFICATION: "))
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            println("WebSocket error: ${e.message}")
        }
    }

    fun navigateTo() {
//        setState {
//            it.copy(globalState = GlobalState.CraftState())
//        }
    }

}