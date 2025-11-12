import dataaccess.SqlDataAccess;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static SqlDataAccess db;

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
    void clearDatabase() {db.clear();};


    @Test
    public void delete() {
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
