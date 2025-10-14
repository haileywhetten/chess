package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.UserService;

import java.util.UUID;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService();
        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        //server.post("session", this::login);


    }
    //TODO: This function is duplicated in UserService, probably other places too
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    //These are the handlers
    private void register(Context ctx) {
        var serializer = new Gson();
        String requestJson = ctx.body();
        var user = serializer.fromJson(requestJson, UserData.class);

        //call to service and register
        AuthData authData = userService.register(user);

        ctx.result(serializer.toJson(authData));

    }

    private void login(Context ctx) {

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
