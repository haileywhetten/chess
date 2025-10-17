package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.UserService;

import java.util.HashMap;
import java.util.Map;


public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService(dataAccess);
        // Register your endpoints and exception handlers here.
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);


    }

    //These are the handlers
    private void clear(Context ctx) {
        try {
            var serializer = new Gson();
            userService.clear();
            ctx.result("{}");
        } catch (Exception ex) {
            var message = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(message);
        }
    }
    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body();
            var user = serializer.fromJson(requestJson, UserData.class);

            //call to service and register
            AuthData authData = userService.register(user);

            ctx.result(serializer.toJson(authData));
        } catch (Exception ex) {
            //This may need to be changed because it may be hard coded
            var message = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            if(ex.getMessage().equals("already taken")) {
                ctx.status(403).result(message);
            }
            else if(ex.getMessage().equals("bad request")) {
                ctx.status(400).result(message);
            }
            else {
                ctx.status(500).result(message);
            }

        }

    }

    private void login(Context ctx) {
        try{
            var serializer = new Gson();
            String requestJson = ctx.body();
            var user = serializer.fromJson(requestJson, UserData.class);
            AuthData authData = userService.login(user);
            ctx.result(serializer.toJson(authData));

        } catch(Exception ex){
            var message = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            if(ex.getMessage().equals("bad request")) {
                ctx.status(400).result(message);
            }
            else if(ex.getMessage().equals("unauthorized")) {
                ctx.status(401).result(message);
            }
            else {
                ctx.status(500).result(message);
            }
        }
    }

    private void logout(Context ctx) {
        try{
            var serializer = new Gson();
            String requestJson = ctx.header("authorization");
            var authToken = serializer.fromJson(requestJson, String.class);
            userService.logout(authToken);
            ctx.result("{}");
        } catch (Exception ex){
            var message = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            if(ex.getMessage().equals("unauthorized")) {
                ctx.status(401).result(message);
            }
            else {
                ctx.status(500).result(message);
            }
        }
    }
    private void createGame(Context ctx) {
        try{
            var serializer = new Gson();
            String authTokenJson = ctx.header("authorization");
            String gameNameJson = ctx.body();
            var authToken = serializer.fromJson(authTokenJson, String.class);
            var gameData = serializer.fromJson(gameNameJson, GameData.class);
            String gameName = gameData.gameName();
            GameData game = userService.createGame(authToken, gameName);
            String json = serializer.toJson(Map.of("gameID", game.gameId()));

            ctx.result(json);

        } catch (Exception ex){
            var message = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            if(ex.getMessage().equals("bad request")) {
                ctx.status(400).result(message);
            }
            else if(ex.getMessage().equals("unauthorized")) {
                ctx.status(401).result(message);
            }
            else {
                ctx.status(500).result(message);
            }
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
