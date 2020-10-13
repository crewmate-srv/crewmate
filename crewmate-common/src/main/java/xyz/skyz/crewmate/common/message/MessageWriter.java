package xyz.skyz.crewmate.common.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MessageWriter {

    private ByteBuf byteBuf;

    private List<Integer> messageStarts = new ArrayList<>();

    public MessageWriter() {
        this.byteBuf = Unpooled.directBuffer(1);
    }

    public void writeBoolean(boolean value) {
        byteBuf.writeBoolean(value);
    }

    public void writeSByte(byte value) {
        byteBuf.writeByte(value);
    }

    public void writeByte(short value) {
        byteBuf.writeByte(value); // ?
    }

    public void writeUInt16(int value) {
        byteBuf.writeShortLE(value);
    }

    public void writeInt16(short value) {
        byteBuf.writeShortLE(value);
    }

    public void writeUInt32(long value) {
        byteBuf.writeLongLE(value); // ?
    }

    public void writeInt32(int value) {
        byteBuf.writeIntLE(value);
    }

    public void writeSingle(float value) {
        byteBuf.writeFloat(value);
    }

    public void writeString(String value) {
        int len = value.length();
        writePackedInt32(len);
        byteBuf.writeCharSequence(value.subSequence(0, len), Charset.defaultCharset());
    }

    public void writeBytes(byte[] value) {
        byteBuf.writeBytes(value);
    }

    public void writeBytesAndSize(byte[] value) {
        int len = value.length;
        writePackedInt32(len);
        byteBuf.writeBytes(value);
    }

    public void writePackedInt32(int value) {
        this.writePackedUInt32(value);
    }

    public void writePackedUInt32(long value) {
        do {
            long b = value;
            if (value >= (0x80 & 0xFF)) {
                b |= (0x80 & 0xFF);
            }
            this.writeByte((short) b);
            value >>= 7;
        } while (value > 0);
    }

    public void startMessage(short typeFlag) {
        messageStarts.add(this.byteBuf.writerIndex());
        writeUInt16(0);
        writeByte(typeFlag);
    }

    public void endMessage() {
        int lastMessageStart = messageStarts.remove(messageStarts.size() - 1);
        this.byteBuf.markWriterIndex();
        int len = this.byteBuf.writerIndex() - lastMessageStart - 3; // Minus length and type byte
        this.byteBuf.writerIndex(lastMessageStart);
        writeUInt16(len);
        this.byteBuf.resetWriterIndex();
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }
}
