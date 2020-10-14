package xyz.skyz.crewmate.redirector;

import xyz.skyz.crewmate.common.util.PropertiesFile;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.CrewmateServer;

public class CrewmateRedirectorServer extends CrewmateServer {

    private PropertiesFile propertiesFile;

    public CrewmateRedirectorServer() {
        System.out.println("Crewmate Redirector Server");
        System.out.println("For issues please submit them to: https://github.com/crewmate-srv/crewmate/issues");
        propertiesFile = new PropertiesFile("server.properties");
        NetServer netServer = new NetServer(this, propertiesFile.get("listen-address"), propertiesFile.getInteger("listen-port"));
        setNetServer(netServer);
        setGameManager(new RedirectorGameManager(this, propertiesFile.get("controller-api")));
        netServer.start();
    }

    public static void main(String[] args) {
        new CrewmateRedirectorServer();
    }
}
