package xyz.skyz.crewmate.common.message;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MessageReader {

    public ByteBuf byteBuf;

    public int length;

    public static MessageReader get(ByteBuf buffer) {
        MessageReader output = new MessageReader();
        output.byteBuf = buffer;
        output.length = getByteArraySafe(buffer).length;
        return output;
    }

    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    public byte readSByte() {
        return byteBuf.readByte();
    }

    public short readByte() {
        return byteBuf.readUnsignedByte();
    }

    public int readUInt16() {
        return byteBuf.readUnsignedShortLE();
    }

    public short readInt16() {
        return byteBuf.readShortLE();
    }

    public long readUInt32() {
        return byteBuf.readUnsignedIntLE();
    }

    public int readInt32() {
        return byteBuf.readIntLE();
    }

    public float readSingle() {
        return byteBuf.readFloat();
    }

    public String readString() {
        int len = this.readPackedInt32();
        CharSequence charSequence = byteBuf.readCharSequence(len, Charset.defaultCharset());
        return charSequence.toString();
    }

    public byte[] readBytesAndSize() {
        int len = this.readPackedInt32();
        return this.readBytes(len);
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        return bytes;
    }

    public int readPackedInt32() {
        return (int) this.readPackedUInt32();
    }

    public long readPackedUInt32() {
        boolean readMore = true;
        int shift = 0;
        long output = 0;

        while (readMore) {

            long b = (long) this.readByte();
            if (b >= 0x80) {
                readMore = true;
                b ^= 0x80;
            } else {
                readMore = false;
            }
            output |= (long) (b << shift);
            shift += 7;
        }

        return output;
    }

    public MessageReader readMessage() {
        int length = readUInt16();
        short typeId = readByte();
        this.byteBuf.readerIndex(this.byteBuf.readerIndex() - 3); // Set back so we can read full embedded message
        return MessageReader.get(this.byteBuf.readBytes(length + 3));
    }

    public static byte[] getByteArraySafe(ByteBuf byteBuf) {
        if (byteBuf.hasArray()) {
            return byteBuf.array();
        }
        int indexTemp = byteBuf.readerIndex();
        byteBuf.readerIndex(0);
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
        byteBuf.readerIndex(indexTemp);
        return bytes;
    }
}
