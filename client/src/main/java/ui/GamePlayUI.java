package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class GamePlayUI {
    private final String gameName;
    private static ChessGame.TeamColor color = null;
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    //private static final String EMPTY = " ";

    private static Random rand = new Random();

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

        String[] headers = { "a"+EMPTY, "b"+EMPTY, "c"+EMPTY, "d"+EMPTY, "e"+EMPTY, "f"+EMPTY, "g"+EMPTY, "h"+EMPTY };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
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
                // Draw horizontal row separator.
                //drawHorizontalLine(out);
                setBlack(out);
            }
        }
    }

    private static void drawOneRowOfSquares(PrintStream out, int boardRow) {
        boolean white;
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            white = (boardRow + 1 % 2 == 1);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            //setWhite(out);
            if(white) {setYellow(out);}
            else {setBlue(out);}

            if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
                out.print(EMPTY.repeat(prefixLength));
                ChessGame.TeamColor pieceColor = piecesColor(boardRow + 1);
                out.print(SET_TEXT_COLOR_BLACK + getPiece(boardRow + 1, boardCol + 1, pieceColor));
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

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
        //out.print(SET_TEXT_COLOR_YELLOW);
    }
    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
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

    private static String getPiece(int row, int col, ChessGame.TeamColor pieceColor) {
        var pieceType = pieceAt(row, col);
        if(pieceType == ChessPiece.PieceType.PAWN) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                return WHITE_PAWN;
            }
            else {return BLACK_PAWN;}
        }
        if(pieceType == ChessPiece.PieceType.ROOK) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                return WHITE_ROOK;
            }
            else {return BLACK_ROOK;}
        }
        if(pieceType == ChessPiece.PieceType.KNIGHT) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                return WHITE_KNIGHT;
            }
            else {return BLACK_KNIGHT;}
        }
        if(pieceType == ChessPiece.PieceType.BISHOP) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                return WHITE_BISHOP;
            }
            else {return BLACK_BISHOP;}
        }
        if(pieceType == ChessPiece.PieceType.KING) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                return WHITE_KING;
            }
            else {return BLACK_KING;}
        }
        if(pieceType == ChessPiece.PieceType.QUEEN) {
            if(pieceColor == ChessGame.TeamColor.WHITE) {
                return WHITE_QUEEN;
            }
            else {return BLACK_QUEEN;}
        }
        else return EMPTY;
    }

    //Determine what color the pieces on a row will be
    private static ChessGame.TeamColor piecesColor(int row) {
        if(color == ChessGame.TeamColor.WHITE) {
            if(row == 1 || row == 2) {
                return ChessGame.TeamColor.WHITE;
            }
            else if (row == 7 || row == 8) {
                return ChessGame.TeamColor.BLACK;
            }
        }
        else if(color == ChessGame.TeamColor.BLACK) {
            if(row == 1 || row == 2) {
                return ChessGame.TeamColor.BLACK;
            }
            else if (row == 7 || row == 8) {
                return ChessGame.TeamColor.WHITE;
            }
        }
        return null;
    }
}