package xyz.skyz.crewmate.server.packet.registry.types.payload.packets;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.data.DisconnectReason;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.annotation.RegistryPacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;
import xyz.skyz.crewmate.server.packet.enums.PayloadType;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

@PacketInfo(packetType = NetPacketType.RELIABLE)
@RegistryPacketInfo(typeId = 1)
public class JoinGameErrorPacket extends ReliablePacket {

    private DisconnectReason disconnectReason;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        writer.startMessage((short) PayloadType.JOIN_GAME.getTypeId());
        writer.startMessage((short) PayloadType.JOIN_GAME.getTypeId());
        writer.writeInt16((short) disconnectReason.getTypeId());
        writer.endMessage();
        writer.endMessage();
        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {
        //
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        //
    }

    public DisconnectReason getDisconnectReason() {
        return disconnectReason;
    }

    public void setDisconnectReason(DisconnectReason disconnectReason) {
        this.disconnectReason = disconnectReason;
    }
}
