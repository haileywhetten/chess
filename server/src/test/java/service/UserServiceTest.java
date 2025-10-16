package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
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
        assertNotNull(userService.login(user));
    }

    @Test
    void badLogin() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe", "bruh", "j@j.com");
        var userService = new UserService(db);
        assertThrows(Exception.class, () -> userService.login(user));
        userService.register(user);
        var userWrongPassword = new UserData("joe", "bruhh", "j@j.com");
        assertThrows(Exception.class, () -> userService.login(userWrongPassword));
    }
}