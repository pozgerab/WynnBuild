package com.gertoxq.wynnbuild.base.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Base64 {
    private static final String digitsStr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-";
    private static final char[] digits = digitsStr.toCharArray();
    private static final Map<Character, Integer> digitsMap = new HashMap<>();

    static {
        for (int i = 0; i < digits.length; i++) {
            digitsMap.put(digits[i], i);
        }
    }

    public static String fromIntV(int int32) {
        StringBuilder result = new StringBuilder();
        do {
            result.insert(0, digits[int32 & 0x3f]);
            int32 >>>= 6;
        } while (int32 != 0);
        return result.toString();
    }

    public static String fromIntN(long int32, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            result.insert(0, digits[(int) (int32 & 0x3f)]);
            int32 >>>= 6;
        }
        return result.toString();
    }

    public static long toInt(String digitsStr) {
        long result = 0;
        char[] digits = digitsStr.toCharArray();
        for (char digit : digits) {
            result = (result << 6) + digitsMap.get(digit);
        }
        return result;
    }

    public static int toIntSigned(String digitsStr) {
        int result = 0;
        char[] digits = digitsStr.toCharArray();
        if (digits.length > 0 && (digitsMap.get(digits[0]) & 0x20) != 0) {
            result = -1;
        }
        for (char digit : digits) {
            result = (result << 6) + digitsMap.get(digit);
        }
        return result;
    }

    public static boolean isB64(String string) {
        return Arrays.stream(string.split("")).allMatch(c -> digitsMap.containsKey(c.toCharArray()[0]));
    }

    public static boolean isB64CodePoint(int codePoint) {
        boolean isNumber = (codePoint > 47 && codePoint < 58);
        boolean isAlphanumLowercase = (codePoint > 96 && codePoint < 123);
        boolean isAlphanumUppercase = (codePoint > 64 && codePoint < 91);
        boolean isPlusMinus = (codePoint == 43) || (codePoint == 45);
        return isNumber || isAlphanumLowercase || isAlphanumUppercase || isPlusMinus;
    }

    public static String fromBytes(byte[] arr) {
        StringBuilder b64String = new StringBuilder();
        int rem = 0;

        for (int i = 0; i < arr.length; i++) {
            int iMod = i % 3;
            int num = ((arr[i] & 0xFF) << (iMod * 2)) & 0x3F | rem;
            rem = (arr[i] & 0xFF) >> (6 - iMod * 2);
            b64String.insert(0, digits[num]);
            if (iMod == 2) {
                b64String.insert(0, digits[rem]);
                rem = 0;
            }
        }
        if (arr.length % 3 != 0) {
            b64String.insert(0, digits[rem]);
        }

        return b64String.toString();
    }

    public static byte[] intoBytes(String b64String) {
        byte[] arr = new byte[(int) Math.floor(b64String.length() * (6.0 / 8.0))];

        for (int i = 0, j = b64String.length() - 1; j > 0; --j, ++i) {
            int iMod = i % 3;
            arr[i] = (byte) (toInt(String.valueOf(b64String.charAt(j))) >>> (iMod * 2));
            arr[i] |= (byte) ((toInt(String.valueOf(b64String.charAt(j - 1))) << (6 - iMod * 2)) & 0xFF);
            if (iMod == 2) {
                --j;
            }
        }

        return arr;
    }
}