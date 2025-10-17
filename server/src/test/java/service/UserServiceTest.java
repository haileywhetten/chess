package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "toomanysecrets", "j@j.com");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void registerInvalidUsername() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        userService.register(user);
        var duplicateUser = new UserData("joe", "yuh", "joe@j.com");
        assertThrows(Exception.class, () -> userService.register(duplicateUser));
        var duplicateUser2 = new UserData(null, "yuh", "joe@j.com");
        assertThrows(Exception.class, () -> userService.register(duplicateUser2));
    }

    @Test
    void clear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        userService.register(user);
        assertNotNull(db.getUser(user.username()));
        userService.clear();
        assertNull(db.getUser(user.username()));
    }

    @Test
    void login() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        userService.register(user);
        AuthData authData = userService.login(user);
        assertEquals(user.username(), authData.username());
    }

    @Test
    void badLogin() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        assertThrows(Exception.class, () -> userService.login(user));
        userService.register(user);
        var userWrongPassword = new UserData("joe", "bruhh", "j@j.com");
        var userNoPassword = new UserData("joe", null, "j@j.com");
        assertThrows(Exception.class, () -> userService.login(userWrongPassword));
        assertThrows(Exception.class, () -> userService.login(userNoPassword));
    }

    @Test
    void logout() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        userService.register(user);
        AuthData authData = userService.login(user);
        userService.logout(authData.authToken());
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void logoutNotLoggedIn() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        AuthData authData = userService.register(user);
        assertThrows(Exception.class, () -> userService.logout("you are such a capper"));
        userService.logout(authData.authToken());
        assertThrows(Exception.class, () -> userService.logout(authData.authToken()));


    }

    @Test
    void createGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        String gameName = "game1";
        AuthData authData = userService.register(user);
        var game = userService.createGame(authData.authToken(), gameName);
        int gameId = game.gameId();
        assertEquals(db.getGame(game.gameId()).gameId(), gameId);
    }

    @Test
    void createGameFail() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        String gameName = "game1";
        AuthData authData = userService.register(user);
        userService.logout(authData.authToken());
        assertThrows(Exception.class, () -> userService.createGame(authData.authToken(), gameName));
        userService.login(user);
        assertThrows(Exception.class, () -> userService.createGame(authData.authToken(), null));
    }

    @Test
    void listGames() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        AuthData authData = userService.register(user);
        String gameName1 = "game1";
        String gameName2 = "hailey's game";
        userService.createGame(authData.authToken(), gameName1);
        userService.createGame(authData.authToken(), gameName2);
        assertNotNull(userService.listGames(authData.authToken()));
    }

    @Test
    void listGamesFail() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        assertThrows(Exception.class, () -> userService.listGames("random auth token"));
    }
}