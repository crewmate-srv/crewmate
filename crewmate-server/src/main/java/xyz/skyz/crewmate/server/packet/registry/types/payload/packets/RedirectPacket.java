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

import java.net.InetAddress;

@PacketInfo(packetType = NetPacketType.RELIABLE)
@RegistryPacketInfo(typeId = 13)
public class RedirectPacket extends ReliablePacket {

    private InetAddress endpointHost;
    private int endpointPort;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        writer.startMessage((short) PayloadType.REDIRECT.getTypeId());
        writer.writeBytes(endpointHost.getAddress());
        writer.writeInt32(endpointPort);
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

    public InetAddress getEndpointHost() {
        return endpointHost;
    }

    public void setEndpointHost(InetAddress endpointHost) {
        this.endpointHost = endpointHost;
    }

    public int getEndpointPort() {
        return endpointPort;
    }

    public void setEndpointPort(int endpointPort) {
        this.endpointPort = endpointPort;
    }
}
