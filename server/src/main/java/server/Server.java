package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
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
        var dataAccess = new MemoryDataAccess();
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService(dataAccess);
        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        //Do need to add a method to the service to clear. 
        server.post("user", this::register);
        //server.post("session", this::login);


    }

    //These are the handlers
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
            ctx.status(403).result(message);
        }

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
