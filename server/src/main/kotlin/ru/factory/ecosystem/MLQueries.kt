package ru.factory.ecosystem

import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

const val ML_URL = "http://127.0.0.1:8001"
const val TAG = "MLQueries"

suspend fun analyzeFrameWithPython(imageBytes: ByteArray): PythonMlResponse? {
    return try {
        // Отправляем POST запрос на локальный Python-сервер
        val response = pythonClient.submitFormWithBinaryData(
            url = "$ML_URL/detect",
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
        println("$TAG: Error connect to Python ML: ${e.localizedMessage}")
        null
    }
}

/**
 * Отправка сырых байтов для распознавания жестов
 */
suspend fun detectGesturesRaw(imageBytes: ByteArray): PythonMlResponse? {
    return try {
        val response = pythonClient.post("$ML_URL/detect-raw") {
            contentType(ContentType.Image.JPEG)
            setBody(imageBytes)
        }
        response.body<PythonMlResponse>()
    } catch (e: Exception) {
        println("$TAG: Error connect to Python ML: ${e.localizedMessage}")
        null
    }
}
