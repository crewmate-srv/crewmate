package xyz.skyz.crewmate.server.packet;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;

public abstract class Packet {

    public abstract ByteBuf serialize(MessageWriter writer);

    public abstract ByteBuf serializeWithLength(MessageWriter writer);

    public abstract void deserialize(MessageReader reader);

    public abstract void handle(NetServer netServer, Connection connection);
}
