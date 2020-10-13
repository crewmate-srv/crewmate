package xyz.skyz.crewmate.redirector;

import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.CrewmateServer;

public class CrewmateRedirectorServer extends CrewmateServer {

    public CrewmateRedirectorServer() {
        NetServer netServer = new NetServer(this, "0.0.0.0", 22023);
        setNetServer(netServer);
        setGameManager(new RedirectorGameManager(this, "http://127.0.0.1:8087"));
        netServer.start();
    }

    public static void main(String[] args) {
        new CrewmateRedirectorServer();
    }
}
