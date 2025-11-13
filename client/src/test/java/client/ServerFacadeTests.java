import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData user = new UserData("h", "w", "@");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        try {
            facade.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };


    @Test
    public void delete() throws Exception {
        assertDoesNotThrow(()->facade.delete());
        assertThrows(Exception.class, () -> facade.listGames());
    }

    @Test
    public void register() throws Exception {
        var authData = facade.register(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerBad() {
        assertThrows(Exception.class, () -> facade.register(new UserData("you", null, "@@")));
    }

    @Test
    public void login() throws Exception {
        facade.register(user);
        assertTrue(facade.login(user).authToken().length() > 20);
    }

    @Test
    public void loginBad() throws Exception {
        assertThrows(Exception.class, () -> facade.login(new UserData("bbb", "bbb", "@@")));
    }

    @Test
    public void logout() throws Exception {
        var authData = facade.register(user);
        assertDoesNotThrow(() -> facade.logout(authData));
        assertThrows(Exception.class, () -> facade.listGames());

    }

    @Test
    public void logoutBad() {
        assertThrows(Exception.class, () -> facade.logout(new AuthData("123", "123")));
    }

    @Test
    public void createGame() {
    }

    @Test
    public void createGameBad() {
    }

    @Test
    public void joinGame() {
    }

    @Test
    public void joinGameBad() {
    }

    @Test
    public void listGames() {
    }

    @Test
    public void listGamesBad() {
    }

    @Test
    public void updateGame() {
    }

    @Test
    public void updateGamesBad() {
    }
}
