package com.kotlin_ai_chess.core.model

enum class PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
}

enum class PieceColor {
    WHITE, BLACK
}

data class Piece(
    val type: PieceType,
    val color: PieceColor,
    var hasMoved: Boolean = false,
    val modelPath: String? = null
)

data class Position(
    val x: Int,
    val y: Int,
    val z: Int = 0
) {
    override fun toString(): String = "($x, $y, $z)"
}

class Board(
    val sizeX: Int = 8,
    val sizeY: Int = 8,
    val sizeZ: Int = 1
) {
    private val grid = mutableMapOf<Position, Piece>()

    fun getPiece(pos: Position): Piece? = grid[pos]

    fun setPiece(pos: Position, piece: Piece?) {
        if (piece == null) grid.remove(pos)
        else grid[pos] = piece
    }

    fun getAllPieces(): Map<Position, Piece> = grid.toMap()

    fun setupStandard() {
        grid.clear()
        // Pawns
        for (x in 0 until sizeX) {
            setPiece(Position(x, 1), Piece(PieceType.PAWN, PieceColor.WHITE, modelPath = "models/white_pawn.glb"))
            setPiece(Position(x, 6), Piece(PieceType.PAWN, PieceColor.BLACK, modelPath = "models/black_pawn.glb"))
        }

        // Main pieces
        val pieceOrder = listOf(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        )

        val paths = mapOf(
            PieceType.ROOK to "rook.glb",
            PieceType.KNIGHT to "knight.glb",
            PieceType.BISHOP to "bishop.glb",
            PieceType.QUEEN to "queen.glb",
            PieceType.KING to "king.glb"
        )

        for (x in 0 until sizeX) {
            val type = pieceOrder[x]
            setPiece(Position(x, 0), Piece(type, PieceColor.WHITE, modelPath = "models/white_${paths[type]}"))
            setPiece(Position(x, 7), Piece(type, PieceColor.BLACK, modelPath = "models/black_${paths[type]}"))
        }
    }

    fun copy(): Board {
        val newBoard = Board(sizeX, sizeY, sizeZ)
        grid.forEach { (pos, piece) ->
            newBoard.setPiece(pos, piece.copy())
        }
        return newBoard
    }
}

data class Move(
    val from: Position,
    val to: Position,
    val promotion: PieceType? = null
)
