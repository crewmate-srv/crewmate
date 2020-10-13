package xyz.skyz.crewmate.server.packet.registry.types.payload.packets;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.data.GameOptionsData;
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
@RegistryPacketInfo(typeId = 0)
public class HostGamePacket extends ReliablePacket {

    private GameOptionsData gameOptionsData;
    private int gameCode;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());
        //writer.writeInt16((short) PayloadType.HOST_GAME.getTypeId());

        writer.startMessage((short) PayloadType.HOST_GAME.getTypeId());
        writer.writeInt32(gameCode);
        writer.endMessage();
        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {
        setPacketTypeId(reader.readByte()); // Packet type
        setNonce(reader.readInt16()); // Nonce

        reader.readByte(); // Length
        reader.readInt16(); // Payload id
        reader.readByte();
        this.gameOptionsData = new GameOptionsData();
        this.gameOptionsData.deserialize(reader);
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        netServer.getCrewmateServer().getGameManager().handleHostGame(connection, gameOptionsData);
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public int getGameCode() {
        return gameCode;
    }
}
