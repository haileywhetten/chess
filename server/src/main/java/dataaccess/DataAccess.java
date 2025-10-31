package dataaccess;

import model.*;

import java.util.List;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
    void createGame(GameData gameData, GameInfo gameInfo);
    GameData getGame(int gameId);
    List<GameInfo> listGames();
    void updateGame(GameData game, GameInfo gameInfo);
    //public Boolean createGame1(GameData gameData, GameInfo gameInfo);
}
