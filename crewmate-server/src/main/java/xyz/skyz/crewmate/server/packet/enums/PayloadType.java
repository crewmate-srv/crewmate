package xyz.skyz.crewmate.server.packet.enums;

public enum PayloadType {

    HOST_GAME(0),
    JOIN_GAME(1),
    START_GAME(2),
    REMOVE_GAME(3),
    REMOVE_PLAYER(4),
    GAME_DATA(5),
    GAME_DATA_TO(6),
    JOINED_GAME(7),
    END_GAME(8),
    GET_GAME_LIST(9),
    ALTER_GAME(10),
    KICK_PLAYER(11),
    WAIT_FOR_HOST(12),
    REDIRECT(13),
    RESELECT_SERVER(14),
    GET_GAME_LIST_V2(16);

    private int typeId;

    PayloadType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static PayloadType getById(int id) {
        for (PayloadType value : values()) {
            if (value.getTypeId() == id) {
                return value;
            }
        }
        return null;
    }
}