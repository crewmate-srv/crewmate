package xyz.skyz.crewmate.server.base;

public class ServerEndpoint {

    private String host;
    private int port;

    public ServerEndpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
