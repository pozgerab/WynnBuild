package com.gertoxq.wynnbuild.base.util;

import net.minecraft.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class BootstringEncoder {

    private static final List<Character> base = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".chars().mapToObj(c -> (char) c).toList();
    private static final Map<Character, Integer> baseMap = new HashMap<>();
    private static final int b = base.size();

    static {
        for (int i = 0; i < base.size(); i++) {
            baseMap.put(base.get(i), i);
        }
    }

    private final int initial_n;
    private final int tmin;
    private final int tmax;
    private final int initial_bias;
    private final int damp;
    private final int skew;
    private final char delimiter;

    public BootstringEncoder(int initial_n, int tmin, int tmax, int initial_bias, int damp, int skew, char delimiter) {
        this.initial_n = initial_n;
        this.tmin = tmin;
        this.tmax = tmax;
        this.initial_bias = initial_bias;
        this.damp = damp;
        this.skew = skew;
        this.delimiter = delimiter;
    }

    private int threshold(int i, int bias) {
        return Math.max(tmin, Math.min(b * (i + 1) - bias, tmax));
    }

    private Pair<Integer, String> decodeVLI(String str, int bias) {
        int i = 0;
        int w = 1;
        int res = 0;
        for (int k = 0; k < str.length(); k++) {
            char char_ = str.charAt(k);
            int d = baseMap.get(char_);
            res += d * w;
            int t = threshold(i, bias);
            if (d < t) {
                break;
            }
            w *= b - t;
            i++;
        }
        return new Pair<>(res, str.substring(i + 1));
    }

    private String encodeVLI(int value, int bias) {
        StringBuilder enc = new StringBuilder();
        int i = 0;
        while (true) {
            int t = threshold(i, bias);
            if (value < t) {
                enc.append(base.get(value));
                break;
            }
            enc.append(base.get(t + ((value - t) % (b - t))));
            value = (value - t) / (b - t);
            i++;
        }
        return enc.toString();
    }

    private int adaptBias(int delta, int length, boolean firstIteration) {
        delta = firstIteration ? Math.floorDiv(delta, damp) : delta >> 1;
        delta = Math.floorDiv(delta, length);
        int k = 0;
        int thresh = ((b - tmin) * tmax) / 2;
        while (delta > thresh) {
            delta /= (b - tmin);
            k += b;
        }
        return k + (((b - tmin + 1) * delta) / (delta + skew));
    }

    private SplitResult splitBasicExtended(String raw) {
        List<String> basic = new ArrayList<>();
        Set<Integer> nonBasic = new TreeSet<>();
        for (int i = 0; i < raw.length(); ) {
            int codePoint = raw.codePointAt(i);
            if (Base64.isB64CodePoint(codePoint)) {
                basic.add(String.valueOf((char) codePoint));
            } else {
                nonBasic.add(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        int encodedCount = basic.size();
        if (encodedCount > 0) {
            basic.add(String.valueOf(delimiter));
        }
        return new SplitResult(basic, nonBasic, encodedCount);
    }

    public String encode(String raw) {
        SplitResult parts = splitBasicExtended(raw);
        List<String> basic = parts.basic;
        Set<Integer> nonBasic = parts.nonBasic;
        int encodedCount = parts.encodedCount;

        if (nonBasic.isEmpty()) {
            return String.join("", basic);
        }

        int delta = 0;
        int n = initial_n;
        int bias = initial_bias;
        boolean firstIteration = true;

        for (int codepoint : nonBasic) {
            delta += (codepoint - n) * (encodedCount + 1);
            n = codepoint;
            for (int i = 0; i < raw.length(); i++) {
                String c = String.valueOf(raw.charAt(i));
                int currCodepoint = c.codePointAt(0);
                if (currCodepoint < n || Base64.isB64CodePoint(currCodepoint)) {
                    delta++;
                }
                if (currCodepoint == n) {
                    basic.add(encodeVLI(delta, bias));
                    encodedCount++;
                    bias = adaptBias(delta, encodedCount, firstIteration);
                    delta = 0;
                    firstIteration = false;
                }
            }
            delta++;
            n++;
        }
        return String.join("", basic);
    }

    public String decode(String bootStr) {
        int delimIdx = bootStr.lastIndexOf(delimiter);
        String encodedStr = delimIdx < 0 ? "" : bootStr.substring(0, delimIdx);
        String deltaStr = bootStr.substring(delimIdx + 1);

        List<Integer> codepoints = encodedStr.codePoints().boxed().collect(Collectors.toList());
        int bias = initial_bias;
        int n = initial_n;
        int i = 0;
        boolean firstIteration = true;

        while (!deltaStr.isEmpty()) {
            Pair<Integer, String> p = decodeVLI(deltaStr, bias);
            int delta = p.getLeft();
            deltaStr = p.getRight();

            i += delta;
            n += i / (codepoints.size() + 1);
            i %= (codepoints.size() + 1);

            codepoints.add(i, n);

            bias = adaptBias(delta, codepoints.size(), firstIteration);
            firstIteration = false;
            i++;
        }

        StringBuilder sb = new StringBuilder();
        for (int cp : codepoints) {
            sb.append(Character.toChars(cp));
        }
        return sb.toString();
    }

    private record SplitResult(List<String> basic, Set<Integer> nonBasic, int encodedCount) {
    }
}