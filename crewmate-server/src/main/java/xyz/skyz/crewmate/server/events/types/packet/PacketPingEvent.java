package xyz.skyz.crewmate.server.events.types.packet;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.packet.types.PingPacket;

public class PacketPingEvent extends PacketEvent {

    private PingPacket pingPacket;

    public PacketPingEvent(Connection connection, PingPacket pingPacket) {
        super(connection);
        this.pingPacket = pingPacket;
    }

    public PingPacket getPacket() {
        return pingPacket;
    }
}
