package ru.factory.ecosystem

import com.github.sarxos.webcam.Webcam
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

object CameraPreview {

    private var frame: JFrame? = null
    private var label: JLabel? = null

    @Volatile
    private var lastDetections: List<Detection> = emptyList()

    /**
     * Обновляет список объектов для отрисовки поверх кадра
     */
    fun updateDetections(detections: List<Detection>) {
        lastDetections = detections
    }

    fun show(webcam: Webcam) {
        SwingUtilities.invokeLater {
            frame = JFrame("Camera Preview")
            label = JLabel()

            frame!!.layout = BorderLayout()
            frame!!.add(label, BorderLayout.CENTER)
            frame!!.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame!!.isVisible = true

            // поток обновления картинки
            Thread {
                while (webcam.isOpen) {
                    val image = webcam.image
                    if (image != null) {
                        // Рисуем рамки прямо на BufferedImage перед отображением
                        drawDetections(image)
                        
                        label!!.icon = ImageIcon(image)
                        label!!.repaint()
                        
                        // Подгоняем размер окна под размер изображения
                        if (frame!!.width != image.width || frame!!.height != image.height + 40) {
                            frame!!.size = Dimension(image.width, image.height + 40)
                        }
                    }
                    Thread.sleep(33) // ~30 FPS
                }
            }.start()
        }
    }

    private fun drawDetections(image: BufferedImage) {
        val g2 = image.createGraphics()
        // Включаем сглаживание для лучшего качества текста и линий
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        
        val detections = lastDetections
        
        for (detection in detections) {
            // 1. Отрисовка рамки (Bounding Box)
            g2.color = Color.CYAN
            g2.stroke = BasicStroke(3f)
            g2.drawRect(
                detection.x.toInt(),
                detection.y.toInt(),
                detection.width.toInt(),
                detection.height.toInt()
            )

            // 2. Текст: Название + Процент уверенности
            val info = "${detection.label} ${(detection.confidence * 100).toInt()}%"
            g2.font = Font("SansSerif", Font.BOLD, 14)
            val metrics = g2.fontMetrics
            val tw = metrics.stringWidth(info)
            val th = metrics.height

            // 3. Фон под текст для читаемости (черный полупрозрачный)
            g2.color = Color(0, 0, 0, 160)
            g2.fillRect(detection.x.toInt(), detection.y.toInt() - th, tw + 10, th)

            // 4. Сам текст
            g2.color = Color.WHITE
            g2.drawString(info, detection.x.toInt() + 5, detection.y.toInt() - 5)
        }
        
        g2.dispose()
    }
}
