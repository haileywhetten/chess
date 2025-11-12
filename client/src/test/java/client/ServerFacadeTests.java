import dataaccess.SqlDataAccess;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

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
    public void register() {
    }

    @Test
    public void registerBad() {
    }

    @Test
    public void login() {
    }

    @Test
    public void loginBad() {
    }

    @Test
    public void logout() {
    }

    @Test
    public void logoutBad() {
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
