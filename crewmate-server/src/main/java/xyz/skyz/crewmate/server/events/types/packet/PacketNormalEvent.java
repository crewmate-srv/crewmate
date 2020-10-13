package xyz.skyz.crewmate.server.events.types.packet;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.packet.types.NormalPacket;

public class PacketNormalEvent extends PacketEvent {

    private NormalPacket normalPacket;

    public PacketNormalEvent(Connection connection, NormalPacket normalPacket) {
        super(connection);
        this.normalPacket = normalPacket;
    }

    public NormalPacket getPacket() {
        return normalPacket;
    }
}
