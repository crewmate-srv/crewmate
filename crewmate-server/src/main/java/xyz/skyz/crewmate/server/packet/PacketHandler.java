package xyz.skyz.crewmate.server.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.util.HexUtil;
import xyz.skyz.crewmate.server.base.game.GameManager;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final PacketManager packetManager;
    private final ExecutorService executor;

    public PacketHandler(PacketManager packetManager) {
        this.packetManager = packetManager;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        ByteBuf copiedByteBuf = byteBuf.copy();
        MessageReader reader = MessageReader.get(copiedByteBuf);

        int typeId = reader.readByte();
        NetPacketType netPacketType = NetPacketType.getById(typeId);

        if (netPacketType == null) {
            System.out.println("Got packet type " + typeId + " packet which is not registered.\n");
            return;
        }

        if (netPacketType != NetPacketType.ACK && netPacketType != NetPacketType.PING) {
            UUID connectionUuid = packetManager.getNetServer().getConnection(channelHandlerContext).getConnectionUuid();
            GameManager gameManager = packetManager.getNetServer().getCrewmateServer().getGameManager();
            if (gameManager.getConnectionUuidGame().containsKey(connectionUuid) && gameManager.getConnectionUuidGame().get(connectionUuid).getPlayerMap().containsKey(connectionUuid)) {
                System.out.println("Player ID: " + gameManager.getConnectionUuidGame().get(connectionUuid).getPlayerMap().get(connectionUuid).getPlayerId());
            }
            System.out.println("Received bytes (from " + connectionUuid.toString().substring(1, 8) + "):\n" + HexUtil.hexDump(MessageReader.getByteArraySafe(copiedByteBuf)));
        }

        Packet packet = packetManager.createPacketFromId(netPacketType);
        if (packet != null) {
            executor.execute(() -> {
                reader.byteBuf.resetReaderIndex();
                packet.deserialize(reader);
                packet.handle(packetManager.getNetServer(), packetManager.getNetServer().getConnection(channelHandlerContext));
            });
        } else {
            System.out.println("The packet was not created for packet type " + typeId + ".");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        // We don't close the channel because we can keep serving requests.
    }
}

