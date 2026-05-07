package ru.factory.ecosystem

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashSet

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// Хранилище активных WebSocket-сессий
val sessions = Collections.synchronizedSet<DefaultWebSocketServerSession>(LinkedHashSet())

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
                val message = "Обнаружено объектов: ${mlResponse.objects.size}. Первый: ${mlResponse.objects.firstOrNull()?.label}"
                
                sessions.forEach { session ->
                    try {
                        session.send(Frame.Text("NOTIFICATION: $message"))
                    } catch (e: Exception) {
                        println("Ошибка отправки в сокет: ${e.message}")
                    }
                }

                call.respond(HttpStatusCode.OK, mlResponse)
            } else {
                call.respondText("Ошибка Python ML", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
