package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.Packet;

public abstract class NormalPacket extends Packet {

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        return null;
    }

    @Override
    public ByteBuf serializeWithLength(MessageWriter writer) {
        return serialize(writer);
    }

    @Override
    public abstract void deserialize(MessageReader reader);

    @Override
    public abstract void handle(NetServer netServer, Connection connection);
}
