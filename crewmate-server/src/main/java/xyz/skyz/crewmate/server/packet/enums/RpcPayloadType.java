package xyz.skyz.crewmate.server.packet.enums;

public enum RpcPayloadType {

    PLAY_ANIMATION(0),
    COMPLETE_TASK(1),
    SYNC_SETTINGS(2),
    SET_INFECTED(3),
    EXILED(4),
    CHECK_NAME(5),
    SET_NAME(6),
    CHECK_COLOR(7),
    SET_COLOR(8),
    SET_HAT(9),
    SET_SKIN(10),
    REPORT_DEAD_BODY(11),
    MURDER_PLAYER(12),
    SEND_CHAT(13),
    START_MEETING(14),
    SET_SCANNER(15),
    SEND_CHAT_NOTE(16),
    SET_PET(17),
    SET_START_COUNTER(18),
    ENTER_VENT(19),
    EXIT_VENT(20),
    SNAP_TO(21),
    CLOSE(22),
    VOTING_COMPLETE(23),
    CAST_VOTE(24),
    CLEAR_VOTE(25),
    ADD_VOTE(26),
    CLOSE_DOORS_OF_TYPE(27),
    REPAIR_SYSTEM(28),
    SET_TASKS(29);

    private int typeId;

    RpcPayloadType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static RpcPayloadType getById(int id) {
        for (RpcPayloadType value : values()) {
            if (value.getTypeId() == id) {
                return value;
            }
        }
        return null;
    }
}
