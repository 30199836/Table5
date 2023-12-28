package com.searesoft.lib;

import java.nio.charset.StandardCharsets;

/**
 * Helper class with various string related utility methods
 */
public class StrUtils {
    public static int elfHash(String str) {
        int res = 0;
        byte[] bytes = str.toLowerCase().getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < bytes.length; i++) {
            res = Integer.rotateLeft(res, 4) + bytes[i];

            int x = res & 0xF0000000;
            if (x != 0) {
                res ^= Integer.rotateRight(x, 24);
                res &= ~x;
            }
        }
        return res & 0x7FFFFFFF;
    }
}
