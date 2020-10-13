package xyz.skyz.crewmate.common.data;

import java.util.ArrayList;
import java.util.List;

public enum GameKeywords {

    ALL(0),
    OTHER(1),
    SPANISH(2),
    KOREAN(4),
    RUSSIAN(8),
    PORTUGUESE(16),
    ARABIC(32),
    FILIPINO(64),
    POLISH(128),
    ENGLISH(256);

    private int bitmask;

    GameKeywords(int bitmask) {
        this.bitmask = bitmask;
    }

    public int getBitmask() {
        return bitmask;
    }

    public static List<GameKeywords> parseBitmask(int value) {
        List<GameKeywords> gameKeywords = new ArrayList<>();
        for (GameKeywords gameKeyword : values()) {
            if ((value & gameKeyword.getBitmask()) == gameKeyword.getBitmask()) {
                gameKeywords.add(gameKeyword);
            }
        }
        return gameKeywords;
    }

    public static int getKeywordBitmask(List<GameKeywords> gameKeywords) {
        int value = 0;
        for (GameKeywords gameKeyword : gameKeywords) {
            value |= gameKeyword.getBitmask();
        }
        return value;
    }
}
