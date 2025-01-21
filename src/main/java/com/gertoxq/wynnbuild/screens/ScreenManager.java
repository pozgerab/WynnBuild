package com.gertoxq.wynnbuild.screens;

import com.gertoxq.wynnbuild.screens.atree.AtreeScreen;
import com.gertoxq.wynnbuild.screens.charinfo.CharacterInfoScreen;
import com.gertoxq.wynnbuild.screens.tome.TomeScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.client;

public class ScreenManager {

    private static final Map<Pattern, ScreenInstanceCreater<? extends ContainerScreenHandler, ? extends ContainerScreen<?>>> registeredScreens = new HashMap<>();

    private static <H extends ContainerScreenHandler, S extends ContainerScreen<H>> void registerScreen(Pattern titlePattern, ScreenInstanceCreater<H, S> creator) {
        registeredScreens.put(titlePattern, creator);
    }

    public static void register() {
        registerScreen(AtreeScreen.TITLE_PATTERN, AtreeScreen.CREATOR);
        registerScreen(CharacterInfoScreen.TITLE_PATTERN, CharacterInfoScreen.CREATOR);
        registerScreen(TomeScreen.TITLE_PATTERN, TomeScreen.CREATOR);
    }

    public static void changeScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        String titleString = title.getString();
        for (Pattern pattern : registeredScreens.keySet()) {
            if (pattern.matcher(titleString).matches()) {
                client.execute(() -> client.setScreen(registeredScreens.get(pattern).create(handler, inventory, title)));
                return;
            }
        }
    }

    @FunctionalInterface
    public interface ScreenInstanceCreater<H extends ContainerScreenHandler, S extends ContainerScreen<H>> {

        S create(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title);
    }
}
