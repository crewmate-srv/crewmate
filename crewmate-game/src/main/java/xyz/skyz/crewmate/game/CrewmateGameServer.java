package xyz.skyz.crewmate.game;

import xyz.skyz.crewmate.common.util.PropertiesFile;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.CrewmateServer;
import xyz.skyz.crewmate.server.base.ServerEndpoint;

public class CrewmateGameServer extends CrewmateServer {

    private PropertiesFile propertiesFile;

    public CrewmateGameServer() {
        System.out.println("Crewmate Game Server");
        System.out.println("For issues please submit them to: https://github.com/crewmate-srv/crewmate/issues");
        propertiesFile = new PropertiesFile("server.properties");
        NetServer netServer = new NetServer(this, propertiesFile.get("listen-address"), propertiesFile.getInteger("listen-port"));
        setNetServer(netServer);
        setGameManager(new CrewmateGameManager(this, new ServerEndpoint(propertiesFile.get("public-address"), propertiesFile.getInteger("public-port")), propertiesFile.get("controller-api")));
        netServer.start();
    }

    public static void main(String[] args) {
        new CrewmateGameServer();
    }
}
