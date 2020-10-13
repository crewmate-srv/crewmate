package xyz.skyz.crewmate.server.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.common.util.HexUtil;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.packet.Packet;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

import java.util.*;

public class Connection {

    private NetServer netServer;
    private ChannelHandlerContext channelHandlerContext;
    private UUID connectionUuid;

    private String playerName;

    private ConnectionState connectionState = ConnectionState.NOT_CONNECTED;

    private int resendLimit = 0;
    private int resendPingMultiplier = 2;

    private int lastNonceAllocated = 0;

    private Map<Short, ReliablePacket> reliableDataPacketsSent = new HashMap<>();

    private final Object pingLock = new Object();

    private int disconnectTimeout = 5000;
    private int missingPingsUntilDisconnect = 6;

    private Timer pingTimer = new Timer();
    private int missingPings = 0;
    private long lastPingTime = -1;
    private long currentPing = -1;

    private ArrayList<Short> packetsReceived = new ArrayList<>();

    public Connection(NetServer netServer, ChannelHandlerContext channelHandlerContext, UUID connectionUuid) {
        this.netServer = netServer;
        this.channelHandlerContext = channelHandlerContext;
        this.connectionUuid = connectionUuid;
        initializeKeepAliveTimer();
    }

    public void sendPacket(Packet packet) {
        MessageWriter writer = new MessageWriter();

        ByteBuf byteBuf = packet.serializeWithLength(writer);
        //byte[] bytes = MessageReader.getByteArraySafe(byteBuf);
        //System.out.println("Sent bytes (non-reliable):\n" + HexUtil.hexDump(bytes));

        try {
            channelHandlerContext.writeAndFlush(byteBuf).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendReliablePacket(ReliablePacket reliablePacket) {
        short nonce;
        synchronized (pingLock) {
            if (lastNonceAllocated < 65535) {
                lastNonceAllocated++;
            } else {
                lastNonceAllocated = 0;
            }
            nonce = (short) lastNonceAllocated;
        }
        reliablePacket.setNonce(nonce);
        reliablePacket.setSentAt(System.currentTimeMillis());
        // Serialize to get length
        MessageWriter writer = new MessageWriter();
        ByteBuf byteBuf = reliablePacket.serializeWithLength(writer);
        // Set length
        byte[] bytes = MessageReader.getByteArraySafe(byteBuf);
        System.out.println("Sent bytes (reliable):\n" + HexUtil.hexDump(bytes));

        // Send data
        reliableDataPacketsSent.put(nonce, reliablePacket);
        try {
            channelHandlerContext.writeAndFlush(byteBuf).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Timer timer = new Timer();
        runResendTask(timer, reliablePacket);
    }

    private void runResendTask(Timer timer, ReliablePacket reliablePacket) {
        TimerTask reliableResendTask = new TimerTask() {
            @Override
            public void run() {
                if (reliablePacket.isAcknowledged()) {
                    return;
                }
                if (!reliableDataPacketsSent.containsKey(reliablePacket.getNonce())) {
                    return;
                }
                resendReliablePacket(reliablePacket.getNonce());
                runResendTask(timer, reliablePacket);
            }
        };
        long resendDelay = updateResendDelay(reliablePacket);
        timer.schedule(reliableResendTask, resendDelay);
    }

    public void resendReliablePacket(short packetId) {
        if (reliableDataPacketsSent.containsKey(packetId)) {
            ReliablePacket reliablePacket = reliableDataPacketsSent.get(packetId);
            if (reliablePacket.isAcknowledged()) {
                return;
            }
            long packetLifetime = System.currentTimeMillis() - reliablePacket.getSentAt();
            if (packetLifetime > disconnectTimeout) {
                System.out.println("The packet " + packetId + " was not ack'd after " + packetLifetime + "ms (" + reliablePacket.getRetransmissions() + " resends)");
                reliableDataPacketsSent.remove(packetId);
                return;
            }
            if (resendLimit != 0 && reliablePacket.getRetransmissions() >= reliablePacket.getRetransmissions()) {
                System.out.println("The packet " + packetId + " was not ack'd after " + reliablePacket.getRetransmissions() + " resends (" + packetLifetime + "ms)");
                reliableDataPacketsSent.remove(packetId);
                return;
            }
            reliablePacket.setRetransmissions(reliablePacket.getRetransmissions() + 1);
            MessageWriter writer = new MessageWriter();
            try {
                channelHandlerContext.writeAndFlush(reliablePacket.serialize(writer)).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("The packet to be resent does not exist in the sent packets.");
        }
    }

    public long updateResendDelay(ReliablePacket reliablePacket) {
        reliablePacket.setNextResendDelay(Math.min(reliablePacket.getNextResendDelay() * resendPingMultiplier, 1000));
        return reliablePacket.getNextResendDelay();
    }

    public void initializeKeepAliveTimer() {
        TimerTask pingTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (connectionState != ConnectionState.CONNECTED) {
                    return;
                }
                if (missingPings > missingPingsUntilDisconnect) {
                    connectionState = ConnectionState.NOT_CONNECTED;
                    System.out.println("Expected " + missingPings + " pings that the remote did not send.");
                    netServer.removeConnection(channelHandlerContext);
                }
                missingPings++;
            }
        };
        pingTimer.schedule(pingTimerTask, 0, 1000);
    }

    public void cancelKeepAliveTimer() {
        pingTimer.cancel();
    }

    public void recalculatePing() {
        long currentTime = System.currentTimeMillis();
        long pingDifference = (currentTime - this.lastPingTime);
        if (pingDifference < 0) {
            pingDifference = 0;
        }
        if (this.lastPingTime != -1) {
            this.currentPing = (int) pingDifference;
        } else {
            this.currentPing = 0;
        }
        this.lastPingTime = currentTime;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public UUID getConnectionUuid() {
        return connectionUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public Map<Short, ReliablePacket> getReliableDataPacketsSent() {
        return reliableDataPacketsSent;
    }

    public void setMissingPings(int missingPings) {
        this.missingPings = missingPings;
    }

    public long getCurrentPing() {
        return currentPing;
    }

    public ArrayList<Short> getPacketsReceived() {
        return packetsReceived;
    }
}
