package serverfacade;

import model.GameInfo;

import java.util.List;

public record GamesListResponse(List<GameInfo> games) {
}
