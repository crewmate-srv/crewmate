package xyz.skyz.crewmate.server.packet.registry.types.payload.packets;

import io.netty.buffer.ByteBuf;
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
@RegistryPacketInfo(typeId = 12)
public class WaitForHostPacket extends ReliablePacket {

    private int gameCode;
    private int playerId;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        writer.startMessage((short) PayloadType.WAIT_FOR_HOST.getTypeId());
        writer.writeInt32(gameCode);
        writer.writePackedInt32(playerId);
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

    public int getGameCode() {
        return gameCode;
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
