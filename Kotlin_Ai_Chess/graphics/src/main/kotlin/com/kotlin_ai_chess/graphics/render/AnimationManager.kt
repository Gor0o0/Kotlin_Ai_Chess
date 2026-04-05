package com.kotlin_ai_chess.graphics.render

import org.joml.Vector3f

class AnimationManager {
    
    data class Animation(
        val pieceId: String, // Уникальный ID фигуры или её начальная позиция
        val startPos: Vector3f,
        val endPos: Vector3f,
        var progress: Float = 0f,
        val duration: Float = 0.5f // Длительность анимации в секундах
    )

    private val activeAnimations = mutableListOf<Animation>()

    fun startAnimation(pieceId: String, start: Vector3f, end: Vector3f) {
        // Удаляем старую анимацию для этой фигуры, если она была
        activeAnimations.removeIf { it.pieceId == pieceId }
        activeAnimations.add(Animation(pieceId, start, end))
    }

    fun update(deltaTime: Float) {
        val iterator = activeAnimations.iterator()
        while (iterator.hasNext()) {
            val anim = iterator.next()
            anim.progress += deltaTime / anim.duration
            if (anim.progress >= 1f) {
                anim.progress = 1f
                iterator.remove()
            }
        }
    }

    fun getAnimatedPosition(pieceId: String, targetPos: Vector3f): Vector3f {
        val anim = activeAnimations.find { it.pieceId == pieceId } ?: return targetPos
        
        // Линейная интерполяция (Lerp)
        val result = Vector3f()
        anim.startPos.lerp(anim.endPos, anim.progress, result)
        
        // Добавим небольшую дугу (прыжок)
        if (anim.progress < 1f) {
            val arcHeight = 1.0f
            result.y += (1.0f - Math.pow((anim.progress * 2.0 - 1.0).toDouble(), 2.0).toFloat()) * arcHeight
        }
        
        return result
    }
    
    fun isAnimating(): Boolean = activeAnimations.isNotEmpty()
}
