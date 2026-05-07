package ru.factory.ecosystem

import com.github.sarxos.webcam.Webcam
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities

object CameraPreview {

    private var frame: JFrame? = null
    private var label: JLabel? = null

    fun show(webcam: Webcam) {
        SwingUtilities.invokeLater {
            frame = JFrame("Camera Preview")
            label = JLabel()

            frame!!.layout = BorderLayout()
            frame!!.add(label, BorderLayout.CENTER)
            frame!!.size = Dimension(800, 600)
            frame!!.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame!!.isVisible = true

            // поток обновления картинки
            Thread {
                while (webcam.isOpen) {
                    val image = webcam.image
                    if (image != null) {
                        label!!.icon = ImageIcon(image)
                        label!!.repaint()
                    }
                    Thread.sleep(33) // ~30 FPS
                }
            }.start()
        }
    }
}