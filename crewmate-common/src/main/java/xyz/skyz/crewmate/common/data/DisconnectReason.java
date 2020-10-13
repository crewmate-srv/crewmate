package xyz.skyz.crewmate.common.data;

public enum DisconnectReason {

    EXIT_GAME(0),
    GAME_FULL(1),
    GAME_STARTED(2),
    GAME_MISSING(3),
    INCORRECT_VERSION(5),
    BANNED(6),
    KICKED(7),
    INVALID_NAME(9),
    HACKING(10),
    DESTROY(16),
    ERROR(17),
    INCORRECT_GAME(18),
    SERVER_REQUEST(19),
    SERVER_FULL(20);

    private int typeId;

    DisconnectReason(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
