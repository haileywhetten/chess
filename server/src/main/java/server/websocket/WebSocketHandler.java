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

import java.io.IOException;
import java.util.Scanner;

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

    private String getOpponentUsername(UserGameCommand command) {
        GameData gameData = dataAccess.getGame(command.getGameID());
        ChessGame game = gameData.game();
        String oppUsername;
        if (command.getColor() == ChessGame.TeamColor.WHITE) {
            oppUsername = gameData.blackUsername();
        } else {
            oppUsername = gameData.whiteUsername();
            ;
        }
        return oppUsername;
    }

    private String getOpponentColorString(UserGameCommand command) {
        if (command.getColor() == ChessGame.TeamColor.WHITE) {
            return "Black";
        } else {
            return "White";
        }
    }

    private ChessGame.TeamColor getOpponentColor(UserGameCommand command) {
        if (command.getColor() == ChessGame.TeamColor.WHITE) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        connections.add(session);
        AuthData authData = dataAccess.getAuth(command.getAuthToken());
        if (authData == null) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: User is not authorized.");
            connections.reflect(session, errorMessage);
        }
        String username = authData.username();
        String message = String.format("%s joined the game as %s", username, command.getColorString());
        var notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
        connections.broadcast(session, notif);
        GameData gameData = dataAccess.getGame(command.getGameID());
        if (gameData == null) {
            ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Game ID does not exist.");
            connections.reflect(session, errorMessage);
        }
        ChessGame game = gameData.game();
        String loadGame = new Gson().toJson(game);
        var gameMessage = new ServerMessage(LOAD_GAME, null, game, null);
        connections.reflect(session, gameMessage);

    }

    private void makeMove(UserGameCommand command, Session session) throws Exception {
        try {
            if(dataAccess.getAuth(command.getAuthToken()) == null) {
                ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Bad auth");
                connections.reflect(session, errorMessage);
            }
            if (command.getMove() != null) {
                if (command.getColor().equals(null)) {
                    ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: You are an observer and cannot move");
                    connections.reflect(session, errorMessage);
                }
                if (getOpponentUsername(command) == null) {
                    ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: You have no opponent to play against.");
                    connections.reflect(session, errorMessage);
                }
                GameData gameData = dataAccess.getGame(command.getGameID());
                ChessGame game = gameData.game();
                if (!game.isGameOver()) {
                    game.makeMove(command.getMove());
                    GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
                    GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                    dataAccess.updateGame(newGameData, newGameInfo);
                    String username = dataAccess.getAuth(command.getAuthToken()).username();
                    String message = String.format("%s has made the move %s", username, command.getMove().toString());
                    ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
                    connections.broadcast(session, notif);
                    var gameMessage = new ServerMessage(LOAD_GAME, null, game, null);
                    connections.broadcast(null, gameMessage);
                    String loser = null;
                    if (game.isInCheckmate(command.getColor())) {
                        game.setGameOver(true);
                        loser = String.format("%s is in checkmate! %s wins! %s loses!", command.getColorString(), getOpponentUsername(command), username);
                    } else if (game.isInCheckmate(getOpponentColor(command))) {
                        game.setGameOver(true);
                        loser = String.format("%s is in checkmate! %s wins! %s loses!", getOpponentColorString(command), username, getOpponentUsername(command));
                    } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                        loser = "Stalemate! Game over.";
                    } else if (game.isInCheck(command.getColor())) {
                        loser = String.format("%s (%s) is in check!", command.getColorString(), username);
                    } else if (game.isInCheck(getOpponentColor(command))) {
                        loser = String.format("%s (%s) is in check!", getOpponentColorString(command), getOpponentUsername(command));
                    }
                    if (loser != null) {
                        ServerMessage loserNotif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, loser, null, null);
                        connections.broadcast(null, loserNotif);
                    }
                } else {
                    ServerMessage errorMessage = new ServerMessage(ERROR, null, null, "Error: Game is over. You cannot make a move.");
                    connections.reflect(session, errorMessage);
                }

            } else {
                ServerMessage errorMessage = new ServerMessage( ERROR, null, null,"Error: No move to make was given.");
                connections.reflect(session, errorMessage);
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
        if (command.getColor() == ChessGame.TeamColor.WHITE) {
            GameData newGameData = new GameData(gameData.gameId(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            GameInfo newGameInfo = new GameInfo(gameData.gameId(), null, gameData.blackUsername(), gameData.gameName());
            dataAccess.updateGame(newGameData, newGameInfo);
        } else if (command.getColor() == ChessGame.TeamColor.BLACK) {
            GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), null, gameData.gameName());
            dataAccess.updateGame(newGameData, newGameInfo);
        }
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        GameData gameData = dataAccess.getGame(command.getGameID());
        ChessGame game = gameData.game();
        game.resign();
        GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
        dataAccess.updateGame(newGameData, newGameInfo);
        var message = String.format("Game over! %s has resigned from the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
        connections.broadcast(null, notification);
    }
}