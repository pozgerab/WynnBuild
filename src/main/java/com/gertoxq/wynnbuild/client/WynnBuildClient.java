package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.event.AbilityNodeChange;
import com.gertoxq.wynnbuild.event.AtreeReset;
import com.gertoxq.wynnbuild.event.ScreenClosed;
import com.gertoxq.wynnbuild.event.WorldChangeTreeRefresh;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.util.DebugContainer;
import com.gertoxq.wynnbuild.util.WynnData;
import com.gertoxq.wynnbuild.webquery.BuilderDataManager;
import com.wynntils.core.WynntilsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class WynnBuildClient implements ClientModInitializer {
    public static Clickable BUTTON;
    public static KeyBinding SAVE_ITEM_JSON_KEYBIND;
    private static final KeyBinding.Category DEBUG = new KeyBinding.Category(Identifier.of("wynnbuild", "debug"));

    @Override
    public void onInitializeClient() {

        WynnBuild.client = MinecraftClient.getInstance();
        WynnData.loadAll();

        WynnBuild.configManager = new Manager();
        WynnBuild.getConfigManager().loadConfig();

        BuilderDataManager.initBuilderData();

        BUTTON = new Clickable(() -> WynnBuild.getConfig().isShowButtons());

        SAVE_ITEM_JSON_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wynnbuild.save_item_json",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                DEBUG
        ));

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen screen1) {
                BUTTON.addTo(screen1, Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> {
                    screen.close();
                    client.execute(WynnBuild::build);
                });
            }
            if (screen instanceof GenericContainerScreen screen1)
                BUTTON.addTo(screen1, Clickable.AXISPOS.START, Clickable.AXISPOS.END, 100, 20, Text.literal("read"), button -> {
                    DebugContainer.snapshotContainer(screen1);
                });
        });

        CommandRegistry.init(WynnBuild.client);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            WynntilsMod.registerEventListener(new WorldChangeTreeRefresh());
            WynntilsMod.registerEventListener(new ScreenClosed());
            WynntilsMod.registerEventListener(new AbilityNodeChange());
            WynntilsMod.registerEventListener(new AtreeReset());
            //WynntilsMod.registerEventListener(new DebugMenuOpen());

            checkNewVersion();
        });
    }

    private void checkNewVersion() {
        String currentVersion = FabricLoader.getInstance().getModContainer(WynnBuild.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        if (!WynnBuild.getConfigManager().getConfig().getLatestVersion().equals(currentVersion)) {

            WynnBuild.info("New wynnbuilder version detected, reloading data and cache just in case.");
            BuilderDataManager.reloadBuilderData(true);

            WynnBuild.getConfigManager().getConfig().setLatestVersion(currentVersion);
            WynnBuild.getConfigManager().saveConfig();
        }
    }
}
