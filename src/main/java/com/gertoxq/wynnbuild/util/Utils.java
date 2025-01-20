package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.screens.builder.BuildScreen;
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
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
    private static int failures = 0;

    public static @Nullable List<Text> getLore(@NotNull ItemStack itemStack) {
        LoreComponent loreComp = itemStack.get(DataComponentTypes.LORE);
        if (loreComp == null) return null;
        return loreComp.lines();
    }

    public static String removeFormat(@NotNull String str) {
        return str.replaceAll("§[0-9a-fA-Fklmnor]", "");
    }

    public static String removeNum(String str) {
        var rep = List.of(" 1", " 2", " 3", " III", " II", " I");
        AtomicReference<String> news = new AtomicReference<>(str);
        rep.forEach(s -> news.set(news.get().replace(s, "")));
        return news.get();
    }

    public static MutableText reduceTextList(List<Text> original) {
        MutableText lore = Text.empty();
        for (int i = 0; i < original.size(); i++) {
            Text line = original.get(i);
            lore.append(line);
            if (i != original.size() - 1) {
                lore.append("\n");
            }
        }
        return lore;
    }

    public static Text getItemPrintTemplate(CustomItem item, String fullHash, String url) {
        return Text.literal("\nItem is generated   ").styled(style -> style.withColor(Formatting.DARK_AQUA))
                .append(Text.literal(item.getName()).styled(style -> style.withColor(item.getTier().format)))
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
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("SAVE").styled(style -> style.withColor(Formatting.GOLD)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Clicking this will open a menu where you can save items allowing you to use it in later builds")))
                        .withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/build saveditems"))))
                .append("\n").styled(style -> style.withBold(true));
    }

    public static Text getBuildTemplate(String url, @Range(from = 0, to = 3) Integer precision) {
        assert precision == null || precision > 3;
        return (precision == null ? Text.literal("\n    ") : Text.literal("\n(").styled(style -> style.withColor(Formatting.DARK_GRAY))
                .append(Text.literal(BuildScreen.PRECISION_OPTIONS.get(precision)).styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true).withUnderline(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Precision Option: ").append(BuildScreen.PRECISION_OPTIONS.get(precision)).append("\n").append(BuildScreen.PRECISION_TOOLTIPS.get(precision)).append("\n\n")
                                        .append(Text.literal("CLICK TO CHANGE PRECISION (/build config)").styled(style1 -> style1.withColor(Formatting.GREEN)))))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/build config"))))
                .append(Text.literal(")").styled(style -> style.withColor(Formatting.DARK_GRAY))))
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

    /**
     * Wraps a method in a try block to catch errors at screen reading
     *
     * @param method
     */
    public static void catchNotLoaded(Runnable method) {
        try {
            method.run();
            failures = 0;
        } catch (Exception e) {
            failures++;
            WynnBuildClient.client.player.sendMessage(Text.literal("Fetching failed! Press the READ button to fetch manually")
                    .styled(style -> style.withColor(Formatting.RED)));
            if (failures < 2) {
                new Task(() -> catchNotLoaded(method), WynnBuildClient.REFETCH_DELAY);
            }
            e.printStackTrace();
        }
    }
}
