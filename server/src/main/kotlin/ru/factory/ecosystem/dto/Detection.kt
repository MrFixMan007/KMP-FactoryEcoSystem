package ru.factory.ecosystem.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Detection(
    @SerialName("class_id") val classId: Int,
    val label: String,
    val confidence: Float,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

@Serializable
data class PythonMlResponse(
    val objects: List<Detection>
)