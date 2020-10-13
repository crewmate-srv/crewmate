package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;

@PacketInfo(packetType = NetPacketType.ACK)
public class AcknowledgementPacket extends NormalPacket {

    private short nonce;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        writer.writeByte((short) NetPacketType.ACK.getTypeId());
        writer.writeInt16(nonce);
        writer.writeByte((short) 0xFF);
        return writer.getByteBuf();
    }

    @Override
    public ByteBuf serializeWithLength(MessageWriter writer) {
        return serialize(writer);
    }

    @Override
    public void deserialize(MessageReader reader) {
        reader.readByte(); // Packet type id
        this.nonce = reader.readInt16();
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        if (connection.getReliableDataPacketsSent().containsKey(nonce)) {
            connection.getReliableDataPacketsSent().get(nonce).setAcknowledged();
            connection.getReliableDataPacketsSent().remove(nonce);
        }
    }

    public short getNonce() {
        return nonce;
    }

    public void setNonce(short nonce) {
        this.nonce = nonce;
    }
}
