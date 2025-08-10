package com.gertoxq.wynnbuild.base;

import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.gertoxq.wynnbuild.identifications.metric.Metrics.SLOTS;

public class Powder {

    public static final int MAX_POWDER_LEVEL = 6;
    public static final Pattern POWDER_PATTERN = Pattern.compile("\\[\\s*([0-9]{1,2})\\s*/\\s*([0-9]{1,2})\\s*] Powder Slots \\[\\s*([✤✦❉✹❋](?:\\s*[✤✦❉✹❋]){0,23})?\\s*]");
    public static final Powder EMPTY_POWDER = new Powder(null, 1, -1);
    private static final Pattern abbrevPattern = Pattern.compile("[etwfa][1-6]", Pattern.CASE_INSENSITIVE);
    private final static Map<Integer, Powder> powderMap = new HashMap<>();
    public static int DEFAULT_POWDER_LEVEL = 6;

    static {
        int powderID = 0;
        for (Element element : Element.values()) {
            for (int i = 1; i <= MAX_POWDER_LEVEL; ++i) {
                Powder powder = new Powder(element, i, powderID++);
                powderMap.put(powder.id, powder);
            }
        }
    }

    public final Element element;
    @Range(from = 1, to = 6)
    public final int level;
    public final int id;

    private Powder(Element element, @Range(from = 1, to = 6) int level, int id) {
        this.element = element;
        this.level = level;
        this.id = id;
    }

    public static boolean isValidPowder(String abbreviation) {
        return abbrevPattern.matcher(abbreviation).matches();
    }

    public static boolean isValidPowder(int powderId) {
        return powderMap.containsKey(powderId);
    }

    public static Powder getPowder(int powderId) {
        return powderMap.get(powderId);
    }

    public static Powder getPowder(String abbreviation) {
        if (!isValidPowder(abbreviation)) {
            throw new IllegalArgumentException("Invalid powder abbreviation: " + abbreviation);
        }
        String el = abbreviation.substring(0, 1).toLowerCase();
        int lvl = Integer.parseInt(abbreviation.substring(1));
        int idx = Arrays.stream(Element.values()).map(element1 -> element1.name().substring(0, 1)).toList().indexOf(el);
        int id = idx * MAX_POWDER_LEVEL + lvl - 1;
        return getPowder(id);
    }

    public static Powder getPowder(Element element, @Range(from = 1, to = 6) int level) {
        return getPowder(element.ordinal() * MAX_POWDER_LEVEL + level - 1);
    }

    public static List<Powder> getPowderFromString(String string) {
        List<Powder> powders = new ArrayList<>();
        Matcher matcher = SLOTS.pattern().matcher(string);
        if (!matcher.matches()) return powders;
        for (Element element : Element.values()) {
            if (string.contains(element.symbol)) {
                powders.addAll(Collections.nCopies(StringUtils.countMatches(string, element.symbol), getPowder(element, DEFAULT_POWDER_LEVEL)));
            }
        }
        return powders;
    }

    @Override
    public String toString() {
        return element.icon + " " + element.name() + this.level + " = " + this.id;
    }

    public enum Element {
        EARTH("✤", "\uE001", Formatting.DARK_GREEN),
        THUNDER("✦", "\uE003", Formatting.YELLOW),
        WATER("❉", "\uE004", Formatting.AQUA),
        FIRE("✹", "\uE002", Formatting.RED),
        AIR("❋", "\uE000", Formatting.WHITE);
        public final String icon;
        public final String symbol;
        public final Formatting format;

        Element(String icon, String symbol, Formatting format) {
            this.icon = icon;
            this.symbol = symbol;
            this.format = format;
        }

        public static Element getInstance(String from) {
            return Stream.of(Element.values()).filter(element -> element.name().equalsIgnoreCase(from)).findAny().orElse(null);
        }
    }

}
