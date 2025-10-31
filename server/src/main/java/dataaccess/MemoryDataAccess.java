package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private final HashMap<Integer, GameInfo> gameInfos = new HashMap<>();
    //public Boolean createGame1(GameData gameData, GameInfo gameInfo) {return false;}
    @Override
    public void clear() {
        users.clear();
        games.clear();
        gameInfos.clear();
        auths.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);

    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        auths.remove(authData.authToken());
    }

    @Override
    public void createGame(GameData gameData, GameInfo gameInfo) {
        games.put(gameData.gameId(), gameData);
        gameInfos.put(gameInfo.gameID(), gameInfo);
    }

    @Override
    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    @Override
    public List<GameInfo> listGames() {
        var list = new ArrayList<>(gameInfos.values());
        for (GameInfo gameInfo : list) {
            System.out.println(gameInfo);
        }
        return list;
    }

    @Override
    public void updateGame(GameData game, GameInfo gameInfo) {
        games.put(game.gameId(), game);
        gameInfos.put(gameInfo.gameID(), gameInfo);

    }
}

