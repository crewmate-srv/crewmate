package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;

@PacketInfo(packetType = NetPacketType.HELLO)
public class HelloPacket extends ReliablePacket {

    private short hazelVersion;
    private int clientVersion;
    private String name;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        return null;
    }

    @Override
    public void deserialize(MessageReader reader) {
        packetTypeId = reader.readByte();
        nonce = reader.readInt16();
        this.hazelVersion = reader.readByte(); // Hazel version
        this.clientVersion = reader.readInt32();
        this.name = reader.readString();
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        AcknowledgementPacket acknowledgementPacket = new AcknowledgementPacket();
        acknowledgementPacket.setNonce(getNonce());
        connection.sendPacket(acknowledgementPacket);
        connection.setPlayerName(name);
    }

    public short getHazelVersion() {
        return hazelVersion;
    }

    public void setHazelVersion(short hazelVersion) {
        this.hazelVersion = hazelVersion;
    }

    public int getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(int clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
