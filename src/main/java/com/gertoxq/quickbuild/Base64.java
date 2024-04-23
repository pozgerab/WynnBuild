package com.gertoxq.quickbuild;

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
            int32 >>= 6;
        } while (int32 != 0);
        return result.toString();
    }
    public static String fromInt2(int int32) {
        StringBuilder result = new StringBuilder();
        while (true) {
            result.insert(0, digits[int32 & 0x3f]);
            int32 >>= 6;
            if (int32 == 0) {
                break;
            }
        }
        return result.toString().length() == 2 ? result.toString() : "0".repeat(2-result.length())+ result;
    }

    public static String fromInt3(int int32) {
        StringBuilder result = new StringBuilder();
        while (true) {
            result.insert(0, digits[int32 & 0x3f]);
            int32 >>= 6;
            if (int32 == 0) {
                break;
            }
        }
        return result.toString().length() == 3 ? result.toString() : "0".repeat(3-result.length())+ result;
    }

    public static String fromIntN(int int32, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.insert(0, digits[int32 & 0x3f]);
            int32 >>= 6;
        }
        return result.toString();
    }

    public static int toInt(String digitsStr) {
        int result = 0;
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
}