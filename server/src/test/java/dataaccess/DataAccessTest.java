package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mindrot.jbcrypt.BCrypt;


import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
    DataAccess db = new SqlDataAccess();



    @Test
    void clear() {
        db.createUser(new UserData("bobby", "dangbruh", "b@b.com"));
        db.clear();
        assertNull(db.getUser("bobby"));
    }

    @Test
    void createUser() {
        String password = "toomanysecrets";
        var user = new UserData("joe", password, "j@j.com");
        db.createUser(user);
        assertTrue(BCrypt.checkpw(password, db.getUser(user.username()).password()));

    }

    @Test
    void getUser() {
        String username = "hailey";
        String password = "whetten";
        String email = "h@h.com";
        var user = new UserData(username, password, email);
        db.createUser(user);
        assertTrue(BCrypt.checkpw(password, db.getUser(user.username()).password()));
        assertEquals(user.username(), db.getUser(username).username());
        assertEquals(user.email(), db.getUser(username).email());
    }

    @Test
    void listGames() {
        var game1 = new GameData(1234, "hailey", "whetten", "game1", new ChessGame());
        var game2 = new GameData(1, null, null, "game2", new ChessGame());
        var game1Info = new GameInfo(game1.gameId(), game1.whiteUsername(), game1.blackUsername(), game1.gameName());
        var game2Info = new GameInfo(game2.gameId(), game2.whiteUsername(), game2.blackUsername(), game2.gameName());
        db.createGame(game1, game1Info);
        db.createGame(game2, game2Info);
        var list = db.listGames();
        assertThrows(Exception.class, () -> System.out.println(list.get(2)));
        assertNotNull(list);
    }

    @Test
    void createAuth() {
        createUser();
        String username = "joe";
        String fakeAuthToken = "123456";
        var authData = new AuthData(username, fakeAuthToken);
        db.createAuth(authData);
        assertEquals(authData, db.getAuth(fakeAuthToken));
    }

    @Test
    void getAuth() {
        createAuth();
        String username = "joe";
        String fakeAuthToken = "123456";
        var authData = new AuthData(username, fakeAuthToken);
        assertEquals(authData, db.getAuth(fakeAuthToken));
    }

    @Test
    void deleteAuth() {
        createAuth();
        String username = "joe";
        var fakeAuthToken = "123456";
        db.deleteAuth(new AuthData(username, fakeAuthToken));
        assertNull(db.getAuth(fakeAuthToken));
    }
}