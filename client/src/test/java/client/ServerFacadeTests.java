import chess.ChessGame;
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
        var authData = facade.register(user);
        assertDoesNotThrow(()->facade.delete());
        assertThrows(Exception.class, () -> facade.logout(authData));
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
        assertThrows(Exception.class, () -> facade.listGames(authData));

    }

    @Test
    public void logoutBad() {
        assertThrows(Exception.class, () -> facade.logout(new AuthData("123", "123")));
    }

    @Test
    public void createGame() throws Exception {
        var authData = facade.register(user);
        String gameName = "game1";
         //GameData game = new GameData(123, user.username(), null, "game1", new ChessGame());
        assertDoesNotThrow(() -> facade.createGame(gameName, authData));
    }

    @Test
    public void createGameBad() throws Exception {
        var authData = facade.register(user);
        facade.logout(authData);
        assertThrows(Exception.class, () -> facade.createGame("game1", authData));

    }

    @Test
    public void joinGame() throws Exception {
        var authData = facade.register(user);
        var gameID = facade.createGame("game1", authData);
        assertDoesNotThrow(() -> facade.joinGame(gameID, ChessGame.TeamColor.BLACK, authData));
        assertEquals(facade.listGames(authData).getFirst().blackUsername(), user.username());

    }

    @Test
    public void joinGameBad() throws Exception {
        var authData = facade.register(user);
        var authData2 = facade.register(new UserData("ww", "hh", "ee"));
        assertThrows(Exception.class, () -> facade.joinGame(123, ChessGame.TeamColor.WHITE, authData));
        var gameID = facade.createGame("Hailey's game", authData);
        assertDoesNotThrow(() -> facade.joinGame(gameID, ChessGame.TeamColor.WHITE, authData));
        assertDoesNotThrow(() -> facade.joinGame(gameID, ChessGame.TeamColor.BLACK, authData2));
        assertThrows(Exception.class, () -> facade.joinGame(gameID, ChessGame.TeamColor.WHITE, authData2));
        assertThrows(Exception.class, () -> facade.joinGame(gameID, ChessGame.TeamColor.BLACK, authData));
    }

    @Test
    public void listGames() throws Exception {
        var authData = facade.register(user);
        String gameName1 = "game1";
        assertDoesNotThrow(() -> facade.createGame(gameName1, authData));
        String gameName2 = "game2";
        assertDoesNotThrow(() -> facade.createGame(gameName2, authData));
        assertNotNull(facade.listGames(authData));
    }

    @Test
    public void listGamesBad() throws Exception {
        assertThrows(Exception.class, () -> facade.listGames(new AuthData("123", "123")));
        var authData = facade.register(user);
        assertTrue(facade.listGames(authData).isEmpty());

    }

}
