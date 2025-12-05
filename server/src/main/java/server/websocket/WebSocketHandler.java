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
        if(command.getColor() == ChessGame.TeamColor.WHITE) {
            oppUsername = gameData.blackUsername();
        }
        else {
            oppUsername = gameData.whiteUsername();;
        }
        return oppUsername;
    }

    private String getOpponentColorString(UserGameCommand command) {
        if(command.getColor() == ChessGame.TeamColor.WHITE) {
            return "Black";
        }
        else {
            return "White";
        }
    }

    private ChessGame.TeamColor getOpponentColor(UserGameCommand command) {
        if(command.getColor() == ChessGame.TeamColor.WHITE) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        connections.add(session);
        AuthData authData = dataAccess.getAuth(command.getAuthToken());
        if(authData == null) {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: User is not authorized.");
            connections.reflect(session, errorMessage);
            throw new Exception();
        }
        String username = authData.username();
        String message = String.format("%s joined the game as %s", username, command.getColorString());
        var notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notif);
        GameData gameData = dataAccess.getGame(command.getGameID());
        if(gameData == null) {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game ID does not exist.");
            connections.reflect(session, errorMessage);
            throw new Exception();
        }
        ChessGame game = gameData.game();
        String loadGame = new Gson().toJson(game);
        var gameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, loadGame);
        connections.reflect(session, gameMessage);

    }

    private void makeMove(UserGameCommand command, Session session) throws Exception {
        try{
            if(command.getMove() != null) {
                if(getOpponentUsername(command) == null) {
                    ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: You have no opponent to play against.");
                    connections.reflect(session, errorMessage);
                    throw new Exception();
                }
                GameData gameData = dataAccess.getGame(command.getGameID());
                ChessGame game = gameData.game();
                if(!game.isGameOver()) {
                    game.makeMove(command.getMove());
                    GameInfo newGameInfo = new GameInfo(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
                    GameData newGameData = new GameData(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                    dataAccess.updateGame(newGameData, newGameInfo);
                    String username = dataAccess.getAuth(command.getAuthToken()).username();
                    String message = String.format("%s has made the move %s", username, command.getMove().toString());
                    ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    connections.broadcast(null, notif);
                    String loser = null;
                    if(game.isInCheckmate(command.getColor())) {
                        game.setGameOver(true);
                        loser = String.format("%s is in checkmate! %s wins! %s loses!", command.getColorString(), getOpponentUsername(command), username);
                    }
                    else if(game.isInCheckmate(getOpponentColor(command))) {
                        game.setGameOver(true);
                        loser = String.format("%s is in checkmate! %s wins! %s loses!", getOpponentColorString(command), username, getOpponentUsername(command));
                    }
                    else if(game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                        loser = "Stalemate! Game over.";
                    }
                    else if(game.isInCheck(command.getColor())) {
                        loser = String.format("%s (%s) is in check!", command.getColorString(), username);
                    }
                    else if(game.isInCheck(getOpponentColor(command))) {
                        loser = String.format("%s (%s) is in check!", getOpponentColorString(command), getOpponentUsername(command));
                    }
                    if(loser != null) {
                        ServerMessage loserNotif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, loser);
                        connections.broadcast(null, loserNotif);
                    }
                }
                else {
                    ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game is over. You cannot make a move.");
                    connections.reflect(session, errorMessage);
                    throw new Exception();
                }

            }
            else {
                ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: No move to make was given.");
                connections.reflect(session, errorMessage);
                throw new Exception();
            }
        } catch (Exception ex) {
            ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid move.");
            connections.reflect(session, errorMessage);
            throw new Exception();
        }
    }

    private void leave(UserGameCommand command, Session session) throws Exception {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        String username = dataAccess.getAuth(command.getAuthToken()).username();
        ChessGame game = dataAccess.getGame(command.getGameID()).game();
        game.resign();
        var message = String.format("Game over! %s has resigned from the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, notification);
    }

    /*private void enter(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(session, notification);
    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }*/
}
