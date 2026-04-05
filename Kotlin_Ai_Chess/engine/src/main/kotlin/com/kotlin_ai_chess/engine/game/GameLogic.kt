package com.kotlin_ai_chess.engine.game

import com.kotlin_ai_chess.core.model.*
import kotlin.math.abs

class GameState(
    val board: Board,
    val currentPlayer: PieceColor,
    val isGameOver: Boolean = false,
    val winner: PieceColor? = null
)

class GameEngine {

    fun isValidMove(state: GameState, move: Move): Boolean {
        val piece = state.board.getPiece(move.from) ?: return false
        if (piece.color != state.currentPlayer) return false

        // 1. Проверка границ доски
        if (!isWithinBounds(state.board, move.to)) return false

        // 2. Нельзя бить своего
        val targetPiece = state.board.getPiece(move.to)
        if (targetPiece?.color == piece.color) return false

        // 3. Специфические правила для фигур
        if (!canPieceMove(state.board, piece, move)) return false

        // 4. Проверка на шах после хода
        val nextBoard = state.board.copy()
        nextBoard.setPiece(move.from, null)
        nextBoard.setPiece(move.to, piece)
        if (isKingUnderAttack(nextBoard, piece.color)) return false

        return true
    }

    private fun isWithinBounds(board: Board, pos: Position): Boolean {
        return pos.x in 0 until board.sizeX &&
                pos.y in 0 until board.sizeY &&
                pos.z in 0 until board.sizeZ
    }

    private fun canPieceMove(board: Board, piece: Piece, move: Move): Boolean {
        val dx = abs(move.to.x - move.from.x)
        val dy = abs(move.to.y - move.from.y)

        return when (piece.type) {
            PieceType.PAWN -> validatePawnMove(board, piece, move)
            PieceType.KNIGHT -> (dx == 1 && dy == 2) || (dx == 2 && dy == 1)
            PieceType.BISHOP -> dx == dy && isPathClear(board, move)
            PieceType.ROOK -> (dx == 0 || dy == 0) && isPathClear(board, move)
            PieceType.QUEEN -> (dx == dy || dx == 0 || dy == 0) && isPathClear(board, move)
            PieceType.KING -> dx <= 1 && dy <= 1
        }
    }

    private fun validatePawnMove(board: Board, piece: Piece, move: Move): Boolean {
        val direction = if (piece.color == PieceColor.WHITE) 1 else -1
        val dx = move.to.x - move.from.x
        val dy = move.to.y - move.from.y
        val target = board.getPiece(move.to)

        // Обычный ход вперед
        if (dx == 0 && dy == direction && target == null) return true

        // Первый ход на 2 клетки
        val startRow = if (piece.color == PieceColor.WHITE) 1 else 6
        if (!piece.hasMoved && dx == 0 && dy == 2 * direction && target == null &&
            board.getPiece(Position(move.from.x, move.from.y + direction)) == null
        ) return true

        // Взятие
        if (abs(dx) == 1 && dy == direction && target != null && target.color != piece.color) return true

        return false
    }

    private fun isPathClear(board: Board, move: Move): Boolean {
        val dx = (move.to.x - move.from.x).let { if (it == 0) 0 else it / abs(it) }
        val dy = (move.to.y - move.from.y).let { if (it == 0) 0 else it / abs(it) }

        var curX = move.from.x + dx
        var curY = move.from.y + dy

        while (curX != move.to.x || curY != move.to.y) {
            if (board.getPiece(Position(curX, curY)) != null) return false
            curX += dx
            curY += dy
        }
        return true
    }

    fun isKingUnderAttack(board: Board, color: PieceColor): Boolean {
        val kingPos = board.getAllPieces().entries.find {
            it.value.type == PieceType.KING && it.value.color == color
        }?.key ?: return false

        val opponentColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        return board.getAllPieces().entries.filter { it.value.color == opponentColor }.any {
            canPieceMove(board, it.value, Move(it.key, kingPos))
        }
    }

    fun applyMove(state: GameState, move: Move): GameState {
        if (!isValidMove(state, move)) return state

        val piece = state.board.getPiece(move.from) ?: return state
        val newBoard = state.board.copy()

        newBoard.setPiece(move.from, null)
        val movedPiece = piece.copy(hasMoved = true)
        newBoard.setPiece(move.to, movedPiece)

        val nextPlayer = if (state.currentPlayer == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        return GameState(
            board = newBoard,
            currentPlayer = nextPlayer
        )
    }
}
