package xyz.skyz.crewmate.server.events.types.packet;

import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.packet.types.HelloPacket;

public class PacketHelloEvent extends PacketEvent {

    private HelloPacket helloPacket;

    public PacketHelloEvent(Connection connection, HelloPacket helloPacket) {
        super(connection);
        this.helloPacket = helloPacket;
    }

    public HelloPacket getPacket() {
        return helloPacket;
    }
}
