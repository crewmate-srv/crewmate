package xyz.skyz.crewmate.server.packet.registry.types.payload.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.annotation.RegistryPacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;
import xyz.skyz.crewmate.server.packet.enums.PayloadType;
import xyz.skyz.crewmate.server.packet.types.NormalPacket;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

import java.util.Map;
import java.util.UUID;

@PacketInfo(packetType = NetPacketType.RELIABLE)
@RegistryPacketInfo(typeId = 5)
public class GameDataPacket extends ReliablePacket {

    private byte[] fullPacketBytes;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        MessageReader reader = MessageReader.get(Unpooled.copiedBuffer(fullPacketBytes));

        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        reader.readByte(); // Packet type
        reader.readInt16(); // Nonce
        short len = reader.readInt16();
        reader.readByte(); // Payload type

        writer.startMessage((short) PayloadType.GAME_DATA.getTypeId());

        writer.writeBytes(reader.readBytes(len));

        writer.endMessage();
        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {
        this.fullPacketBytes = MessageReader.getByteArraySafe(reader.byteBuf);
        setPacketTypeId(reader.readByte()); // Packet type
        setNonce(reader.readInt16()); // Nonce
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        Map<UUID, Game> connectionUuidGame = netServer.getCrewmateServer().getGameManager().getConnectionUuidGame();
        if (!connectionUuidGame.containsKey(connection.getConnectionUuid())) {
            return;
        }
        connectionUuidGame.get(connection.getConnectionUuid()).sendToAllExcept(this, connection.getConnectionUuid());
    }
}
