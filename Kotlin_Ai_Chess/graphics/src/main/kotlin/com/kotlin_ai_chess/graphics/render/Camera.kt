package com.kotlin_ai_chess.graphics.render

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera {
    val position = Vector3f(0f, 10f, 10f) // Начальная позиция сверху и сбоку
    val rotation = Vector3f(45f, 0f, 0f) // Наклон вниз

    private val viewMatrix = Matrix4f()
    private val projectionMatrix = Matrix4f()

    fun updateProjectionMatrix(width: Int, height: Int) {
        val aspectRatio = width.toFloat() / height.toFloat()
        projectionMatrix.identity()
        projectionMatrix.perspective(Math.toRadians(60.0).toFloat(), aspectRatio, 0.01f, 1000f)
    }

    fun getViewMatrix(): Matrix4f {
        viewMatrix.identity()
        viewMatrix.rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), Vector3f(1f, 0f, 0f))
            .rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))
            .rotate(Math.toRadians(rotation.z.toDouble()).toFloat(), Vector3f(0f, 0f, 1f))
        viewMatrix.translate(-position.x, -position.y, -position.z)
        return viewMatrix
    }

    fun getProjectionMatrix(): Matrix4f = projectionMatrix
}
