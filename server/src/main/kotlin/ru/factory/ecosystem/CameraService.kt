package ru.factory.ecosystem

import com.github.sarxos.webcam.Webcam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object CameraService {
    const val TAG = "CameraService"
    const val WIDTH = 640
    const val HEIGHT = 480
    private var webcam: Webcam? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun startScanning() {
        if (job?.isActive == true) return

        job = scope.launch {
            try {
                webcam = openCamera()

                CameraPreview.show(webcam!!)

                while (isActive) {
                    val image = webcam?.image
                    if (image != null) {
                        val baos = ByteArrayOutputStream()
                        ImageIO.write(image, "jpg", baos)
                        val bytes = baos.toByteArray()

                        // Отправляем на Python сервис
                        val result = detectGesturesRaw(bytes)

                        // Обновляем список объектов в превью для отрисовки рамок
                        CameraPreview.updateDetections(result?.objects ?: emptyList())

                        if (result != null && result.objects.isNotEmpty()) {
                            val message =
                                "Gesture found!:${result.objects.first().label} (${(result.objects.first().confidence * 100).toInt()}%)"

                            // Рассылаем всем подключенным клиентам через WebSocket
                            sessions.forEach { session ->
                                try {
                                    session.send(io.ktor.websocket.Frame.Text("NOTIFICATION: $message"))
                                    println("$TAG: NOTIFICATION send:\n$message")
                                } catch (e: Exception) {
                                    // Ошибка отправки конкретному клиенту
                                    println("$TAG: Error response to client:\n${e.message}")
                                }
                            }
                        } else {
                            println("$TAG: Gestures Not found")
                        }
                    }
                    delay(500) // Пауза между кадрами (2 FPS для примера)
                }
            } catch (e: Exception) {
                println("$TAG: Error of Camera:\n${e.message}")
            } finally {
                webcam?.close()
            }
        }
    }

    fun openCamera(): Webcam {
        val webcams = Webcam.getWebcams()

        // 1. пробуем физическую камеру
        val physical = webcams.firstOrNull {
            !it.name.contains("OBS", ignoreCase = true)
        }

        try {
            if (physical != null) {
                println("$TAG: camera ${physical.name} found")
                physical.setViewSize(Dimension(WIDTH, HEIGHT))
                physical.open()
                if (physical.isOpen) return physical
            }
        } catch (e: Exception) {
            println("$TAG: Physical camera failed: ${e.message}")
        }

        throw RuntimeException("No camera available")
    }

    fun stopScanning() {
        CameraPreview.close()
        job?.cancel()
        webcam?.close()
    }
}
