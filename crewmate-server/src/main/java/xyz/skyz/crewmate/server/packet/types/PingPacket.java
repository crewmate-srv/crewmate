package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;

@PacketInfo(packetType = NetPacketType.PING)
public class PingPacket extends ReliablePacket {

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.PING.getTypeId());
        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {
        packetTypeId = reader.readByte();
        nonce = reader.readInt16();
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        connection.setMissingPings(0);
        connection.recalculatePing();
        AcknowledgementPacket acknowledgementPacket = new AcknowledgementPacket();
        acknowledgementPacket.setNonce(getNonce());
        connection.sendPacket(acknowledgementPacket);
    }
}
