package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class GamePlayUI {
    private final String gameName;
    private static ChessGame.TeamColor color = null;
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final String EMPTY = " ";


    public GamePlayUI(String gameName, ChessGame.TeamColor color) {
        this.gameName = gameName;
        GamePlayUI.color = color;
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawChessBoard(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);
        out.print(EMPTY + " " + EMPTY);

        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h"};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print(EMPTY);
        printHeaderText(out, headerText);
        out.print(EMPTY);

    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawOneRowOfSquares(out, boardRow);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private static void drawOneRowOfSquares(PrintStream out, int boardRow) {
        boolean white = ((boardRow + 1) % 2 == 1);
        int rowNumber = 8 - boardRow;
        out.print(SET_TEXT_COLOR_GREEN + EMPTY + rowNumber + EMPTY);
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if(white) {setYellow(out);}
            else {setBlue(out);}

            if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
                out.print(EMPTY.repeat(prefixLength));
                ChessGame.TeamColor pieceColor = piecesColor(boardRow + 1);
                out.print(getPiece(boardRow + 1, boardCol + 1, pieceColor, out));
                out.print(EMPTY.repeat(suffixLength));
            }
            else {
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            }

            setBlack(out);
            white = !white;
        }

        out.println();
        }
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
    }
    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
    }

    private static ChessPiece.PieceType pieceAt(int row, int col) {
        if(row == 2 || row == 7) {
            return ChessPiece.PieceType.PAWN;
        }
        else if ((row == 1 || row == 8) && (col == 1 || col == 8)) {
            return ChessPiece.PieceType.ROOK;
        }
        else if ((row == 1 || row == 8) && (col == 2 || col == 7)) {
            return ChessPiece.PieceType.KNIGHT;
        }
        else if ((row == 1 || row == 8) && (col == 3 || col == 6)) {
            return ChessPiece.PieceType.BISHOP;
        }
        else if ((row == 1 || row == 8) && (color.equals(ChessGame.TeamColor.WHITE) && col == 4)) {
            return ChessPiece.PieceType.QUEEN;
        }
        else if ((row == 1 || row == 8) && (color.equals(ChessGame.TeamColor.WHITE) && col == 5)) {
            return ChessPiece.PieceType.KING;
        }
        else if ((row == 1 || row == 8) && (color.equals(ChessGame.TeamColor.BLACK) && col == 4)) {
            return ChessPiece.PieceType.KING;
        }
        else if ((row == 1 || row == 8) && (color.equals(ChessGame.TeamColor.BLACK) && col == 5)) {
            return ChessPiece.PieceType.QUEEN;
        }
        else {return null;}
    }

    private static void setWhite1(PrintStream out) {
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void setBlack1(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static String getPiece(int row, int col, ChessGame.TeamColor pieceColor, PrintStream out) {
        var pieceType = pieceAt(row, col);
        if(pieceType == ChessPiece.PieceType.PAWN) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                setWhite1(out);
            }
            else {setBlack1(out);
            }
            return " p ";
        }
        if(pieceType == ChessPiece.PieceType.ROOK) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                setWhite1(out);
            }
            else {setBlack1(out);
            }
            return " r ";
        }
        if(pieceType == ChessPiece.PieceType.KNIGHT) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                setWhite1(out);
            }
            else {setBlack1(out);
            }
            return " n ";
        }
        if(pieceType == ChessPiece.PieceType.BISHOP) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                setWhite1(out);
            }
            else {setBlack1(out);
            }
            return " b ";
        }
        if(pieceType == ChessPiece.PieceType.KING) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                setWhite1(out);
            }
            else {setBlack1(out);
            }
            return " k ";
        }
        if(pieceType == ChessPiece.PieceType.QUEEN) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                setWhite1(out);
            }
            else {setBlack1(out);
            }
            return " q ";
        }
        else {return EMPTY.repeat(3);}
    }

    //Determine what color the pieces on a row will be
    private static ChessGame.TeamColor piecesColor(int row) {
        if(color == ChessGame.TeamColor.WHITE) {
            if(row == 1 || row == 2) {
                return ChessGame.TeamColor.BLACK;
            }
            else if (row == 7 || row == 8) {
                return ChessGame.TeamColor.WHITE;
            }
        }
        else if(color == ChessGame.TeamColor.BLACK) {
            if(row == 1 || row == 2) {
                return ChessGame.TeamColor.WHITE;
            }
            else if (row == 7 || row == 8) {
                return ChessGame.TeamColor.BLACK;
            }
        }
        return null;
    }
}