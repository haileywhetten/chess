package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import java.util.UUID;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));
        server.post("session", ctx -> login(ctx));


    }
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        String requestJson = ctx.body();
        var req = serializer.fromJson(requestJson, Map.class);

        //call to service and register

        var res = Map.of("username", req.get("username"), "authToken", generateToken());
        ctx.result(serializer.toJson(res));

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
