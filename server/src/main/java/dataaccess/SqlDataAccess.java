package dataaccess;

import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;

import java.util.List;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }

    @Override
    public void createGame(GameData gameData, GameInfo gameInfo) {

    }

    @Override
    public GameData getGame(int gameId) {
        return null;
    }

    @Override
    public List<GameInfo> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData game, GameInfo gameInfo) {

    }
}
