package xyz.skyz.crewmate.server.events.types.packet;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.packet.types.DisconnectPacket;

public class PacketDisconnectEvent extends PacketEvent {

    private DisconnectPacket disconnectPacket;

    public PacketDisconnectEvent(Connection connection, DisconnectPacket disconnectPacket) {
        super(connection);
        this.disconnectPacket = disconnectPacket;
    }

    public DisconnectPacket getPacket() {
        return disconnectPacket;
    }
}
