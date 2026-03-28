package ru.factory.ecosystem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform