package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.build.Build;
import com.wynntils.models.items.items.game.GearItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    public static Text getBuildTemplate(String url, @Range(from = 0, to = 3) boolean precise) {
        int precision = precise ? 1 : 0;
        return Text.literal("\n(").styled(style -> style.withColor(Formatting.DARK_GRAY))
                .append(Text.literal(Build.PRECISION_OPTIONS.get(precision)).styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true).withUnderline(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Precision Option: ").append(Build.PRECISION_OPTIONS.get(precision)).append("\n").append(Build.PRECISION_TOOLTIPS.get(precision)).append("\n\n")
                                        .append(Text.literal("CLICK TO CHANGE PRECISION (/build config)").styled(style1 -> style1.withColor(Formatting.GREEN)))))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/build config"))))
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

}
