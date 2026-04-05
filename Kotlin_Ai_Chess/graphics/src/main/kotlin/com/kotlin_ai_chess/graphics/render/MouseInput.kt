package com.kotlin_ai_chess.graphics.render

import com.kotlin_ai_chess.core.model.Position
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

class MouseInput(private val window: Window, private val camera: Camera) {

    fun getRayFromMouse(mouseX: Double, mouseY: Double): Vector3f {
        // 1. Нормализованные координаты устройства (NDC)
        val x = (2.0f * mouseX.toFloat()) / window.width - 1.0f
        val y = 1.0f - (2.0f * mouseY.toFloat()) / window.height
        val z = 1.0f
        val rayNDC = Vector3f(x, y, z)

        // 2. Координаты в пространстве клипа
        val rayClip = Vector4f(rayNDC.x, rayNDC.y, -1.0f, 1.0f)

        // 3. Координаты в пространстве глаза (Eye Space)
        val invProj = Matrix4f(camera.getProjectionMatrix()).invert()
        val rayEye = invProj.transform(rayClip)
        rayEye.z = -1.0f
        rayEye.w = 0.0f

        // 4. Координаты в мировом пространстве (World Space)
        val invView = Matrix4f(camera.getViewMatrix()).invert()
        val rayWorld = invView.transform(rayEye)
        
        val result = Vector3f(rayWorld.x, rayWorld.y, rayWorld.z)
        return result.normalize()
    }

    /**
     * Простой алгоритм пересечения луча с плоскостью доски (y = 0)
     */
    fun intersectBoard(mouseX: Double, mouseY: Double): Position? {
        val rayOrigin = camera.position
        val rayDirection = getRayFromMouse(mouseX, mouseY)

        // Плоскость y = 0, нормаль (0, 1, 0)
        // Уравнение луча: P = O + t*D
        // Уравнение плоскости: P * N = 0
        // (O + t*D) * N = 0 => O*N + t*(D*N) = 0 => t = -(O*N) / (D*N)

        val normal = Vector3f(0f, 1f, 0f)
        val denom = rayDirection.dot(normal)

        if (Math.abs(denom) > 1e-6) {
            val t = -rayOrigin.dot(normal) / denom
            if (t >= 0) {
                val intersection = Vector3f(rayDirection).mul(t).add(rayOrigin)
                
                // Переводим 3D координаты обратно в координаты шахматной доски
                val chessX = Math.floor((intersection.x + 4.0).toDouble()).toInt()
                val chessY = Math.floor((intersection.z + 4.0).toDouble()).toInt()
                
                if (chessX in 0..7 && chessY in 0..7) {
                    return Position(chessX, chessY)
                }
            }
        }
        return null
    }
}
