package dataaccess;

import model.*;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
    void createGame(GameData gameData);
    GameData getGame(int gameId);
}
