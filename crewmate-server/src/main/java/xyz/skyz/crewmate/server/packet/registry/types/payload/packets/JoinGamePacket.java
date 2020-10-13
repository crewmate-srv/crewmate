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
@RegistryPacketInfo(typeId = 1)
public class JoinGamePacket extends ReliablePacket {

    private int gameCode;
    private short unknown;
    private int playerId;
    private int hostId;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        writer.startMessage((short) PayloadType.JOIN_GAME.getTypeId());
        writer.writeInt32(gameCode);
        writer.writeInt32(playerId);
        writer.writeInt32(hostId);
        writer.endMessage();

        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {
        setPacketTypeId(reader.readByte()); // Packet type
        setNonce(reader.readInt16()); // Nonce

        reader.readByte(); // Length
        reader.readInt16(); // Payload id
        reader.readBytes(4); // ?
        this.gameCode = reader.readInt32();
        this.unknown = reader.readByte();
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        netServer.getCrewmateServer().getGameManager().handleJoinGame(connection, gameCode);
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public int getGameCode() {
        return gameCode;
    }

    public short getUnknown() {
        return unknown;
    }

    public void setUnknown(short unknown) {
        this.unknown = unknown;
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
}
