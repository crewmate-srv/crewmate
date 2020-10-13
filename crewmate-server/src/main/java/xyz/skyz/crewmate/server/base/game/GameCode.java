package xyz.skyz.crewmate.server.base.game;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class GameCode {

    private static final Random random = new Random();
    private static final String V2_CHAR_MAPPINGS = "QWXRTYLPESDFGHUJKZOCVBINMA";
    private static final byte[] V2_BYTE_MAPPINGS = new byte[]{0x19, 0x15, 0x13, 0x0A, 0x08, 0x0B, 0x0C, 0x0D, 0x16,
            0x0F, 0x10, 0x06, 0x18, 0x17, 0x12, 0x07, 0x00, 0x03, 0x09, 0x04, 0x0E, 0x14, 0x01, 0x02, 0x05, 0x11};

    public static String generateRandomGameCodeV2() {
        char[] dataChar = new char[6];
        for (int i = 0; i < dataChar.length; i++) {
            dataChar[i] = V2_CHAR_MAPPINGS.charAt(random.nextInt(V2_CHAR_MAPPINGS.length() - 1));
        }
        return new String(dataChar);
    }

    /*
     * The following are sourced from:
     * Source: https://github.com/TheNullicorn/among-us-api/wiki/Game-Codes
     */

    public static int gameCodeV2StringToNumber(String gameCode) {
        byte b1 = V2_BYTE_MAPPINGS[gameCode.charAt(0) - 65];
        byte b2 = V2_BYTE_MAPPINGS[gameCode.charAt(1) - 65];
        byte b3 = V2_BYTE_MAPPINGS[gameCode.charAt(2) - 65];
        byte b4 = V2_BYTE_MAPPINGS[gameCode.charAt(3) - 65];
        byte b5 = V2_BYTE_MAPPINGS[gameCode.charAt(4) - 65];
        byte b6 = V2_BYTE_MAPPINGS[gameCode.charAt(5) - 65];

        int msb = (b1 + 26 * b2) & 0x3FF;
        int lsb = (b3 + 26 * (b4 + 26 * (b5 + 26 * b6)));
        return msb | ((lsb << 10) & 0x3FFFFC00) | 0x80000000;
    }

    public static String gameCodeV2NumberToString(int gameCode) {
        int msb = gameCode & 0x3FF;
        int lsb = (gameCode >> 10) & 0xFFFFF;
        byte[] strBytes = new byte[]{
                (byte) V2_CHAR_MAPPINGS.charAt(msb % 26),
                (byte) V2_CHAR_MAPPINGS.charAt(msb / 26),
                (byte) V2_CHAR_MAPPINGS.charAt(lsb % 26),
                (byte) V2_CHAR_MAPPINGS.charAt(lsb / 26 % 26),
                (byte) V2_CHAR_MAPPINGS.charAt(lsb / (26 * 26) % 26),
                (byte) V2_CHAR_MAPPINGS.charAt(lsb / (26 * 26 * 26) % 26)
        };
        return new String(strBytes, StandardCharsets.UTF_8);
    }
}
