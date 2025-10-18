package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void clear() {
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws Exception {
        if(user.password() == null || user.username() == null || user.email() == null) {
            throw new Exception("bad request");
        }
        if(dataAccess.getUser(user.username()) != null){
            throw new Exception("already taken");
        }
        dataAccess.createUser(user);
        AuthData authData = new AuthData(user.username(), generateToken());
        dataAccess.createUser(user);
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        if(user.username() == null || user.password() == null) {
            throw new Exception("bad request");
        }
        if(dataAccess.getUser(user.username()) == null || !dataAccess.getUser(user.username()).password().equals(user.password())) {
            throw new Exception("unauthorized");
        }

        var authData = new AuthData(user.username(), generateToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception {
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authData);
    }

    public GameData createGame(String authToken, String gameName) throws Exception {
        var authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            throw new Exception("unauthorized");
        }
        if(gameName == null) {
            throw new Exception("bad request");
        }
        var game = new ChessGame();
        Random rand = new Random();
        int gameID = rand.nextInt(10000);
        var gameData = new GameData(gameID, null, null, gameName, game);
        var gameInfo = new GameInfo(gameID, null, null, gameName);
        dataAccess.createGame(gameData, gameInfo);

        return gameData;
    }

    public List<GameInfo> listGames(String authToken) throws Exception {
        var authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            throw new Exception("unauthorized");
        }
        return dataAccess.listGames();
    }

    public void joinGame(String authToken, int gameId, ChessGame.TeamColor teamColor) throws Exception {
        var authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            throw new Exception("unauthorized");
        }
        String username = authData.username();
        var gameData = dataAccess.getGame(gameId);
        if(gameData == null || teamColor == null || gameId > 9999) {
            throw new Exception("bad request");
        }
        String whiteUsername;
        String blackUsername;
        if(teamColor == ChessGame.TeamColor.WHITE) {
            if(gameData.whiteUsername() != null) {
                throw new Exception("already taken");
            }
            whiteUsername = username;
            blackUsername = gameData.blackUsername();
        }
        else {
            if(gameData.blackUsername() != null) {
                throw new Exception("already taken");
            }
            whiteUsername = gameData.whiteUsername();
            blackUsername = username;
        }
        var updatedGame = new GameData(gameData.gameId(), whiteUsername, blackUsername, gameData.gameName(), gameData.game());
        var updatedGameInfo = new GameInfo(gameData.gameId(), whiteUsername, blackUsername, gameData.gameName());
        dataAccess.updateGame(updatedGame, updatedGameInfo);

    }


    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
