package xyz.skyz.crewmate.server.packet.enums;

public enum NetPacketType {

    NORMAL(0),
    RELIABLE(1),
    HELLO(8),
    DISCONNECT(9),
    ACK(10),
    FRAGMENT(11),
    PING(12);

    private final int typeId;

    NetPacketType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static NetPacketType getById(int id) {
        for (NetPacketType value : values()) {
            if (value.getTypeId() == id) {
                return value;
            }
        }
        return null;
    }
}
