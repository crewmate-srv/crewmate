package xyz.skyz.crewmate.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import xyz.skyz.crewmate.server.base.CrewmateServer;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.channel.UdpServerChannel;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.events.Listener;
import xyz.skyz.crewmate.server.events.types.Event;
import xyz.skyz.crewmate.server.packet.PacketHandler;
import xyz.skyz.crewmate.server.packet.PacketManager;
import xyz.skyz.crewmate.server.packet.registry.types.data.GameDataRegistry;
import xyz.skyz.crewmate.server.packet.registry.types.payload.PayloadRegistry;
import xyz.skyz.crewmate.server.packet.registry.types.rpc.RpcRegistry;
import xyz.skyz.crewmate.server.packet.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NetServer {

    private final String host;
    private final int port;
    private Map<ChannelHandlerContext, Connection> connectionMap = new HashMap<>();
    private Map<UUID, ChannelHandlerContext> uuidChannelHandlerContextMap = new HashMap<>();
    private PacketManager packetManager;
    private List<Listener> listeners = new ArrayList<>();
    private CrewmateServer crewmateServer;

    public NetServer(CrewmateServer crewmateServer, int port) {
        this(crewmateServer, "0.0.0.0", port);
    }

    public NetServer(CrewmateServer crewmateServer, String host, int port) {
        this.crewmateServer = crewmateServer;
        this.host = host;
        this.port = port;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();

        packetManager = new PacketManager(this);

        packetManager.registerPacket(new AcknowledgementPacket());
        packetManager.registerPacket(new BaseNormalPacket());
        packetManager.registerPacket(new BaseReliablePacket());
        packetManager.registerPacket(new DisconnectPacket());
        packetManager.registerPacket(new HelloPacket());
        packetManager.registerPacket(new PingPacket());

        packetManager.registerRegistry(new GameDataRegistry());
        packetManager.registerRegistry(new PayloadRegistry());
        packetManager.registerRegistry(new RpcRegistry());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(new DefaultEventLoopGroup())
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline().addLast(new PacketHandler(packetManager));
                        }
                    });
            bootstrap.channel(UdpServerChannel.class);
            bootstrap.bind(host, port).syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }

    public Connection getConnectionByUuid(UUID uuid) {
        if (!uuidChannelHandlerContextMap.containsKey(uuid)) {
            return null;
        }
        ChannelHandlerContext channelHandlerContext = uuidChannelHandlerContextMap.get(uuid);
        if (!connectionMap.containsKey(channelHandlerContext)) {
            uuidChannelHandlerContextMap.remove(uuid);
            return null;
        }
        return connectionMap.get(channelHandlerContext);
    }

    public Connection getConnection(ChannelHandlerContext channelHandlerContext) {
        Connection connection = connectionMap.get(channelHandlerContext);
        if (connection == null) {
            UUID uuid = UUID.randomUUID();
            while (uuidChannelHandlerContextMap.containsKey(uuid)) {
                uuid = UUID.randomUUID();
            }
            connection = new Connection(this, channelHandlerContext, uuid);
            connectionMap.put(channelHandlerContext, connection);
            uuidChannelHandlerContextMap.put(uuid, channelHandlerContext);
            return connection;
        } else {
            return connection;
        }
    }

    public void removeConnection(ChannelHandlerContext channelHandlerContext) {
        Connection connection = connectionMap.get(channelHandlerContext);
        if (connection != null) {
            Map<UUID, Game> connectionUuidGame = getCrewmateServer().getGameManager().getConnectionUuidGame();
            if (connectionUuidGame.containsKey(connection.getConnectionUuid())) {
                getCrewmateServer().getGameManager().handleRemovePlayer(connection, connectionUuidGame.get(connection.getConnectionUuid()).getGameCode());
            }
            connection.cancelKeepAliveTimer();
            for (UUID uuid : uuidChannelHandlerContextMap.keySet()) {
                if (uuidChannelHandlerContextMap.get(uuid) == channelHandlerContext) {
                    uuidChannelHandlerContextMap.remove(uuid);
                    break;
                }
            }
            connectionMap.remove(channelHandlerContext);
        }
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public void callEvent(Event event) {
        for (Listener listener : listeners) {
            listener.handleEvent(event);
        }
    }

    public void registerListeners(Listener... listenersToAdd) {
        listeners.addAll(Arrays.asList(listenersToAdd));
    }

    public void unregisterListeners(Listener... listenersToRemove) {
        listeners.removeAll(Arrays.asList(listenersToRemove));
    }

    public CrewmateServer getCrewmateServer() {
        return crewmateServer;
    }
}
