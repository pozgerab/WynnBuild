package com.gertoxq.wynnbuild.util;

import com.wynntils.models.gear.type.GearTier;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static String withSign(int number) {
        return (number >= 0 ? "+" : "") + number;
    }

    public static int mod(int v, int m) {
        return ((v % m) + m) % m;
    }

    public static <T> Set<T> difference(Set<T> a, Set<T> b) {
        Set<T> copy = new HashSet<>(a);
        copy.removeAll(b);
        return copy;
    }

    public static <A, B> List<Map.Entry<A, B>> zip2(List<A> a, List<B> b) {
        List<Map.Entry<A, B>> result = new ArrayList<>();
        int size = Math.min(a.size(), b.size());
        for (int i = 0; i < size; i++) {
            result.add(new AbstractMap.SimpleEntry<>(a.get(i), b.get(i)));
        }
        return result;
    }

    public static boolean between(int num1, int num2, int target) {
        return num1 <= target && target <= num2;
    }

    public static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String capitalizeAllFirst(String string) {
        return Arrays.stream(string.split(" ")).map(Utils::capitalizeFirst).collect(Collectors.joining(" "));
    }

    public static String escapeToUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c < 128 || c == '§') {
                sb.append(c);
            } else {
                sb.append(String.format("\\u%04X", (int) c));
            }
        }
        return sb.toString();
    }

    public static Text getItemPrintTemplate(String name, GearTier tier, String fullHash, String url) {
        return Text.literal("\nItem is generated   ").styled(style -> style.withColor(Formatting.DARK_AQUA))
                .append(Text.literal(name).styled(style -> style.withColor(tier.getChatFormatting())))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("COPY").styled(style -> style.withColor(Formatting.GREEN)
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal(url)))
                        .withClickEvent(new ClickEvent.CopyToClipboard(url))
                        .withUnderline(true)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("OPEN").styled(style -> style.withClickEvent(new ClickEvent.OpenUrl(URI.create(url)))
                        .withUnderline(true)
                        .withColor(Formatting.RED)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("COPY HASH").styled(style -> style.withColor(Formatting.YELLOW)
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal(fullHash)))
                        .withClickEvent(new ClickEvent.CopyToClipboard(fullHash))
                        .withUnderline(true)))
                .append("\n").styled(style -> style.withBold(true));
    }

}
