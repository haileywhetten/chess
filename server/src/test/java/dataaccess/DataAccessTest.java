package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() {
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("joe", "j@j.com", "toomanysecrets"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void createUser() {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
    }

    @Test
    void getUser() {
    }

    @Test
    void listGames() {
        DataAccess db = new MemoryDataAccess();
        var game1 = new GameData(1234, "hailey", "whetten", "game1", new ChessGame());
        var game2 = new GameData(1, null, null, "game2", new ChessGame());
        var game1Info = new GameInfo(game1.gameId(), game1.whiteUsername(), game1.blackUsername(), game1.gameName());
        var game2Info = new GameInfo(game2.gameId(), game2.whiteUsername(), game2.blackUsername(), game2.gameName());
        db.createGame(game1, game1Info);
        db.createGame(game2, game2Info);
        var list = db.listGames();
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        assertThrows(Exception.class, () -> System.out.println(list.get(2)));
        assertNotNull(list);
    }
}