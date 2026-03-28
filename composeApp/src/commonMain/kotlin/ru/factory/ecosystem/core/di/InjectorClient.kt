package ru.factory.ecosystem.core.di

import ru.factory.ecosystem.features._mainscreen.ui.MainScreenViewModel
import ru.factory.ecosystem.features.entryscreen.EntryScreenViewModel
import kotlin.reflect.KClass

/**
 * Синглтон для предоставления зависимостей.
 */

object InjectorClient {

    private val _instances = mutableMapOf<KClass<*>, Any>()
    val instances: Map<KClass<*>, Any> = _instances

    inline fun <reified T : Any> inject(): T {
        return instances[T::class] as? T
            ?: error("From InjectorClient: No provider for ${T::class}")
    }

    private inline fun <reified T : Any> register(instance: T) {
        _instances[T::class] = instance
    }

    init {
        register(MainScreenViewModel())
        register(EntryScreenViewModel())
    }
}