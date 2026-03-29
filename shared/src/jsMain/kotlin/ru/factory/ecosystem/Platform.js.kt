package ru.factory.ecosystem

import io.ktor.client.HttpClient

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
    override fun openInspector() {
    }
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun createHttpClient(): HttpClient = HttpClient()