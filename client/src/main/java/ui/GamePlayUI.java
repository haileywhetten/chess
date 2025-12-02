package ui;

import chess.*;
import model.UserData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlayUI {
    private final String gameName;
    private static ChessGame.TeamColor color = null;
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final String EMPTY = " ";
    static ChessGame game = new ChessGame();
    static ChessBoard board = game.getBoard();


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
        help(out);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            String line = scanner.nextLine();

            try {
                result = eval(line, out);

            } catch (Throwable e) {
                var msg = e.toString();
                out.print(msg);
            }
        }
        help(out);
    }

    public String help(PrintStream out) {
        //TODO: Alternate menu for an observer
        out.println(SET_TEXT_COLOR_BLUE + """
                help - list possible commands
                redraw - redraw current chess board
                leave - leave the game
                move <move> - make a move
                resign - resign from the game
                highlight - highlight all legal moves of once piece
                """ + SET_TEXT_COLOR_WHITE);
        return "";
    }

    public String eval(String input, PrintStream out) {
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "leave" -> "leave";
                case "redraw" -> "redraw";
                case "move" -> move(params);
                case "resign" -> "resign";
                case "highlight" -> "highlight";
                default -> help(out);
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);
        out.print(EMPTY + " " + EMPTY);

        String[] headers;
        if(color == ChessGame.TeamColor.WHITE) {headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        }
        else {headers = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};}
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        out.print(RESET_BG_COLOR);
        out.println();
        setBlack(out);
        out.print(SET_TEXT_COLOR_GREEN);

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
        out.println(RESET_BG_COLOR);
    }

    private static void drawOneRowOfSquares(PrintStream out, int boardRow) {

        boolean white = ((boardRow + 1) % 2 == 1);
        int rowNumber;
        if(color == ChessGame.TeamColor.WHITE) {rowNumber = 8 - boardRow;}
        else {rowNumber = boardRow + 1;}
        out.print(SET_TEXT_COLOR_GREEN + EMPTY + rowNumber + EMPTY);
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if(white) {setYellow(out);}
            else {setBlue(out);}

            if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
                out.print(EMPTY.repeat(prefixLength));
                out.print(getPiece(boardRow + 1, boardCol + 1, out, board));
                out.print(EMPTY.repeat(suffixLength));
            }
            else {
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            }

            setBlack(out);
            white = !white;
        }

        out.println(RESET_BG_COLOR);
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
        out.print(SET_BG_COLOR_BLUE);
    }

    private static ChessPiece pieceAt(int row, int col, ChessBoard chessBoard) {
        var position = new ChessPosition(row, col);
        return chessBoard.getPiece(position);
    }

    private static void setWhite1(PrintStream out) {
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void setBlack1(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static String getPiece(int row, int col, PrintStream out, ChessBoard chessBoard) {
        ChessPiece piece;
        if(color == ChessGame.TeamColor.WHITE) {
            piece = pieceAt(9 - row, col, chessBoard);
        }
        else {
            piece = pieceAt(row, 9 - col, chessBoard);
        }
        if(piece == null) {
            return "   ";
        }
        var pieceColor = piece.getTeamColor();
        var pieceType = piece.getPieceType();
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

    private static String move(String[] params) {
        try {
            //TODO: Make sure this is not an observer using boolean
            if (params.length == 0) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Not enough parameters");
            }
            else if (params.length == 1) {
                ChessMove move = getMove(params[0]);
                //TODO: Pawn promotion piece
                game.makeMove(move);
                var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                drawHeaders(out);
                drawChessBoard(out);
            }
            else{
                System.out.println("Too many parameters");
                }

        } catch(Exception ex) {
            System.out.println("Please try again. Enter a valid move.");
        }
        return "";
    }

    private static ChessMove getMove(String moveString) throws Exception {
        if(!moveString.matches("^[a-h][1-8]:[a-h][1-8]$")) {
            throw new Exception();
        }
        String[] tokens = moveString.toLowerCase().split(":");
        int col1 = tokens[0].charAt(0) - 'a' + 1;
        int row1 = tokens[0].charAt(1) - '0' + 1;
        int col2 = tokens[1].charAt(0) - 'a' + 1;
        int row2 = tokens[1].charAt(1) - '0' + 1;
        //TODO: Pawn promotion piece
        return new ChessMove(new ChessPosition(row1, col1), new ChessPosition(row2, col2), null);

    }
}