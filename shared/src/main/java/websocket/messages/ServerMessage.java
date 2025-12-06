package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    public ServerMessageType serverMessageType;
    public String message;
    public ChessGame game;
    public String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType serverMessageType, String message, ChessGame game, String errorMessage) {
        this.serverMessageType = serverMessageType;
        this.message = message;
        this.game = game;
        this.errorMessage = errorMessage;

    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getServerMessage() {
        if(message != null) {
            return message;
        }
        else {
            return errorMessage;
        }
    }

    public ChessGame getGame() {return game;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
