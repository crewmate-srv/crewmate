package xyz.skyz.crewmate.server.base;

import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.connection.Connection;

public class Player {

    private Game game;
    private Connection connection;
    private int playerId;
    private String name;
    private PlayerLimboState playerLimboState;

    public Player(Game game, Connection connection, int playerId, String name) {
        this.game = game;
        this.connection = connection;
        this.playerId = playerId;
        this.name = name;
        this.playerLimboState = PlayerLimboState.PRE_SPAWN;
    }

    public Game getGame() {
        return game;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public PlayerLimboState getPlayerLimboState() {
        return playerLimboState;
    }

    public void setPlayerLimboState(PlayerLimboState playerLimboState) {
        this.playerLimboState = playerLimboState;
    }
}
