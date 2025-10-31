package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() {
        try {
            DatabaseManager.createDatabase();
            configureDatabase();
        }
        catch (Exception ex) {
            System.out.println("Bruh it aint working lol");
        }

    }

    @Override
    public void clear() {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement1 = "DELETE from auth";
            var statement2 = "DELETE from user";
            var statement3 = "TRUNCATE TABLE game";
            try(PreparedStatement ps = conn.prepareStatement(statement1)) {
                ps.executeUpdate();
            }
            try(PreparedStatement ps = conn.prepareStatement(statement2)) {
                ps.executeUpdate();
            }
            try(PreparedStatement ps = conn.prepareStatement(statement3)) {
                ps.executeUpdate();
            }
        } catch(Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void createUser(UserData user) {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch(Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }



    }

    @Override
    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void createAuth(AuthData authData) {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.username());
                ps.setString(2, authData.authToken());
                ps.executeUpdate();
            }
        } catch(Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("username"), rs.getString("authToken"));
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE from auth where authToken=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void createGame(GameData gameData, GameInfo gameInfo) {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameData.gameId());
                ps.setString(2, gameData.whiteUsername());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.gameName());
                ps.setString(5, new Gson().toJson(gameData.game()));
                ps.executeUpdate();
            }
        } catch(Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    @Override
    public GameData getGame(int gameId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("json"), ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public List<GameInfo> listGames() {
        var result = new ArrayList<GameInfo>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameInfo gameInfo = new GameInfo(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"));
                        result.add(gameInfo);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
        return result;
    }

    @Override
    public void updateGame(GameData game, GameInfo gameInfo) {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, json = ?";
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, new Gson().toJson(game.game()));
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              FOREIGN KEY (`username`) REFERENCES user(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            //NOTE: maybe whiteUsername and blackUsername need to be foreign keys to user username or something?
    };

    private void configureDatabase() throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new Exception();
        }
    }
}
