package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.event.WorldChangeTreeRefresh;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.core.WynntilsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WynnBuildClient implements ClientModInitializer {
    public static Clickable BUTTON;
    public static int REFETCH_DELAY = 40;

    @Override
    public void onInitializeClient() {

        WynnBuild.client = MinecraftClient.getInstance();
        Task.init();
        WynnData.loadAll();
        ScreenManager.register();

        WynnBuild.configManager = new Manager();
        WynnBuild.configManager.loadConfig();

        BUTTON = new Clickable(() -> WynnBuild.configManager.getConfig().isShowButtons());

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen screen1) {
                BUTTON.addTo(screen1, Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> {
                    screen.close();
                    client.execute(WynnBuild::build);
                });
            }
        });

        CommandRegistry.init(WynnBuild.client);

        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                WynntilsMod.registerEventListener(new WorldChangeTreeRefresh()));
    }
}
