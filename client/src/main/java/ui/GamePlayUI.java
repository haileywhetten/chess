package ui;

import chess.*;
import com.google.gson.Gson;
import model.UserData;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class GamePlayUI implements ServerMessageHandler{
    private final String gameName;
    private static ChessGame.TeamColor color = null;
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final String EMPTY = " ";
    private static boolean observer;
    private static WebSocketFacade facade = null;
    private static int gameID;
    private static String auth;
    private static ChessGame currentGame;


    public GamePlayUI(String gameName, ChessGame.TeamColor color, boolean observer, String url, int gameID, String auth) throws Exception {
        this.gameName = gameName;
        GamePlayUI.color = color;
        GamePlayUI.observer = observer;
        facade = new WebSocketFacade(url, this);
        GamePlayUI.gameID = gameID;
        GamePlayUI.auth = auth;
        facade.connectToGame(gameID, auth);
    }

    public static String getColorString() {
        if(color.equals(ChessGame.TeamColor.WHITE)) {return "white";}
        else {return "black";}
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);


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
        out.println(SET_TEXT_COLOR_RED + "You have left the game.");
    }

    public static String help(PrintStream out) {
        if(!observer) {
            out.println(SET_TEXT_COLOR_BLUE + """
                help - list possible commands
                redraw - redraw current chess board
                leave - leave the game
                move <move> - make a move
                resign - resign from the game
                highlight - highlight all legal moves of once piece
                """ + SET_TEXT_COLOR_WHITE);
        } else {
            out.println(SET_TEXT_COLOR_BLUE + """
                help - list possible commands
                redraw - redraw current chess board
                leave - leave the game
                highlight - highlight all legal moves of once piece
                """ + SET_TEXT_COLOR_WHITE);
        }

        return "";
    }

    public String eval(String input, PrintStream out) {
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "leave" -> "leave";
                case "redraw" -> redraw(out);
                case "move" -> move(params);
                case "resign" -> resign(out);
                case "highlight" -> highlight(params);
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

    private static void drawChessBoard(PrintStream out, ChessBoard board) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawOneRowOfSquares(out, boardRow, board);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
        out.println(RESET_BG_COLOR);
    }

    private static void drawOneRowOfSquares(PrintStream out, int boardRow, ChessBoard board) {

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
        /*if(observer) {
            System.out.println("You are an observer and cannot move.");
            return "b";}*/
        try {
            if (params.length == 0) {
                System.out.println(SET_TEXT_COLOR_GREEN + "Not enough parameters");
            }
            else if (params.length == 1) {
                ChessMove move = getMove(params[0], currentGame.getBoard());
                facade.makeMove(gameID, auth, move);
                var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                drawHeaders(out);
                drawChessBoard(out, currentGame.getBoard());
            }
            else{
                System.out.println("Too many parameters");
                }

        } catch(Exception ex) {
            System.out.println("Please try again. Enter a valid move.");
        }
        return "";
    }

    private static ChessMove getMove(String moveString, ChessBoard board) throws Exception {
        if(!moveString.matches("^[a-h][1-8]:[a-h][1-8]$")) {
            throw new Exception();
        }
        String[] tokens = moveString.toLowerCase().split(":");
        int col1 = tokens[0].charAt(0) - 'a' + 1;
        int row1 = tokens[0].charAt(1) - '0';
        int col2 = tokens[1].charAt(0) - 'a' + 1;
        int row2 = tokens[1].charAt(1) - '0';
        ChessPosition start = new ChessPosition(row1, col1);
        ChessPosition end = new ChessPosition(row2, col2);
        boolean endOfBoardPawn = false;
        if((color == ChessGame.TeamColor.WHITE && row2 == 8) || (color == ChessGame.TeamColor.BLACK && row2 == 1)) {
            endOfBoardPawn = true;
        }
        if((board.getPiece(start).getPieceType() == ChessPiece.PieceType.PAWN) && (endOfBoardPawn)) {
            System.out.println(SET_BG_COLOR_WHITE + "Your pawn is being promoted! What would you like to promote it to? <ROOK|KNIGHT|BISHOP|QUEEN>");
            System.out.print(RESET_BG_COLOR);
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine().toLowerCase();
                switch (line) {
                    case "bishop" -> {
                        return new ChessMove(start, end, ChessPiece.PieceType.BISHOP);
                    }
                    case "rook" -> {
                        return new ChessMove(start, end, ChessPiece.PieceType.ROOK);
                    }
                    case "knight" -> {
                        return new ChessMove(start, end, ChessPiece.PieceType.KNIGHT);
                    }
                    case "queen" -> {
                        return new ChessMove(start, end, ChessPiece.PieceType.QUEEN);
                    }
                    default -> System.out.println("Please enter a valid piece type.");
            }


        }
        else {
            return new ChessMove(start, end, null);
        }
        return null;

    }

    private static String highlight(String[] params) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        try {
            if (params.length == 0) {
                System.out.println(SET_TEXT_COLOR_GREEN + "Not enough parameters");
            }
            else if (params.length == 1) {
                ChessPosition startPosition = getStartingPosition(params[0]);
                drawHeaders(out);
                highlightedSquares(out, startPosition);

                out.println(RESET_BG_COLOR);
            }
            else{
                System.out.println("Too many parameters");
            }


        } catch(Exception ex) {
            out.println("Please enter a valid position");
        }
        return "";
    }

    private static void highlightedSquares (PrintStream out, ChessPosition startPosition) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            boolean white = ((boardRow + 1) % 2 == 1);
            int rowNumber;
            if(color == ChessGame.TeamColor.WHITE) {rowNumber = 8 - boardRow;}
            else {rowNumber = boardRow + 1;}
            out.print(SET_TEXT_COLOR_GREEN + EMPTY + rowNumber + EMPTY);
            for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {

                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                    ChessPosition endPosition;
                    if(color == ChessGame.TeamColor.BLACK) {
                        endPosition = new ChessPosition(boardRow + 1, 8 - boardCol);
                    }
                    else {endPosition = new ChessPosition(8 - boardRow, boardCol + 1);}
                    Collection<ChessPosition> squares = squaresToHighlight(currentGame.validMoves(startPosition));
                    squares.add(startPosition);
                    if(!squares.contains(endPosition)) {
                        if(white) {setYellow(out);}
                        else {setBlue(out);}
                    }
                    else {
                        if(white) {out.print(SET_BG_COLOR_DARK_GREEN);}
                        else {out.print(SET_BG_COLOR_GREEN);}
                    }


                    if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
                        out.print(EMPTY.repeat(prefixLength));
                        out.print(getPiece(boardRow + 1, boardCol + 1, out, currentGame.getBoard()));
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

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private static ChessPosition getStartingPosition(String positionString) throws Exception {
        if(!positionString.matches("^[a-h][1-8]$")) {
            throw new Exception();
        }
        int col1 = positionString.charAt(0) - 'a' + 1;
        int row1 = positionString.charAt(1) - '0';
        return new ChessPosition(row1, col1);
    }

    private static Collection<ChessPosition> squaresToHighlight(Collection<ChessMove> moves) {
        List<ChessPosition> squares = new ArrayList<>(List.of());
        for(ChessMove move : moves) {
            squares.add(move.getEndPosition());
        }
        return squares;
    }

    private static String resign(PrintStream out) {
        if(observer) {
            System.out.println("You are an observer and cannot resign.");
            return "b";}
        out.println(SET_TEXT_COLOR_YELLOW + "Are you sure you want to resign? Resigning results in a forfeit.");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().toLowerCase();
        if(line.equals("yes")) {
            drawHeaders(out);
            drawChessBoard(out, currentGame.getBoard());
            out.println("You have resigned. Type leave to leave the game.");
        } else if (line.equals("no")) {
            out.println("You did not resign.");
        }
        else {
            out.println("Invalid input. Try again");
        }
        help(out);
        return"";
    }
    private static String redraw(PrintStream out) {
        drawHeaders(out);
        drawChessBoard(out, currentGame.getBoard());
        return "";
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_MAGENTA + notification.getServerMessage());
    }

    @Override
    public void loadGame(ServerMessage notification) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawHeaders(out);
        currentGame = notification.getGame();
        ChessBoard board = currentGame.getBoard();
        drawChessBoard(out, board);
    }

    @Override
    public void error(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_MAGENTA + notification.getServerMessage());
    }

    /*
    *
    * */

}