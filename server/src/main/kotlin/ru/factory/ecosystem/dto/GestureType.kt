package ru.factory.ecosystem.dto

private const val TAG = "GestureType"

enum class GestureType(val value: String) {
    CALL("call"),
    DISLIKE("dislike"),
    FIST("fist"),
    FOUR("four"),
    LIKE("like"),
    MUTE("mute"),
    NO_GESTURE("no_gesture"),
    OK("ok"),
    ONE("one"),
    PALM("palm"),
    PEACE("peace"),
    PEACE_INVERTED("peace_inverted"),
    ROCK("rock"),
    STOP("stop"),
    STOP_INVERTED("stop_inverted"),
    THREE("three"),
    THREE2("three2"),
    TWO_UP("two_up"),
    TWO_UP_INVERTED("two_up_inverted");

    companion object {
        /**
         * Поиск enum по его строковому значению.
         * Возвращает null, если такого жеста не существует.
         */
        fun fromValue(value: String): GestureType {
            // Для Kotlin 1.9 и новее используется entries, для более старых версий — values()
            return entries.find { it.value == value } ?: error("$TAG: $value не найден")
        }

        fun getDangerGestures(): List<GestureType> {
            return listOf(
                STOP,
                STOP_INVERTED,
                MUTE,
                PALM,
                ROCK
            )
        }

        fun getGoodGestures(): List<GestureType> {
            return listOf(
                LIKE,
                OK
            )
        }
    }
}