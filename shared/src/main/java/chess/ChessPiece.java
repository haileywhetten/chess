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
        var moves = new HashSet<ChessMove>();
        boolean validMove = true;
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        var newPosition1 = new ChessPosition(currRow + 1, currCol);
        validMove = isPositionValid(board, newPosition1);

        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition1, null);
            moves.add(newMove1);
        }
        var newPosition2 = new ChessPosition(currRow, currCol + 1);
        validMove = isPositionValid(board, newPosition2);

        if(validMove) {
            var newMove2 = new ChessMove(myPosition, newPosition2, null);
            moves.add(newMove2);
        }
        var newPosition3 = new ChessPosition(currRow - 1, currCol);
        validMove = isPositionValid(board, newPosition3);

        if(validMove) {
            var newMove3 = new ChessMove(myPosition, newPosition3, null);
            moves.add(newMove3);
        }
        var newPosition4 = new ChessPosition(currRow, currCol - 1);
        validMove = isPositionValid(board, newPosition4);

        if(validMove) {
            var newMove4 = new ChessMove(myPosition, newPosition4, null);
            moves.add(newMove4);
        }
        var newPosition5 = new ChessPosition(currRow + 1, currCol + 1);
        validMove = isPositionValid(board, newPosition5);

        if(validMove) {
            var newMove5 = new ChessMove(myPosition, newPosition5, null);
            moves.add(newMove5);
        }
        var newPosition6 = new ChessPosition(currRow + 1, currCol - 1);
        validMove = isPositionValid(board, newPosition6);

        if(validMove) {
            var newMove6 = new ChessMove(myPosition, newPosition6, null);
            moves.add(newMove6);
        }
        var newPosition7 = new ChessPosition(currRow - 1, currCol + 1);
        validMove = isPositionValid(board, newPosition7);

        if(validMove) {
            var newMove7 = new ChessMove(myPosition, newPosition7, null);
            moves.add(newMove7);
        }
        var newPosition8 = new ChessPosition(currRow - 1, currCol - 1);
        validMove = isPositionValid(board, newPosition8);

        if(validMove) {
            var newMove8 = new ChessMove(myPosition, newPosition8, null);
            moves.add(newMove8);
        }
        return moves;
    }
    private Collection<ChessMove> knightMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();
        boolean validMove = true;

        var newPosition1 = new ChessPosition(currRow + 1, currCol + 2);
        validMove = isPositionValid(board, newPosition1);
        if(validMove) {
            var newMove = new ChessMove(myPosition, newPosition1, null);
            moves.add(newMove);
        }
        var newPosition2 = new ChessPosition(currRow + 1, currCol - 2);
        validMove = isPositionValid(board, newPosition2);
        if(validMove) {
            var newMove = new ChessMove(myPosition, newPosition2, null);
            moves.add(newMove);
        }
        var newPosition3 = new ChessPosition(currRow - 1, currCol + 2);
        validMove = isPositionValid(board, newPosition3);
        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition3, null);
            moves.add(newMove1);
        }
        var newPosition4 = new ChessPosition(currRow - 1, currCol - 2);
        validMove = isPositionValid(board, newPosition4);
        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition4, null);
            moves.add(newMove1);
        }
        var newPosition5 = new ChessPosition(currRow + 2, currCol + 1);
        validMove = isPositionValid(board, newPosition5);
        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition5, null);
            moves.add(newMove1);
        }
        var newPosition6 = new ChessPosition(currRow - 2, currCol + 1);
        validMove = isPositionValid(board, newPosition6);
        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition6, null);
            moves.add(newMove1);
        }
        var newPosition7 = new ChessPosition(currRow + 2, currCol - 1);
        validMove = isPositionValid(board, newPosition7);
        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition7, null);
            moves.add(newMove1);
        }
        var newPosition8 = new ChessPosition(currRow - 2, currCol - 1);
        validMove = isPositionValid(board, newPosition8);
        if(validMove) {
            var newMove1 = new ChessMove(myPosition, newPosition8, null);
            moves.add(newMove1);
        }

        return moves;
    }
    private Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        return null;
    }
    private Collection<ChessMove> queenMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        var rookMoves = rookMove(board, myPosition);
        var bishopMoves = bishopMove(board, myPosition);
        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);
        return moves;
    }
    private Collection<ChessMove> rookMove(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        boolean validMove = true;
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        while (validMove) {
            currRow++;
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
