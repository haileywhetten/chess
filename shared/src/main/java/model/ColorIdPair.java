package model;

import chess.ChessGame;

public record ColorIdPair(ChessGame.TeamColor playerColor, int gameID) {
    public ChessGame.TeamColor getColor() {
        return playerColor;
    }
    public int getId() {
        return gameID;
    }
}
