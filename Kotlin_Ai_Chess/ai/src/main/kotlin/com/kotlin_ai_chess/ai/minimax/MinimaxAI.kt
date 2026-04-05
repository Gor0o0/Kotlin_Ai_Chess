package com.kotlin_ai_chess.ai.minimax

import com.kotlin_ai_chess.engine.game.GameState
import com.kotlin_ai_chess.engine.game.GameEngine
import com.kotlin_ai_chess.core.model.*

class MinimaxAI(private val engine: GameEngine) {

    /**
     * Находит лучший ход для текущего игрока.
     * Для примитивного ИИ используем небольшую глубину.
     */
    fun findBestMove(state: GameState, depth: Int = 2): Move? {
        val possibleMoves = getAllValidMoves(state)
        if (possibleMoves.isEmpty()) return null

        var bestMove: Move? = null
        var bestValue = if (state.currentPlayer == PieceColor.WHITE) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY

        for (move in possibleMoves) {
            val nextState = engine.applyMove(state, move)
            val boardValue = minimax(nextState, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

            if (state.currentPlayer == PieceColor.WHITE) {
                if (boardValue > bestValue) {
                    bestValue = boardValue
                    bestMove = move
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue
                    bestMove = move
                }
            }
        }
        return bestMove
    }

    private fun minimax(state: GameState, depth: Int, alpha: Double, beta: Double): Double {
        if (depth == 0) return evaluateBoard(state.board)

        val moves = getAllValidMoves(state)
        if (moves.isEmpty()) {
            return if (engine.isKingUnderAttack(state.board, state.currentPlayer)) {
                if (state.currentPlayer == PieceColor.WHITE) -10000.0 else 10000.0 // Мат
            } else 0.0 // Пат
        }

        var currentAlpha = alpha
        var currentBeta = beta

        if (state.currentPlayer == PieceColor.WHITE) {
            var maxEval = Double.NEGATIVE_INFINITY
            for (move in moves) {
                val eval = minimax(engine.applyMove(state, move), depth - 1, currentAlpha, currentBeta)
                maxEval = maxOf(maxEval, eval)
                currentAlpha = maxOf(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break
            }
            return maxEval
        } else {
            var minEval = Double.POSITIVE_INFINITY
            for (move in moves) {
                val eval = minimax(engine.applyMove(state, move), depth - 1, currentAlpha, currentBeta)
                minEval = minOf(minEval, eval)
                currentBeta = minOf(currentBeta, eval)
                if (currentBeta <= currentAlpha) break
            }
            return minEval
        }
    }

    private fun evaluateBoard(board: Board): Double {
        var score = 0.0
        board.getAllPieces().forEach { (_, piece) ->
            val value = when (piece.type) {
                PieceType.PAWN -> 10.0
                PieceType.KNIGHT -> 30.0
                PieceType.BISHOP -> 30.0
                PieceType.ROOK -> 50.0
                PieceType.QUEEN -> 90.0
                PieceType.KING -> 900.0
            }
            score += if (piece.color == PieceColor.WHITE) value else -value
        }
        return score
    }

    private fun getAllValidMoves(state: GameState): List<Move> {
        val moves = mutableListOf<Move>()
        val board = state.board
        board.getAllPieces().forEach { (pos, piece) ->
            if (piece.color == state.currentPlayer) {
                // В идеале тут нужен генератор ходов, но пока перебором всех клеток
                for (x in 0 until board.sizeX) {
                    for (y in 0 until board.sizeY) {
                        val to = Position(x, y)
                        val move = Move(pos, to)
                        if (engine.isValidMove(state, move)) {
                            moves.add(move)
                        }
                    }
                }
            }
        }
        return moves
    }
}
