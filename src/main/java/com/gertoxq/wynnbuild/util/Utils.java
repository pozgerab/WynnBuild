package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.custom.CustomItem;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
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

    public static Text getBuildTemplate(String url) {
        return Text.literal("\n    Your build is generated   ").styled(style -> style.withColor(Formatting.GOLD))
                .append(Text.literal("COPY").styled(style -> style.withColor(Formatting.GREEN)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(url)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url))
                        .withUnderline(true)))
                .append("  ")
                .append(Text.literal("OPEN").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .withUnderline(true)
                        .withColor(Formatting.RED)))
                .append("\n").styled(style -> style.withBold(true));
    }
}
