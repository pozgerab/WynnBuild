package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.WynnData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WynnBuildClient implements ClientModInitializer {
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Clickable BUTTON;
    public static int REFETCH_DELAY = 40;
    public static int ATREE_IDLE; // How many ticks is elapsed before turning page while reading atree
    public static boolean readAtree = false;

    @Override
    public void onInitializeClient() {

        WynnBuild.client = MinecraftClient.getInstance();
        Task.init();
        IDs.load();
        WynnData.loadAll();
        ScreenManager.register();

        WynnBuild.configManager = new Manager();
        WynnBuild.configManager.loadConfig();
        readAtree = !WynnBuild.configManager.getConfig().getAtreeEncoding().isEmpty();

        Ability.refreshTree();

        BUTTON = new Clickable(() -> WynnBuild.configManager.getConfig().isShowButtons());

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen screen1) {
                BUTTON.addTo(screen1, Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> WynnBuild.build());
            }
        });

        CommandRegistry.init(WynnBuild.client);
    }
}
