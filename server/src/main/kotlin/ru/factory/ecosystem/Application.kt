package ru.factory.ecosystem

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import ru.factory.ecosystem.dto.GestureType
import ru.factory.ecosystem.dto.GestureType.Companion.fromValue
import ru.factory.ecosystem.dto.PythonMlResponse
import ru.factory.ecosystem.html_pages.MAIN_PAGE_HTML
import ru.factory.ecosystem.service.CameraService
import ru.factory.ecosystem.service.analyzeFrameWithPython
import java.io.File
import java.util.Collections
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

const val TAG = "Server_application"

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// Хранилище активных WebSocket-сессий
private val sessions = Collections.synchronizedSet<DefaultWebSocketServerSession>(LinkedHashSet())

internal suspend fun PythonMlResponse.processNotifications() {

    val actualGestures = this.objects.filter { it.label != GestureType.NO_GESTURE.value }

    println("$TAG: actual detected gestures: $actualGestures")

    if (actualGestures.isEmpty()) return

    val dangerGestureTypes = GestureType.getDangerGestures()
    val goodGestureTypes = GestureType.getGoodGestures()

    if (actualGestures.any { dangerGestureTypes.contains(fromValue(it.label)) }) {
        println("$TAG: danger detected gestures")
        sendMessageForSessions("danger detected")
    } else if (actualGestures.any { goodGestureTypes.contains(fromValue(it.label)) }) {
        println("$TAG: good detected gestures")
        sendMessageForSessions("good detected")
    } else {
        println("$TAG: warning detected gestures")
        sendMessageForSessions("warning detected")
    }
}

private suspend fun sendMessageForSessions(message: String) {
    sessions.forEach { session ->
        try {
            session.send(Frame.Text(message))
            println("$TAG: send to $session message: $message")
        } catch (e: Exception) {
            println("Ошибка отправки в сокет: ${e.message}")
        }
    }
}

// Client to communicate with Python microservice
val pythonClient = HttpClient(CIO) {
    install(ClientContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

fun Application.module() {
    // Установка WebSockets на сервере
    install(WebSockets) {
        pingPeriodMillis = 15000
        timeoutMillis = 15000
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // 1. Install ContentNegotiation for the SERVER to respond with JSON
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }

    routing {
        // Главная страница с кнопками управления
        get("/index") {
            call.respondText(MAIN_PAGE_HTML, ContentType.Text.Html)
        }

        get("/") {
            call.respondText(Greeting.greet())
        }

        // Эндпоинт для подключения клиентов по WebSocket
        webSocket("/notifications") {
            sessions.add(this)
            try {
                for (frame in incoming) {
                    // Просто держим соединение открытым
                    println("frame $frame")
                }
            } catch (e: ClosedReceiveChannelException) {
                println("📱 Клиент отключился: ${e.localizedMessage}")
            } catch (e: Exception) {
                println("📱 Ошибка соединения: ${e.localizedMessage}")
            } finally {
                sessions.remove(this)
            }
        }

        get("/start-scan") {
            // Запуск сканирования с камеры
            CameraService.startScanning()
            call.respondText("Запуск сканирования")
        }

        get("/stop-scan") {
            CameraService.stopScanning()
            call.respondText("Остановка сканирования")
        }

        get("/lol") {
            call.respondText("lol bro loool")
        }

        // Тестовый маршрут для ручного запуска анализа файла
        get("/test-ml") {
            val file = File("test_images/hand.png")

            if (!file.exists()) {
                call.respondText("Файл не найден", status = HttpStatusCode.NotFound)
                return@get
            }

            val imageBytes = file.readBytes()
            val mlResponse: PythonMlResponse? = analyzeFrameWithPython(imageBytes)

            if (mlResponse != null) {
                mlResponse.processNotifications()
                call.respond(HttpStatusCode.OK, mlResponse)
            } else {
                call.respondText("Ошибка Python ML", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
