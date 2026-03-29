package ru.factory.ecosystem

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override fun openInspector() {
        // Chucker is Android-only.
    }
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun createHttpClient(): HttpClient = HttpClient(Darwin)