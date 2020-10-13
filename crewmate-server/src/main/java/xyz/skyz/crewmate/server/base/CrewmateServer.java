package xyz.skyz.crewmate.server.base;

import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.game.GameManager;

public abstract class CrewmateServer {

    private NetServer netServer;
    private GameManager gameManager;

    public NetServer getNetServer() {
        return netServer;
    }

    public void setNetServer(NetServer netServer) {
        this.netServer = netServer;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
