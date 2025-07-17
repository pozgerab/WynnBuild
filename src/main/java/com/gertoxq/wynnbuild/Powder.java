package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.base.Base64;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Powder {

    public static List<String> elements = List.of("e", "t", "w", "f", "a");
    public static Map<String, Integer> powderIDs = new HashMap<>();
    public static Map<Integer, String> powderNames = new HashMap<>();
    public static int MAX_POWDER_LEVEL = 6;
    public static Pattern regex = Pattern.compile("\\[\\s*([0-9]{1,2})\\s*/\\s*([0-9]{1,2})\\s*] Powder Slots \\[\\s*([✤✦❉✹❋](?:\\s*[✤✦❉✹❋]){0,23})?\\s*]");
    public static int DEFAULT_POWDER_LEVEL = 6;

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

    public static String getPowderString(List<List<Element>> powders) {
        StringBuilder buildString = new StringBuilder();

        for (List<Element> _powderset : powders) {
            int nBits = (int) Math.ceil((double) _powderset.size() / 6);
            buildString.append(com.gertoxq.wynnbuild.base.Base64.fromIntN(nBits, 1));

            List<Integer> powderset = new ArrayList<>(_powderset).stream().map(element -> powderIDs.get(element.name().substring(0, 1) + DEFAULT_POWDER_LEVEL)).toList();
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

    public static @Nullable List<Element> getPowderFromString(String string) {
        if (!regex.matcher(string).matches()) return null;
        List<Element> powders = new ArrayList<>();
        for (Element element : Element.values()) {
            if (string.contains(element.icon)) {
                powders.addAll(Collections.nCopies(StringUtils.countMatches(string, element.icon), element));
            }
        }
        return powders;
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
