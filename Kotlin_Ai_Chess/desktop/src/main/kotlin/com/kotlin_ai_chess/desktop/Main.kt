package com.kotlin_ai_chess.desktop

import com.kotlin_ai_chess.core.model.Board
import com.kotlin_ai_chess.core.model.PieceColor
import com.kotlin_ai_chess.core.model.Move
import com.kotlin_ai_chess.core.model.Position
import com.kotlin_ai_chess.engine.game.GameEngine
import com.kotlin_ai_chess.engine.game.GameState
import com.kotlin_ai_chess.graphics.render.*
import com.kotlin_ai_chess.ai.minimax.MinimaxAI
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import java.io.File

fun main() {
    println("Запуск 3D Шахмат с OpenGL...")
    
    val board = Board()
    board.setupStandard()
    
    val engine = GameEngine()
    var state = GameState(board, PieceColor.WHITE)
    val ai = MinimaxAI(engine)
    
    val window = Window(800, 600, "Kotlin 3D Chess - OpenGL")
    window.init()
    
    val shaderManager = ShaderManager()
    val vertexCode = File("src/main/resources/shaders/default.vert").readText()
    val fragmentCode = File("src/main/resources/shaders/default.frag").readText()
    shaderManager.createShader(vertexCode, fragmentCode)
    
    val modelLoader = ModelLoader()
    val animationManager = AnimationManager()
    val renderer = Renderer(shaderManager, modelLoader, animationManager)
    val camera = Camera()
    camera.updateProjectionMatrix(window.width, window.height)
    
    val mouseInput = MouseInput(window, camera)
    
    var lastTime = glfwGetTime()
    
    // Переменные для обработки кликов
    var selectedPos: Position? = null

    glfwSetMouseButtonCallback(window.handle) { _, button, action, _ ->
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
            val x = DoubleArray(1)
            val y = DoubleArray(1)
            glfwGetCursorPos(window.handle, x, y)
            
            val clickedPos = mouseInput.intersectBoard(x[0], y[0])
            if (clickedPos != null) {
                if (selectedPos == null) {
                    // Выбор фигуры
                    val piece = state.board.getPiece(clickedPos)
                    if (piece != null && piece.color == state.currentPlayer) {
                        selectedPos = clickedPos
                        renderer.selectedPosition = clickedPos
                    }
                } else {
                    // Попытка хода
                    val move = Move(selectedPos!!, clickedPos)
                    if (engine.isValidMove(state, move)) {
                        // Запуск анимации перед обновлением состояния
                        val piece = state.board.getPiece(selectedPos!!)!!
                        val start3D = Vector3f(selectedPos!!.x - 3.5f, 0f, selectedPos!!.y - 3.5f)
                        val end3D = Vector3f(clickedPos.x - 3.5f, 0f, clickedPos.y - 3.5f)
                        val pieceId = "${piece.color}_${piece.type}_${clickedPos.x}_${clickedPos.y}"
                        
                        animationManager.startAnimation(pieceId, start3D, end3D)
                        
                        state = engine.applyMove(state, move)
                        println("Игрок сделал ход: $move")
                    }
                    selectedPos = null
                    renderer.selectedPosition = null
                }
            }
        }
    }
    
    while (!window.shouldClose()) {
        val currentTime = glfwGetTime()
        val deltaTime = (currentTime - lastTime).toFloat()
        lastTime = currentTime
        
        window.clear()
        
        animationManager.update(deltaTime)
        renderer.render(state.board, camera)

        window.update()
        
        // Ход ИИ (только если нет активных анимаций)
        if (state.currentPlayer == PieceColor.BLACK && !animationManager.isAnimating()) {
            val bestMove = ai.findBestMove(state)
            if (bestMove != null) {
                val piece = state.board.getPiece(bestMove.from)!!
                val start3D = Vector3f(bestMove.from.x - 3.5f, 0f, bestMove.from.y - 3.5f)
                val end3D = Vector3f(bestMove.to.x - 3.5f, 0f, bestMove.to.y - 3.5f)
                val pieceId = "${piece.color}_${piece.type}_${bestMove.to.x}_${bestMove.to.y}"
                
                animationManager.startAnimation(pieceId, start3D, end3D)
                
                state = engine.applyMove(state, bestMove)
                println("ИИ сделал ход: $bestMove")
            }
        }
    }
    
    renderer.cleanup()
    shaderManager.cleanup()
    window.cleanup()
}

fun printBoard(board: Board) {
    for (y in 7 downTo 0) {
        print("${y + 1} ")
        for (x in 0 until 8) {
            val piece = board.getPiece(com.kotlin_ai_chess.core.model.Position(x, y))
            if (piece == null) {
                print(". ")
            } else {
                val symbol = when (piece.type) {
                    com.kotlin_ai_chess.core.model.PieceType.KING -> "K"
                    com.kotlin_ai_chess.core.model.PieceType.QUEEN -> "Q"
                    com.kotlin_ai_chess.core.model.PieceType.ROOK -> "R"
                    com.kotlin_ai_chess.core.model.PieceType.BISHOP -> "B"
                    com.kotlin_ai_chess.core.model.PieceType.KNIGHT -> "N"
                    com.kotlin_ai_chess.core.model.PieceType.PAWN -> "P"
                }
                val colorSymbol = if (piece.color == PieceColor.WHITE) symbol.uppercase() else symbol.lowercase()
                print("$colorSymbol ")
            }
        }
        println()
    }
    println("  a b c d e f g h")
}
