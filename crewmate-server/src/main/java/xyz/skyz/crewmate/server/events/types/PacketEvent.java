package xyz.skyz.crewmate.server.events.types;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.Packet;

public abstract class PacketEvent extends Event {

    private Connection connection;

    public PacketEvent(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public abstract Packet getPacket();
}
