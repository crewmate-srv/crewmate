package xyz.skyz.crewmate.server.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.RecyclableArrayList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UdpServerChannel extends AbstractServerChannel {

    protected final EventLoopGroup group;
    protected final List<Bootstrap> ioBootstraps = new ArrayList<>();
    protected final List<Channel> ioChannels = new ArrayList<>();
    protected final ConcurrentHashMap<InetSocketAddress, UdpChannel> userChannels = new ConcurrentHashMap<>();

    public UdpServerChannel() throws IOException {
        this(1);
    }

    public UdpServerChannel(int ioThreads) {
        if (ioThreads < 1) {
            throw new IllegalArgumentException("IO threads cound can't be less than 1");
        }
        boolean epollAvailabe = Epoll.isAvailable();
        if (!epollAvailabe) {
            ioThreads = 1;
        }
        group = epollAvailabe ? new EpollEventLoopGroup(ioThreads) : new NioEventLoopGroup(ioThreads);
        Class<? extends DatagramChannel> channel = epollAvailabe ? EpollDatagramChannel.class : NioDatagramChannel.class;
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            final ReadRouteChannelHandler ioReadRoute = new ReadRouteChannelHandler();

            @Override
            protected void initChannel(Channel ioChannel) throws Exception {
                ioChannel.pipeline().addLast(ioReadRoute);
            }
        };
        while (ioThreads-- > 0) {
            Bootstrap ioBootstrap = new Bootstrap().group(group).channel(channel).handler(initializer);
            if (epollAvailabe) {
                ioBootstrap.option(ChannelOption.SO_REUSEADDR, true);
            }
            ioBootstraps.add(ioBootstrap);
        }
    }

    @Sharable
    protected class ReadRouteChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket p) throws Exception {
            UdpChannel channel = userChannels.compute(p.sender(), (lAddr, lChannel) -> {
                return ((lChannel == null) || !lChannel.isOpen()) ? new UdpChannel(UdpServerChannel.this, lAddr) : lChannel;
            });
            channel.buffers.add(p.content().retain());
            if (channel.getIsNew()) {
                ChannelPipeline serverPipeline = UdpServerChannel.this.pipeline();
                serverPipeline.fireChannelRead(channel);
                serverPipeline.fireChannelReadComplete();
            } else {
                if (channel.isRegistered()) {
                    channel.read();
                }
            }
        }
    }

    protected void doWrite(RecyclableArrayList list, InetSocketAddress remote) {
        Channel ioChannel = ioChannels.get(remote.hashCode() & (ioChannels.size() - 1));
        ioChannel.eventLoop().execute(() -> {
            try {
                for (Object buf : list) {
                    ioChannel.write(new DatagramPacket((ByteBuf) buf, remote));
                }
                ioChannel.flush();
            } finally {
                list.recycle();
            }
        });
    }

    protected void doUserChannelRemove(UdpChannel userChannel) {
        userChannels.compute((InetSocketAddress) userChannel.remoteAddress(), (lAddr, lChannel) -> lChannel == userChannel ? null : lChannel);
    }

    protected volatile boolean open = true;

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public boolean isActive() {
        return isOpen();
    }

    @Override
    protected void doClose() throws Exception {
        open = false;
        for (UdpChannel udpChannel : new ArrayList<>(userChannels.values())) {
            udpChannel.close();
        }
        for (Channel ioChannel : ioChannels) {
            ioChannel.close();
        }
        group.shutdownGracefully().sync();
    }

    @Override
    protected SocketAddress localAddress0() {
        return ioChannels.size() > 0 ? ioChannels.get(0).localAddress() : null;
    }

    @Override
    public InetSocketAddress localAddress() {
        return ioChannels.size() > 0 ? (InetSocketAddress) ioChannels.get(0).localAddress() : null;
    }

    @Override
    protected void doBind(SocketAddress local) throws Exception {
        for (Bootstrap bootstrap : ioBootstraps) {
            ioChannels.add(bootstrap.bind(local).sync().channel());
        }
        ioBootstraps.clear();
    }

    protected final DefaultChannelConfig config = new DefaultChannelConfig(this) {

        {
            setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
        }

        @Override
        public boolean isAutoRead() {
            return true;
        }

        @Override
        public ChannelConfig setAutoRead(boolean autoRead) {
            return this;
        }

    };

    @Override
    public DefaultChannelConfig config() {
        return config;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return true;
    }

    @Override
    protected void doBeginRead() throws Exception {
    }

}