package xyz.skyz.crewmate.server.packet.types;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.packet.Packet;

public abstract class ReliablePacket extends Packet {

    short packetTypeId;
    short nonce;
    private int retransmissions = 0;
    private boolean acknowledged = false;
    private long sentAt = 0;
    private long nextResendDelay = 250;

    public short getPacketTypeId() {
        return packetTypeId;
    }

    public void setPacketTypeId(short packetTypeId) {
        this.packetTypeId = packetTypeId;
    }

    public short getNonce() {
        return nonce;
    }

    public void setNonce(short nonce) {
        this.nonce = nonce;
    }

    @Override
    public ByteBuf serializeWithLength(MessageWriter writer) {
        byte[] serializedBytes = MessageReader.getByteArraySafe(serialize(new MessageWriter()));
        writer.writeByte(getPacketTypeId());
        writer.writeInt16(getNonce());
        writer.writeBytes(serializedBytes);
        return writer.getByteBuf();
    }

    public int getRetransmissions() {
        return retransmissions;
    }

    public void setRetransmissions(int retransmissions) {
        this.retransmissions = retransmissions;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged() {
        this.acknowledged = true;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public long getNextResendDelay() {
        return nextResendDelay;
    }

    public void setNextResendDelay(long nextResendDelay) {
        this.nextResendDelay = nextResendDelay;
    }
}
