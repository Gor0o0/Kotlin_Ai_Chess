package com.kotlin_ai_chess.graphics.render

import com.kotlin_ai_chess.core.model.Board
import com.kotlin_ai_chess.core.model.Piece
import com.kotlin_ai_chess.core.model.PieceColor
import com.kotlin_ai_chess.core.model.Position
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glBindVertexArray

class Renderer(
    private val shaderManager: ShaderManager, 
    private val modelLoader: ModelLoader,
    private val animationManager: AnimationManager
) {
    
    private val modelCache = mutableMapOf<String, List<ModelLoader.Mesh>>()
    private val modelMatrix = Matrix4f()
    
    // Выделенная фигура
    var selectedPosition: Position? = null
    
    // Параметры освещения
    private val lightPos = Vector3f(4f, 10f, 4f) // Над центром доски
    private val lightColor = Vector3f(1f, 1f, 1f) // Белый свет
    
    fun render(board: Board, camera: Camera) {
        shaderManager.bind()
        
        // Установка матриц камеры
        val viewMatrix = camera.getViewMatrix()
        val projectionMatrix = camera.getProjectionMatrix()
        
        val matrixBuffer = FloatArray(16)
        
        shaderManager.setUniformMatrix4fv("viewMatrix", viewMatrix.get(matrixBuffer))
        shaderManager.setUniformMatrix4fv("projectionMatrix", projectionMatrix.get(matrixBuffer))
        
        // Установка параметров освещения
        shaderManager.setUniformVector3f("lightPos", lightPos)
        shaderManager.setUniformVector3f("lightColor", lightColor)
        shaderManager.setUniformVector3f("viewPos", camera.position)
        
        // Рендеринг всех фигур на доске
        board.getAllPieces().forEach { (pos, piece) ->
            renderPiece(pos, piece)
        }
        
        shaderManager.unbind()
    }
    
    private fun renderPiece(pos: Position, piece: Piece) {
        val path = piece.modelPath ?: return
        val meshes = modelCache.getOrPut(path) {
            try {
                val fullPath = "src/main/resources/$path" 
                modelLoader.loadModel(fullPath)
            } catch (e: Exception) {
                println("Failed to load model $path: ${e.message}")
                emptyList()
            }
        }
        
        // Рассчитываем целевую позицию в 3D пространстве
        val targetX = pos.x.toFloat() - 3.5f
        val targetY = pos.z.toFloat() 
        val targetZ = pos.y.toFloat() - 3.5f
        
        // Получаем анимированную позицию
        // Используем более стабильный ID: тип + цвет + позиция назначения
        val pieceId = "${piece.color}_${piece.type}_${pos.x}_${pos.y}" 
        val animPos = animationManager.getAnimatedPosition(pieceId, Vector3f(targetX, targetY, targetZ))
        
        modelMatrix.identity()
        modelMatrix.translate(animPos.x, animPos.y, animPos.z)
        
        // Если это черные, разворачиваем их на 180 градусов
        if (piece.color == PieceColor.BLACK) {
            modelMatrix.rotateY(Math.toRadians(180.0).toFloat())
        }
        
        // Эффект выделения (фигура приподнимается)
        if (pos == selectedPosition) {
            modelMatrix.translate(0f, 0.5f, 0f)
            modelMatrix.scale(0.55f) // Чуть больше при выделении
        } else {
            modelMatrix.scale(0.5f)
        }
        
        val matrixBuffer = FloatArray(16)
        shaderManager.setUniformMatrix4fv("modelMatrix", modelMatrix.get(matrixBuffer))
        
        // Цвет фигуры с учетом выделения
        val baseColor = if (piece.color == PieceColor.WHITE) Vector3f(0.9f, 0.9f, 0.9f) else Vector3f(0.2f, 0.2f, 0.2f)
        val finalColor = if (pos == selectedPosition) Vector3f(1.0f, 1.0f, 0.0f) else baseColor // Желтый при выделении
        
        shaderManager.setUniformVector3f("objectColor", finalColor)
        
        meshes.forEach { mesh ->
            glBindVertexArray(mesh.vaoId)
            glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0)
        }
    }
    
    fun cleanup() {
        modelCache.values.forEach { modelLoader.cleanup(it) }
    }
}
