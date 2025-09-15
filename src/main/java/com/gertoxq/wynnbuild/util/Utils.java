package com.gertoxq.wynnbuild.util;

import com.wynntils.models.items.items.game.GearItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.gertoxq.wynnbuild.WynnBuild.getConfig;
import static com.gertoxq.wynnbuild.WynnBuild.getConfigManager;

public class Utils {

    public static String withSign(int number) {
        return (number >= 0 ? "+" : "") + number;
    }

    public static int mod(int v, int m) {
        return ((v % m) + m) % m;
    }

    public static <A, B> List<Map.Entry<A, B>> zip2(List<A> a, List<B> b) {
        List<Map.Entry<A, B>> result = new ArrayList<>();
        int size = Math.min(a.size(), b.size());
        for (int i = 0; i < size; i++) {
            result.add(new AbstractMap.SimpleEntry<>(a.get(i), b.get(i)));
        }
        return result;
    }

    public static @Nullable List<Text> getLore(@NotNull ItemStack itemStack) {
        LoreComponent loreComp = itemStack.get(DataComponentTypes.LORE);
        if (loreComp == null) return null;
        return loreComp.lines();
    }

    public static String removeFormat(@NotNull String str) {
        return str.replaceAll("§.", "").replaceAll("\\*", "");
    }

    public static String removeNum(String str) {
        var rep = List.of(" 1", " 2", " 3", " III", " II", " I");
        AtomicReference<String> news = new AtomicReference<>(str);
        rep.forEach(s -> news.set(news.get().replace(s, "")));
        return news.get();
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

            if (c < 128) {
                sb.append(c);
            } else {
                sb.append(String.format("\\u%04X", (int) c));
            }
        }
        return sb.toString();
    }

    public static Text getItemPrintTemplate(GearItem item, String fullHash, String url) {
        return Text.literal("\nItem is generated   ").styled(style -> style.withColor(Formatting.DARK_AQUA))
                .append(Text.literal(item.getName()).styled(style -> style.withColor(item.getGearTier().getChatFormatting())))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("COPY").styled(style -> style.withColor(Formatting.GREEN)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(url)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url))
                        .withUnderline(true)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("OPEN").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .withUnderline(true)
                        .withColor(Formatting.RED)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("COPY HASH").styled(style -> style.withColor(Formatting.YELLOW)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(fullHash)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fullHash))
                        .withUnderline(true)))
                .append("\n").styled(style -> style.withBold(true));
    }

    public static Text getBuildTemplate(String url) {
        return Text.literal("\n(").styled(style -> style.withColor(Formatting.DARK_GRAY))
                .append(Text.literal("Options").styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, optionsTooltip())).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/build config"))))
                .append(Text.literal(")").styled(style -> style.withColor(Formatting.DARK_GRAY)))
                .append(Text.literal(" Your build is generated   ").styled(style -> style.withColor(Formatting.GOLD))
                        .append(Text.literal("COPY").styled(style -> style.withColor(Formatting.GREEN)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(url)))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url))
                                .withUnderline(true)))
                        .append("  ")
                        .append(Text.literal("OPEN").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .withUnderline(true)
                                .withColor(Formatting.RED)))
                        .append("\n").styled(style -> style.withBold(true)));
    }

    public static MutableText optionsTooltip() {
        return Text.literal("Options").styled(style -> style.withColor(Formatting.DARK_AQUA))
                .append("\n\n")
                .append(Text.literal("Precision: ").styled(style -> style.withColor(Formatting.GRAY))
                        .append(Text.literal(getConfigManager().getConfig().getPrecision() == 1 ? "ON" : "OFF").styled(style -> style.withColor(getConfig().getPrecision() == 1 ? Formatting.GREEN : Formatting.RED))))
                .append("\n")
                .append(Text.literal("Include Tomes: ").styled(style -> style.withColor(Formatting.GRAY))
                        .append(Text.literal(getConfigManager().getConfig().isIncludeTomes() ? "ON" : "OFF").styled(style -> style.withColor(getConfig().isIncludeTomes() ? Formatting.GREEN : Formatting.RED))))
                .append("\n")
                .append(Text.literal("Include Aspects: ").styled(style -> style.withColor(Formatting.GRAY))
                        .append(Text.literal(getConfigManager().getConfig().isIncludeAspects() ? "ON" : "OFF").styled(style -> style.withColor(getConfig().isIncludeAspects() ? Formatting.GREEN : Formatting.RED))))
                .append("\n\n")
                .append(Text.literal("Click to change (/build config)").styled(style -> style.withColor(Formatting.DARK_GRAY)));
    }

}
