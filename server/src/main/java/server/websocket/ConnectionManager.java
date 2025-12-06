package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.putIfAbsent(gameID, new HashSet<>());
        connections.get(gameID).add(session);
    }

    public void remove(Session session, int gameID) {
        connections.putIfAbsent(gameID, new HashSet<>());
        connections.get(gameID).remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage notification, int gameID) throws IOException {
        Gson gson = new Gson().newBuilder().serializeNulls().create();
        String msg = gson.toJson(notification);
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    System.out.println(msg);
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void reflect(Session session, ServerMessage notification) throws Exception {
        Gson gson = new Gson().newBuilder().serializeNulls().create();
        String msg = gson.toJson(notification);
        session.getRemote().sendString(msg);

    }
}
