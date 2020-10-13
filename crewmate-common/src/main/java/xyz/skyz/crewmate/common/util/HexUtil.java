package xyz.skyz.crewmate.common.util;

import java.nio.ByteBuffer;

public class HexUtil {

    public static String byteBufToHex(ByteBuffer byteBuffer) {
        StringBuilder stringBuilder = new StringBuilder();
        while (byteBuffer.hasRemaining()) {
            stringBuilder.append(String.format("%02x", byteBuffer.get()).toUpperCase());
            if (byteBuffer.hasRemaining()) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public static String hexDump(byte[] bytes)
    {
        if (bytes == null) {
            return "<null>";
        }
        int bytesPerLine = 16;
        int bytesLength = bytes.length;
        int newLineLength = "\n".length();

        char[] hexChars = "0123456789ABCDEF".toCharArray();

        int firstHexColumn =
                8                   // 8 characters for the address
                + 3;                  // 3 spaces

        int firstCharColumn = firstHexColumn
                + bytesPerLine * 3       // - 2 digit for the hexadecimal value and 1 space
                + (bytesPerLine - 1) / 8 // - 1 extra space every 8 characters from the 9th
                + 2;                  // 2 spaces

        int lineLength = firstCharColumn
                + bytesPerLine           // - characters to show the ascii value
                + newLineLength;        // Carriage return and line feed (should normally be 2)

        char[] line = new char[lineLength];
        for (int i = 0; i < line.length; i++) {
            line[i] = ' ';
            if (i == line.length - 1) {
                line[i] = '\n';
            }
        }
        int expectedLines = (bytesLength + bytesPerLine - 1) / bytesPerLine;
        StringBuilder result = new StringBuilder(expectedLines * lineLength);

        for (int i = 0; i < bytesLength; i += bytesPerLine) {
            line[0] = hexChars[(i >> 28) & 0xF];
            line[1] = hexChars[(i >> 24) & 0xF];
            line[2] = hexChars[(i >> 20) & 0xF];
            line[3] = hexChars[(i >> 16) & 0xF];
            line[4] = hexChars[(i >> 12) & 0xF];
            line[5] = hexChars[(i >> 8) & 0xF];
            line[6] = hexChars[(i >> 4) & 0xF];
            line[7] = hexChars[(i >> 0) & 0xF];

            int hexColumn = firstHexColumn;
            int charColumn = firstCharColumn;

            for (int j = 0; j < bytesPerLine; j++) {
                if (j > 0 && (j & 7) == 0) hexColumn++;
                if (i + j >= bytesLength) {
                    line[hexColumn] = ' ';
                    line[hexColumn + 1] = ' ';
                    line[charColumn] = ' ';
                } else {
                    byte b = bytes[i + j];
                    line[hexColumn] = hexChars[(b >> 4) & 0xF];
                    line[hexColumn + 1] = hexChars[b & 0xF];
                    line[charColumn] = (b < 32 ? '.' : (char) b);
                }
                hexColumn += 3;
                charColumn++;
            }
            result.append(line);
        }
        return result.toString();
    }
}
