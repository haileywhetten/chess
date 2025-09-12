package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return moveCalculator(board, myPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.BISHOP){
            return bishopMove(board, myPosition);
        }
        if(type == PieceType.KING) {
            return kingMove(board, myPosition);
        }
        if(type == PieceType.KNIGHT) {
            return knightMove(board, myPosition);
        }
        if(type == PieceType.PAWN) {
            return pawnMove(board, myPosition);
        }
        if(type == PieceType.QUEEN) {
            return queenMove(board, myPosition);
        }
        if(type == PieceType.ROOK) {
            return rookMove(board, myPosition);
        }
        return null;
    }
    private Collection<ChessMove> bishopMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        boolean validMove = true;
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        while (validMove) {
            currRow++;
            currCol++;
            var newPosition = new ChessPosition(currRow, currCol);
            validMove = isPositionValid(board, newPosition);

            if(validMove) {
                var newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                if(board.getPiece(newPosition) != null && !(board.getPiece(newPosition).getTeamColor().equals(pieceColor))) {
                    validMove = false;
                }
            }

        }
        validMove = true;
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();

        while (validMove) {
            currRow--;
            currCol++;
            var newPosition = new ChessPosition(currRow, currCol);
            validMove = isPositionValid(board, newPosition);

            if(validMove) {
                var newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                if(board.getPiece(newPosition) != null && !(board.getPiece(newPosition).getTeamColor().equals(pieceColor))) {
                    validMove = false;
                }
            }

        }
        validMove = true;
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();

        while (validMove) {
            currRow--;
            currCol--;
            var newPosition = new ChessPosition(currRow, currCol);
            validMove = isPositionValid(board, newPosition);

            if(validMove) {
                var newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                if(board.getPiece(newPosition) != null && !(board.getPiece(newPosition).getTeamColor().equals(pieceColor))) {
                    validMove = false;
                }
            }

        }
        validMove = true;
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();

        while (validMove) {
            currRow++;
            currCol--;
            var newPosition = new ChessPosition(currRow, currCol);
            validMove = isPositionValid(board, newPosition);

            if(validMove) {
                var newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                if(board.getPiece(newPosition) != null && !(board.getPiece(newPosition).getTeamColor().equals(pieceColor))) {
                    validMove = false;
                }
            }

        }
        return moves;
    }
    private Collection<ChessMove> kingMove(ChessBoard board, ChessPosition myPosition) {

        return null;
    }
    private Collection<ChessMove> knightMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        return null;
    }
    private Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        return null;
    }
    private Collection<ChessMove> queenMove(ChessBoard board, ChessPosition myPosition) {
        var queenMoves = new HashSet<ChessMove>();
        Collection<ChessMove> bishopMoves = bishopMove(board, myPosition);
        Collection<ChessMove> rookMoves = rookMove(board, myPosition);
        return null;
    }
    private Collection<ChessMove> rookMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        boolean validMove = true;
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();
        return null;
    }

    private boolean isPositionValid(ChessBoard board, ChessPosition myPosition){
        if (myPosition.getRow() < 1 || myPosition.getColumn() < 1) {
            return false;
        }
        else if (myPosition.getRow() > 8 || myPosition.getColumn() > 8) {
            return false;
        }
        else if (board.getPiece(myPosition) != null && board.getPiece(myPosition).getTeamColor().equals(pieceColor)) {
            return false;
        }
        else {return true; }
    }
}
