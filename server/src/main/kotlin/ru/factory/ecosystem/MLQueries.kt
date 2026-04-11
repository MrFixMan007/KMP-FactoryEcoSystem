package ru.factory.ecosystem

import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

suspend fun analyzeFrameWithPython(imageBytes: ByteArray): PythonMlResponse? {
    return try {
        // Отправляем POST запрос на локальный Python-сервер
        val response = pythonClient.submitFormWithBinaryData(
            url = "http://127.0.0.1:8001/detect",
            formData = formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"frame.jpg\"")
                })
            }
        )

        // Магия Ktor: автоматически конвертируем ответ в data class
        response.body<PythonMlResponse>()

    } catch (e: Exception) {
        println("❌ Ошибка связи с Python микросервисом: ${e.localizedMessage}")
        null
    }
}