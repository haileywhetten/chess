package serverfacade;

import com.google.gson.Gson;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final int port;

    public ServerFacade(/*String url,*/ int port) {
        this.port = port;
        serverUrl = String.format("http://localhost:%d", port);
    }

    public void delete() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    public AuthData register(UserData user) throws Exception {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData user) throws Exception {
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public <T> T logout(AuthData auth) throws Exception {
        var request = buildRequest("DELETE", "/session", null, auth.authToken());
        var response = sendRequest(request);
        return handleResponse(response, null);
    }

    public int createGame(String gameName, AuthData auth) throws Exception {
        //var body = new Gson().toJson(Map.of("gameName", gameName));
        var request = buildRequest("POST", "/game", Map.of("gameName", gameName), auth.authToken());
        var response = sendRequest(request);
        var gameResponse = handleResponse(response, GameResponse.class);
        return gameResponse.gameID();
    }

    public void joinGame(ColorIdPair colorAndId, AuthData auth) throws Exception {
        var request = buildRequest("PUT", "/game", colorAndId, auth.authToken());
        sendRequest(request);
    }

    public List<GameInfo> listGames(AuthData auth) throws Exception {
        var request = buildRequest("GET", "/game", null, auth.authToken());
        var response = sendRequest(request);
        GameInfo[] gamesArray = handleResponse(response, GameInfo[].class);
        assert gamesArray != null;
        return Arrays.asList(gamesArray);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception();
            }

            throw new Exception();
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}