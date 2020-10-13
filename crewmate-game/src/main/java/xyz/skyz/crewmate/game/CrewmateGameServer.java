package xyz.skyz.crewmate.game;

import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.CrewmateServer;
import xyz.skyz.crewmate.server.base.ServerEndpoint;

public class CrewmateGameServer extends CrewmateServer {

    public CrewmateGameServer() {
        NetServer netServer = new NetServer(this, "0.0.0.0", 22024);
        setNetServer(netServer);
        setGameManager(new CrewmateGameManager(this, new ServerEndpoint("0.0.0.0", 22024), "http://127.0.0.1:8087"));
        netServer.start();
    }

    public static void main(String[] args) {
        new CrewmateGameServer();
    }
}
