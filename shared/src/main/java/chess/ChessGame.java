package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeam;
    private ChessBoard board;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        findKing(board);
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor currentColor = piece.getTeamColor();
        var initialMoves = piece.pieceMoves(board, startPosition);
        var finalValidMoves = new HashSet<ChessMove>();
        for(ChessMove move: initialMoves) {
            if(makeGhostMove(move, currentColor)) {
                finalValidMoves.add(move);
            }
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KING) {
            for(ChessMove move: finalValidMoves) {
                if(!makeGhostMove(move, currentColor)) {
                    finalValidMoves.remove(move);
                }
            }
        }
        return finalValidMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var validMoves = validMoves(move.getStartPosition());
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(validMoves.contains(move)) {
            board.removePiece(move.getStartPosition());
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);
            if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                findKing(board);
            }
        }
        else {
            throw new InvalidMoveException("move is not in the list of valid moves");
        }
    }

    /*Makes a hypothetical move on the chess board and returns true if that move can be made without putting
    * the king in check*/
    /*RETURNS FALSE IF MAKING THE MOVE PUTS THE KING IN CHECK*/
    public boolean makeGhostMove(ChessMove move, TeamColor teamColor) {
        /*Makes a ghost board identical to the current board*/
        var ghostBoard = new ChessBoard();
        ChessPosition kingPosition;
        if(teamColor == TeamColor.WHITE) {
            kingPosition = whiteKingPosition;
        }
        else{kingPosition = blackKingPosition;}
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                var currentPosition = new ChessPosition(i+1, j+1);
                if(board.getPiece(currentPosition) != null) {
                    var newPieceType = board.getPiece(currentPosition).getPieceType();
                    var newPieceColor = board.getPiece(currentPosition).getTeamColor();
                    var piece = new ChessPiece(newPieceColor, newPieceType);
                    ghostBoard.addPiece(currentPosition, piece);
                    if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPosition = currentPosition;
                    }
                }
            }
        }
        /*Makes a ghost move */
        var ghostPiece = ghostBoard.getPiece(move.getStartPosition());
        ghostBoard.removePiece(move.getStartPosition());
        ghostBoard.removePiece(move.getEndPosition());
        ghostBoard.addPiece(move.getEndPosition(), ghostPiece);
        /*Checks to see if making that ghost move puts the king in check*/
        var movesSet = CheckTeamPieces(teamColor, ghostBoard, kingPosition);
        return movesSet.isEmpty();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        if(teamColor == TeamColor.WHITE) {
            kingPosition = whiteKingPosition;
        }
        else{
            kingPosition = blackKingPosition;
        }
        var movesSet = CheckTeamPieces(teamColor, board, kingPosition);
        return !movesSet.isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            return false;
        }
        else {
            return FindValidMoves(teamColor).isEmpty();
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!FindValidMoves(teamColor).isEmpty()) {
            return false;
        }
        else {
            return isInCheck(teamColor);
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /*Returns a hash set of positions where there are pieces that can take teamColor's king*/
    public Collection<ChessPosition> CheckTeamPieces(TeamColor teamColor, ChessBoard theBoard, ChessPosition kingPosition) {
        var moves = new HashSet<ChessPosition>();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                var startPosition = new ChessPosition(i+1,j+1);
                var piece = theBoard.getPiece(startPosition);
                if(piece != null && theBoard.getPiece(startPosition).getTeamColor() != teamColor) {
                    var testMove = new ChessMove(startPosition, kingPosition, null);
                    /*THIS WOULD HAVE TO CHANGE FROM PIECEMOVES TO VALIDMOVES*/
                    var pieceMoves = piece.pieceMoves(theBoard, startPosition);
                    if(pieceMoves.contains(testMove)) {
                        moves.add(startPosition);
                    }
                }
            }
        }
        return moves;
    }

    /*Returns moves that will not result in teamColor's king being in check*/
    /*If this set it empty, then we are either in Checkmate or Stalemate, depending on if teamColor's king is
    * currently in check or not.*/
    public Collection<ChessMove> FindValidMoves(TeamColor teamColor) {
        var newValidMoves = new HashSet<ChessMove>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                var currentPosition = new ChessPosition(i+1, j+1);
                var piece = board.getPiece(currentPosition);
                if(piece != null && piece.getTeamColor() == teamColor) {
                    var pieceMoves = piece.pieceMoves(board, currentPosition);
                    for(ChessMove move : pieceMoves) {
                        if(makeGhostMove(move, teamColor)) {
                            newValidMoves.add(move);
                        }
                    }
                }
            }
        }
        return newValidMoves;
    }
    public void findKing(ChessBoard theBoard) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                var currentPosition = new ChessPosition(i+1, j+1);
                ChessPiece piece = theBoard.getPiece(currentPosition);
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if(piece.getTeamColor() == TeamColor.WHITE) {
                        whiteKingPosition = currentPosition;
                    }
                    else{ blackKingPosition = currentPosition;}
                }
            }
        }
    }
}
