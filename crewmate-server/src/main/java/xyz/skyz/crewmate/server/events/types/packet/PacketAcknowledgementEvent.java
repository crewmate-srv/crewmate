package xyz.skyz.crewmate.server.events.types.packet;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.packet.types.AcknowledgementPacket;

public class PacketAcknowledgementEvent extends PacketEvent {

    private AcknowledgementPacket acknowledgementPacket;

    public PacketAcknowledgementEvent(Connection connection, AcknowledgementPacket acknowledgementPacket) {
        super(connection);
        this.acknowledgementPacket = acknowledgementPacket;
    }

    public AcknowledgementPacket getPacket() {
        return acknowledgementPacket;
    }
}
