package com.gertoxq.wynnbuild.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class DebugContainer {

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
        root.addProperty("container_size", handler.slots.size());
        root.add("items", itemsArray);

        writeToFile(root);
    }

    private static void writeToFile(JsonObject json) {
        try {
            Path configDir = FabricLoader.getInstance().getGameDir();
            Path file = configDir.resolve("debug/inventory_snapshot_" + UUID.randomUUID() + ".json");

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
