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
import xyz.skyz.crewmate.server.packet.types.NormalPacket;

import java.util.Map;
import java.util.UUID;

@PacketInfo(packetType = NetPacketType.NORMAL)
@RegistryPacketInfo(typeId = -1)
public class SpecialRelayOnlyPacket extends NormalPacket {

    private byte[] fullPacketBytes;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        return Unpooled.copiedBuffer(fullPacketBytes);
    }

    @Override
    public void deserialize(MessageReader reader) {
        this.fullPacketBytes = MessageReader.getByteArraySafe(reader.byteBuf);
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
