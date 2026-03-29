package ru.factory.ecosystem

import io.ktor.client.HttpClient

interface Platform {
    val name: String

    /**
     * Открыть инспектор сетевых соединений клиентов.
     * Для Android chucker.
     */
    fun openInspector()
}

expect fun getPlatform(): Platform

expect fun createHttpClient(): HttpClient