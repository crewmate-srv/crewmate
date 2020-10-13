package xyz.skyz.crewmate.server.events.types.packet;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

public class PacketReliableEvent extends PacketEvent {

    private ReliablePacket reliablePacket;

    public PacketReliableEvent(Connection connection, ReliablePacket reliablePacket) {
        super(connection);
        this.reliablePacket = reliablePacket;
    }

    public ReliablePacket getPacket() {
        return reliablePacket;
    }
}
