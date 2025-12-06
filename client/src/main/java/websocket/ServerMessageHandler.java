package websocket;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage notification);
    void loadGame(ServerMessage notification);
    void error(ServerMessage notification);
}