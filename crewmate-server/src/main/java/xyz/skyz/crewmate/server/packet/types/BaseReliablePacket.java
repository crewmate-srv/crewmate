package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;
import xyz.skyz.crewmate.server.packet.registry.types.payload.PayloadRegistry;

@PacketInfo(packetType = NetPacketType.RELIABLE)
public class BaseReliablePacket extends ReliablePacket {

    private MessageReader reader;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        return null;
    }

    @Override
    public void deserialize(MessageReader reader) {
        this.reader = reader;
        reader.byteBuf.markReaderIndex();
        reader.readByte(); // Packet type
        setNonce(reader.readInt16()); // Nonce
        reader.byteBuf.resetReaderIndex();
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        if (connection.getPacketsReceived().contains(getNonce())) {
            return;
        } else {
            connection.getPacketsReceived().add(getNonce());
            while (connection.getPacketsReceived().size() > 15) {
                connection.getPacketsReceived().remove(0);
            }
        }
        AcknowledgementPacket acknowledgementPacket = new AcknowledgementPacket();
        acknowledgementPacket.setNonce(getNonce());
        connection.sendPacket(acknowledgementPacket);
        netServer.getPacketManager().getPacketRegistry(PayloadRegistry.class).handle(reader, netServer, connection);
    }
}
