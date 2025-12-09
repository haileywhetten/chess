package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import dataaccess.SqlDataAccess;
import chess.*;
import dataaccess.*;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess = new SqlDataAccess();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case MAKE_MOVE -> makeMove(command, ctx.session);
                case LEAVE -> leave(command, ctx.session);
                case RESIGN -> resign(command, ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private String getColorString(UserGameCommand command) {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        GameData gameData = dataAccess.getGame(command.getGameID());
        if(username.equals(gameData.whiteUsername())) {
            return "white";
        }
        else if (username.equals(gameData.blackUsername())){
            return "black";
        }
        else {return "observer";}
    }

    private ChessGame.TeamColor getColor(UserGameCommand command) {
        String color = getColorString(command);
        if(color.equals("white")) {
            return ChessGame.TeamColor.WHITE;
        }
        else if(color.equals("black")) {
            return ChessGame.TeamColor.BLACK;
        }
        else {return null;}
    }

    private String getOpponentUsername(UserGameCommand command) {
        GameData gameData = dataAccess.getGame(command.getGameID());
        String oppUsername;
        if (getColor(command) == ChessGame.TeamColor.WHITE) {
            oppUsername = gameData.blackUsername();
        } else {
            oppUsername = gameData.whiteUsername();
            ;
        }
        return oppUsername;
    }

    private String getOpponentColorString(UserGameCommand command) {
        if (getColor(command) == ChessGame.TeamColor.WHITE) {
            return "Black";
        } else {
            return "White";
        }
    }

    private ChessGame.TeamColor getOpponentColor(UserGameCommand command) {
        if (getColor(command) == ChessGame.TeamColor.WHITE) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        connections.add(session, command.getGameID());
        GameData gameData = dataAccess.getGame(command.getGameID());
        if (gameData == null) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Game ID does not exist.");
            connections.reflect(session, errorMessage);
            return;
        }
        AuthData authData = dataAccess.getAuth(command.getAuthToken());
        if (authData == null) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: User is not authorized.");
            connections.reflect(session, errorMessage);
            return;
        }
        String username = authData.username();
        String message = String.format("%s joined the game as %s", username, getColorString(command));
        var notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
        connections.broadcast(session, notif, command.getGameID());
        ChessGame game = gameData.game();
        var gameMessage = new ServerMessage(LOAD_GAME, null, game, null);
        connections.reflect(session, gameMessage);

    }

    private void makeMove(UserGameCommand command, Session session) throws Exception {
        try {
            if(dataAccess.getAuth(command.getAuthToken()) == null) {
                ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Bad auth");
                connections.reflect(session, errorMessage);
                return;
            }
            if (command.getMove() != null) {
                if (getColor(command).equals(null)) {
                    ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: You are an observer and cannot move");
                    connections.reflect(session, errorMessage);
                    return;
                }
                GameData gameData = dataAccess.getGame(command.getGameID());
                ChessGame game = gameData.game();
                if(game.isGameOver()) {
                    ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Game is over. You cannot make a move.");
                    connections.reflect(session, errorMessage);
                    return;
                } else {
                    if(game.getTeamTurn() != getColor(command)) {
                        ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: It is not your turn.");
                        connections.reflect(session, errorMessage);
                        return;
                    }
                    game.makeMove(command.getMove());
                    GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
                    GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(),
                            gameData.blackUsername(), gameData.gameName(), game);
                    dataAccess.updateGame(newGameData, newGameInfo);
                    String username = dataAccess.getAuth(command.getAuthToken()).username();
                    System.out.println(moveToString(command.getMove()));
                    String message = String.format("%s has moved %s", username, moveToString(command.getMove()));
                    ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
                    connections.broadcast(session, notif, command.getGameID());
                    var gameMessage = new ServerMessage(LOAD_GAME, null, game, null);
                    connections.broadcast(null, gameMessage, command.getGameID());
                    String loser = null;
                    if (game.isInCheckmate(getColor(command))) {
                        game.setGameOver(true);
                        loser = String.format("%s is in checkmate! %s wins! %s loses!",
                                getColorString(command), getOpponentUsername(command), username);
                    } else if (game.isInCheckmate(getOpponentColor(command))) {
                        game.setGameOver(true);
                        loser = String.format("%s is in checkmate! %s wins! %s loses!",
                                getOpponentColorString(command), username, getOpponentUsername(command));
                    } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                        game.setGameOver(true);
                        loser = "Stalemate! Game over.";
                    } else if (game.isInCheck(getColor(command))) {
                        loser = String.format("%s (%s) is in check!", getColorString(command), username);
                    } else if (game.isInCheck(getOpponentColor(command))) {
                        loser = String.format("%s (%s) is in check!", getOpponentColorString(command), getOpponentUsername(command));
                    }
                    GameInfo newGameInfo2 = new GameInfo(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
                    GameData newGameData2 = new GameData(gameData.gameId(), gameData.whiteUsername(),
                            gameData.blackUsername(), gameData.gameName(), game);
                    dataAccess.updateGame(newGameData2, newGameInfo2);
                    if (loser != null) {
                        ServerMessage loserNotif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, loser, null, null);
                        connections.broadcast(null, loserNotif, command.getGameID());
                    }
                }

            } else {
                ServerMessage errorMessage = new ServerMessage( ERROR, null, null,"Error: No move to make was given.");
                connections.reflect(session, errorMessage);
                return;
            }
        } catch (Exception ex) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Invalid move.");
            connections.reflect(session, errorMessage);
        }
    }

    private void leave(UserGameCommand command, Session session) throws Exception {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        GameData gameData = dataAccess.getGame(command.getGameID());
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
        if (getColor(command) == ChessGame.TeamColor.WHITE) {
            GameData newGameData = new GameData(gameData.gameId(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            GameInfo newGameInfo = new GameInfo(gameData.gameId(), null, gameData.blackUsername(), gameData.gameName());
            dataAccess.updateGame(newGameData, newGameInfo);
        } else if (getColor(command) == ChessGame.TeamColor.BLACK) {
            GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), null, gameData.gameName());
            dataAccess.updateGame(newGameData, newGameInfo);
        }
        connections.broadcast(session, notification, command.getGameID());
        connections.remove(session, command.getGameID());
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        if(getColorString(command).equals("observer")) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: An observer can't resign.");
            connections.reflect(session, errorMessage);
            return;
        }
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        GameData gameData = dataAccess.getGame(command.getGameID());
        ChessGame game = gameData.game();
        if(game.isGameOver()) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Game is already over.");
            connections.reflect(session, errorMessage);
            return;
        }
        game.resign();
        GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
        dataAccess.updateGame(newGameData, newGameInfo);
        var message = String.format("Game over! %s has resigned from the game. Type leave to leave", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
        connections.broadcast(null, notification, command.getGameID());
    }

    private String moveToString(ChessMove move) {
        String moveString = move.toString();
        String[] tokens = moveString.toLowerCase().split(":");
        int colNum1 = Character.getNumericValue(tokens[0].charAt(2));
        char colLetter1 = (char) ('a' + colNum1 - 1);
        int colNum2 = Character.getNumericValue(tokens[1].charAt(1));
        char colLetter2 = (char) ('a' + colNum2 - 1);
        String letterMoveString = String.format("%c%c to %c%c", colLetter1, tokens[0].charAt(1), colLetter2, tokens[1].charAt(0));
        return letterMoveString;
    }
}