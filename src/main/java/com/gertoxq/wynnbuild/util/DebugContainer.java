package com.gertoxq.wynnbuild.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.wynntils.utils.mc.LoreUtils;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.ItemUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugContainer {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void snapshotContainer(GenericContainerScreen screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        var handler = screen.getScreenHandler();
        var registryOps = client.getNetworkHandler()
                .getRegistryManager()
                .getOps(JsonOps.INSTANCE);

        JsonArray itemsArray = new JsonArray();

        for (int i = 0; i < handler.slots.size(); i++) {
            ItemStack stack = handler.slots.get(i).getStack();

            if (stack.isEmpty()) continue;

            JsonObject slotObject = new JsonObject();
            slotObject.addProperty("slot", i);

            var encoded = ItemStack.CODEC
                    .encodeStart(registryOps, stack)
                    .getOrThrow();

            slotObject.add("item", encoded);

            itemsArray.add(slotObject);
        }

        JsonObject root = new JsonObject();
        JsonArray titleArray = new JsonArray();
        screen.getTitle().getString().codePoints().forEach(cp -> {
            titleArray.add(String.format("U+%04X", cp));
        });
        root.addProperty("titleString", screen.getTitle().getString());
        root.add("title", titleArray);
        root.addProperty("container_size", handler.slots.size());
        root.add("items", itemsArray);

        writeToFile(root, "inv_snapshot");
    }

    public static void snapshotItem() {
        if (McUtils.screen() == null) return;
        if (!(McUtils.screen() instanceof HandledScreen<?> screen) || screen.focusedSlot == null) return;

        ItemStack stack = screen.focusedSlot.getStack();

        var registryOps = McUtils.mc().getNetworkHandler()
                .getRegistryManager()
                .getOps(JsonOps.INSTANCE);

        JsonObject itemObj = new JsonObject();
        var encoded = ItemStack.CODEC
                .encodeStart(registryOps, screen.focusedSlot.getStack())
                .getOrThrow();

        JsonArray loreArray = new JsonArray();
        LoreUtils.getLore(stack).forEach(styledTextParts -> loreArray.add(Utils.escapeToUnicode(styledTextParts.getString())));

        itemObj.addProperty("name", Utils.escapeToUnicode(ItemUtils.getItemName(stack).getString()));
        itemObj.add("lore", loreArray);
        itemObj.add("json", encoded);


        writeToFile(itemObj, stack.getItemName().getString());

    }

    private static void writeToFile(JsonObject json, String name) {
        try {
            Path configDir = FabricLoader.getInstance().getGameDir();
            Path file = configDir.resolve("debug/" + name + "_" + formatter.format(new Date()) + ".json");

            Files.writeString(
                    file,
                    new GsonBuilder().setPrettyPrinting().create().toJson(json),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
