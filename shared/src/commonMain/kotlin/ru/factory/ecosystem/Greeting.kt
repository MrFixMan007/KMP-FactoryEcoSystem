package ru.factory.ecosystem

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello blya, ${platform.name}!"
    }
}