package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.base.util.Base64;
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

    public static String getPowderString(List<List<Powder>> allPowders) {
        StringBuilder buildString = new StringBuilder();

        for (List<Powder> powders : allPowders) {
            int nBits = (int) Math.ceil((double) powders.size() / 6);
            buildString.append(Base64.fromIntN(nBits, 1));

            List<Integer> powderset = powders.stream().map(element -> element.id).toList();
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

    public static List<Powder> getPowderFromString(String string) {
        List<Powder> powders = new ArrayList<>();
        Matcher matcher = SLOTS.pattern().matcher(string);
        if (!matcher.matches()) return powders;
        for (Element element : Element.values()) {
            if (string.contains(element.icon)) {
                powders.addAll(Collections.nCopies(StringUtils.countMatches(matcher.group("powders"), element.icon), getPowder(element, DEFAULT_POWDER_LEVEL)));
            }
        }
        return powders;
    }

    @Override
    public String toString() {
        return element.icon + " " + element.name() + this.level + " = " + this.id;
    }

    public enum Element {
        EARTH("✤", Formatting.DARK_GREEN),
        THUNDER("✦", Formatting.YELLOW),
        WATER("❉", Formatting.AQUA),
        FIRE("✹", Formatting.RED),
        AIR("❋", Formatting.WHITE);
        public final String icon;
        public final Formatting format;

        Element(String icon, Formatting format) {
            this.icon = icon;
            this.format = format;
        }

        public static Element getInstance(String from) {
            return Stream.of(Element.values()).filter(element -> element.name().equalsIgnoreCase(from)).findAny().orElse(null);
        }
    }

}
