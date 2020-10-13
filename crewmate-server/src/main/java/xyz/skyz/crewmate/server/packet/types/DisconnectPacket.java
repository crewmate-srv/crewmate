package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;

@PacketInfo(packetType = NetPacketType.DISCONNECT)
public class DisconnectPacket extends ReliablePacket {

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        return null;
    }

    @Override
    public void deserialize(MessageReader reader) {
        //
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        netServer.removeConnection(connection.getChannelHandlerContext());
    }
}
