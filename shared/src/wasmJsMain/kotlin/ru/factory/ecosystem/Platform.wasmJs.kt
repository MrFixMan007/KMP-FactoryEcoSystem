package ru.factory.ecosystem

import io.ktor.client.HttpClient

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override fun openInspector() {
    }
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun createHttpClient(): HttpClient = HttpClient()