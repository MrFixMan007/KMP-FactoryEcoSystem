package ru.factory.ecosystem

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

object FactoryApi {

    private val platform = getPlatform()

    // Determine BASE_URL based on platform and emulator status
    private val BASE_URL = when {
        // Android Emulator uses 10.0.2.2 to access host loopback
        platform.name.contains("Android") -> {
            "http://10.0.2.2:$SERVER_PORT"
        }
        // iOS Simulator and Desktop/Web use localhost
        else -> {
            "http://localhost:$SERVER_PORT"
        }
    }

    private val client = createHttpClient()

    suspend fun getGreeting(): String {
        val response = client.get("$BASE_URL/")
        return response.bodyAsText()
    }

    suspend fun getLol(): String {
        val response = client.get("$BASE_URL/lol")
        return response.bodyAsText()
    }
}