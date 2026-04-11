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
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

// Client to communicate with Python microservice
val pythonClient = HttpClient(CIO) {
    install(ClientContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // Игнорировать лишние поля, если Python пришлет что-то новое
            isLenient = true
            prettyPrint = true
        })
    }
}

fun Application.module() {
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
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/lol") {
            call.respondText("lol bro loool")
        }

        // Наш тестовый GET-маршрут
        get("/test-ml") {
            // 1. Находим файл на диске сервера (путь относительно корня проекта Ktor)
            val file = File("test_images/hand.png")

            if (!file.exists()) {
                call.respondText(
                    "❌ Ошибка: Файл не найден по пути ${file.absolutePath}",
                    status = HttpStatusCode.NotFound
                )
                return@get
            }

            // 2. Читаем байты
            val imageBytes = file.readBytes()
            println("📤 Отправляем фото в Python (${imageBytes.size} байт)...")

            // 3. Вызываем нашу функцию (из прошлого шага), которая стучится в Python
            val pythonResponse: PythonMlResponse? = analyzeFrameWithPython(imageBytes)

            // 4. Возвращаем результат в браузер
            if (pythonResponse != null) {
                // Ktor автоматически превратит PythonMlResponse в красивый JSON
                call.respond(HttpStatusCode.OK, pythonResponse)
            } else {
                call.respondText(
                    "❌ Ошибка: Python-сервер не ответил или вернул ошибку.",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
