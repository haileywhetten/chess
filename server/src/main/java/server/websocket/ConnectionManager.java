package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        Gson gson = new Gson().newBuilder().serializeNulls().create();
        String msg = gson.toJson(notification);
        for (Session c : connections.values()) {
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
        System.out.println(msg);
        session.getRemote().sendString(msg);

    }
}
