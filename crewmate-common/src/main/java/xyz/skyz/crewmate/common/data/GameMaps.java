package xyz.skyz.crewmate.common.data;

import java.util.ArrayList;
import java.util.List;

public enum GameMaps {

    SKELD((byte) 0),
    MIRA_HQ((byte) 1),
    POLUS((byte) 2);

    private byte bitmask;

    GameMaps(byte bitmask) {
        this.bitmask = bitmask;
    }

    public byte getBitmask() {
        return bitmask;
    }

    public static List<GameMaps> parseBitmask(byte value) {
        List<GameMaps> gameMaps = new ArrayList<>();
        for (GameMaps gameMap : values()) {
            if ((value & gameMap.getBitmask()) == gameMap.getBitmask()) {
                gameMaps.add(gameMap);
            }
        }
        return gameMaps;
    }

    public static byte getStatusValue(List<GameMaps> gameMaps) {
        byte value = 0;
        for (GameMaps gameMap : gameMaps) {
            value |= gameMap.getBitmask();
        }
        return value;
    }
}
