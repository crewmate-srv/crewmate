package xyz.skyz.crewmate.server.base.game;

import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.server.base.CrewmateServer;
import xyz.skyz.crewmate.server.base.ServerEndpoint;
import xyz.skyz.crewmate.server.connection.Connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class GameManager {

    private CrewmateServer crewmateServer;
    private Map<Integer, Game> gameMap = new HashMap<>();
    private Map<UUID, Game> connectionUuidGame = new HashMap<>();

    public GameManager(CrewmateServer crewmateServer) {
        this.crewmateServer = crewmateServer;
    }

    public CrewmateServer getCrewmateServer() {
        return crewmateServer;
    }

    public Map<Integer, Game> getGameMap() {
        return gameMap;
    }

    public abstract void handleHostGame(Connection connection, GameOptionsData gameOptionsData);

    public abstract void handleJoinGame(Connection connection, int gameCode);

    public abstract void handleRemovePlayer(Connection connection, int gameCode);

    public abstract void removeGame(int gameCode);

    public boolean isGameLocal(int code) {
        return gameMap.containsKey(code);
    }

    public abstract int generateGameCode();

    public abstract ServerEndpoint findServerEndpoint();

    public abstract void searchForGames(Connection connection, GameOptionsData gameOptionsData);

    public Map<UUID, Game> getConnectionUuidGame() {
        return connectionUuidGame;
    }
}
