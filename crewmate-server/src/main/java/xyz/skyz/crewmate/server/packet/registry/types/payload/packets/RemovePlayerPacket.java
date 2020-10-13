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
@RegistryPacketInfo(typeId = 4)
public class RemovePlayerPacket extends ReliablePacket {

    private int gameCode;
    private int playerId;
    private int hostId;
    private short reason;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        writer.startMessage((short) PayloadType.REMOVE_PLAYER.getTypeId());
        writer.writeInt32(gameCode);
        writer.writeInt32(playerId);
        writer.writeInt32(hostId);
        writer.writeByte(reason);
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

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public short getReason() {
        return reason;
    }

    public void setReason(short reason) {
        this.reason = reason;
    }
}
