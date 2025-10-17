package model;

import chess.ChessGame;

public record ColorIdPair(ChessGame.TeamColor playerColor, String gameId) {
    public ChessGame.TeamColor getColor() {
        return playerColor;
    }
    public String getId() {
        return gameId;
    }
}
