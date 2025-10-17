package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;
import java.util.Random;
import java.util.UUID;
//TODO: Delete other UserData from the datamodel package because I duplicated that

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
        //return new AuthData(user.username(), generateToken());
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
        int gameId = rand.nextInt(10000);
        var gameData = new GameData(gameId, null, null, gameName, game);
        dataAccess.createGame(gameData);

        return gameData;
    }

    public List<GameData> listGames(String authToken) throws Exception {
        var authData = dataAccess.getAuth(authToken);
        if(authData == null) {
            throw new Exception("unauthorized");
        }
        return dataAccess.listGames();
    }


    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
