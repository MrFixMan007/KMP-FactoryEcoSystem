package ru.factory.ecosystem

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override fun openInspector() {
    }
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(WebSockets)
}