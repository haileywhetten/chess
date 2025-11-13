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

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final int port;

    public ServerFacade(/*String url,*/ int port) {
        this.port = port;
        serverUrl = String.format("http://localhost:%d", port);
    }

    public void delete() throws Exception {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    public AuthData register(UserData user) throws Exception {
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData user) throws Exception {
        var request = buildRequest("POST", "/session", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(AuthData auth) throws Exception {
        var request = buildRequest("DELETE", "/session", auth);
        sendRequest(request);
    }

    public GameData createGame(AuthData auth) throws Exception {
        var request = buildRequest("POST", "/game", auth);
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }

    public void joinGame(ColorIdPair colorAndId) throws Exception {
        var request = buildRequest("PUT", "/game", colorAndId);
        sendRequest(request);
    }

    public List<GameInfo> listGames() throws Exception {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        GameInfo[] gamesArray = handleResponse(response, GameInfo[].class);
        assert gamesArray != null;
        return Arrays.asList(gamesArray);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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