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
    void createUserNegative() {
        clear();
        createUser();
        var user = new UserData("joe", "hello", "9@9.com");
        assertThrows(Exception.class, () -> db.createUser(user));
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
    void getUserNegative() {
        assertNull(db.getUser("nonsense username"));
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
    void createAuthNegative() {
        var authDataFake = new AuthData("fake username doesn't exist", "12345");
        assertThrows(Exception.class, () -> db.createAuth(authDataFake));
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
    void getAuthNegative() {
        assertNull(db.getAuth("not a real authToken"));
    }

    @Test
    void deleteAuth() {
        createAuth();
        String username = "joe";
        var fakeAuthToken = "123456";
        db.deleteAuth(new AuthData(username, fakeAuthToken));
        assertNull(db.getAuth(fakeAuthToken));
    }

    @Test
    void deleteAuthNegative() {
        db.clear();
        var newUser = new UserData("hi", "1", "2");
        db.createUser(newUser);
        var newAuth = new AuthData("hi", "123");
        db.createAuth(newAuth);
        var fakeAuth = new AuthData("5", "555");
        db.deleteAuth(fakeAuth);
        assertNotNull(db.getAuth(newAuth.authToken()));

    }

    @Test
    void createGame() {
        clear();
        createUser();
        getUser();
        int gameId = 12;
        String whiteUsername = "hailey";
        String blackUsername = "joe";
        String gameName = "hailey destroys joe";
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        GameInfo gameInfo = new GameInfo(gameId, whiteUsername, blackUsername, gameName);
        db.createGame(gameData, gameInfo);
        assertEquals(gameData, db.getGame(gameId));
    }

    @Test
    void createGameNegative() {
        clear();
        createUser();
        int gameId = 15;
        String game1Name = "1";
        String game2Name = "2";
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(gameId, null, null, game1Name, chessGame1);
        GameData game2 = new GameData(gameId, null, null, game2Name, chessGame2);
        GameInfo game1Info = new GameInfo(gameId, null, null, game1Name);
        GameInfo game2Info = new GameInfo(gameId, null, null, game2Name);
        db.createGame(game1, game1Info);
        assertThrows(Exception.class, () -> db.createGame(game2, game2Info));
    }

    @Test
    void getGame() {
        createGame();
        int gameId = 12;
        String whiteUsername = "hailey";
        String blackUsername = "joe";
        String gameName = "hailey destroys joe";
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        assertEquals(gameData, db.getGame(gameId));

    }

    @Test
    void getGameNegative() {
        clear();
        assertNull(db.getGame(111));
    }

    @Test
    void listGames() {
        clear();
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
    void listGamesNegative() {
        clear();
        assertTrue(db.listGames().isEmpty());
    }

    @Test
    void updateGame() {
        var newUsername = "joe";
        var game3 = new GameData(1, null, null, "game3", new ChessGame());
        var game3Info = new GameInfo(game3.gameId(), game3.whiteUsername(), game3.blackUsername(), game3.gameName());
        db.createGame(game3, game3Info);
        var game3New = new GameData(1, null, newUsername, "game3", new ChessGame());
        var game3NewInfo = new GameInfo(game3New.gameId(), game3New.whiteUsername(), game3New.blackUsername(), game3New.gameName());
        db.updateGame(game3New, game3NewInfo);
        assertNotNull(db.getGame(game3New.gameId()));
        assertEquals(newUsername, db.getGame(game3New.gameId()).blackUsername());
    }

    @Test
    void updateGameFail() {
        //createGame();
        clear();
        GameData gameData = new GameData(12, "burt", "joe", "hailey destorys joe", new ChessGame());
        GameInfo gameInfo = new GameInfo(12, "burt", "joe", "hailey destorys joe");
        assertThrows(Exception.class, () -> db.updateGame(gameData, gameInfo));
    }


}