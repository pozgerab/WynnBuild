package com.gertoxq.quickbuild;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class Powder {

    public static List<String> elements = List.of("e", "t", "w", "f", "a");
    public static Map<String, Integer> powderIDs = new HashMap<>();
    public static Map<Integer, String> powderNames = new HashMap<>();
    public static int MAX_POWDER_LEVEL = 6;
    public static String schema = "\\[\\s*([0-3])\\s*/\\s*([1-3])\\s*] Powder Slots \\[\\s*([✤✦✽✹❋])(\\s+([✤✦✽✹❋])){0,2}\\s*]";
    public static Pattern regex = Pattern.compile(schema);
    public static int DEFAULT_POWDER_LEVEL;

    static {
        int _powderID = 0;
        for (String x : elements) {
            for (int i = 1; i <= MAX_POWDER_LEVEL; ++i) {
                powderIDs.put(x.toUpperCase() + i, _powderID); // Uppercase
                powderNames.put(_powderID, x + i);
                _powderID++;
            }
        }
    }

    public static String getPowderString(List<List<Integer>> powders) {
        StringBuilder buildString = new StringBuilder();

        for (List<Integer> _powderset : powders) {
            int nBits = (int) Math.ceil((double) _powderset.size() / 6);
            buildString.append(Base64.fromIntN(nBits, 1));

            List<Integer> powderset = new ArrayList<>(_powderset);

            while (!powderset.isEmpty()) {

                List<Integer> firstSix = new ArrayList<>(powderset.subList(0, Math.min(6, powderset.size())));
                Collections.reverse(firstSix);

                int powderHash = 0;
                for (Integer powder : firstSix) {
                    powderHash = (powderHash << 5) + 1 + powder;
                }

                buildString.append(Base64.fromIntN(powderHash, 5));

                powderset = powderset.subList(Math.min(6, powderset.size()), powderset.size());
            }
        }

        return buildString.toString();
    }

    public static @Nullable List<Integer> getPowderFromString(String string) {
        if (!regex.matcher(string).matches()) return null;
        Map<Element, Integer> powderMap = new HashMap<>();
        for (Element element : Element.values()) {
            if (string.contains(element.icon)) {
                powderMap.put(element, powderMap.getOrDefault(element, 0) + StringUtils.countMatches(string, element.icon));
            }
        }
        List<Integer> powders = new ArrayList<>();
        powderMap.forEach((element, integer) -> {
            for (int i = 0; i < integer; i++) {
                powders.add(powderIDs.get(element.toString().charAt(0) + String.valueOf(DEFAULT_POWDER_LEVEL)));
            }
        });
        return powders;
    }

    public enum Element {
        EARTH("✤"),
        THUNDER("✦"),
        WATER("✽"),
        FIRE("✹"),
        AIR("❋");
        public final String icon;

        Element(String icon) {
            this.icon = icon;
        }
    }

}
