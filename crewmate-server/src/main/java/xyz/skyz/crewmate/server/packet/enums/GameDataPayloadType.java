package xyz.skyz.crewmate.server.packet.enums;

public enum GameDataPayloadType {

    DATA(1),
    RPC(2),
    SPAWN(4),
    DESPAWN(5),
    SCENE_CHANGE(6),
    READY(7),
    CHANGE_SETTINGS(8);

    private int typeId;

    GameDataPayloadType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public static GameDataPayloadType getById(int id) {
        for (GameDataPayloadType value : values()) {
            if (value.getTypeId() == id) {
                return value;
            }
        }
        return null;
    }
}
