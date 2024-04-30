package com.gertoxq.quickbuild.client;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ReadBtn extends ButtonWidget {

    public ReadBtn(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, textSupplier -> Text.literal("idk"));
    }
    public static void addToRightBottom(Screen screen, int width, int height, Text message, PressAction action) {
        if (QuickBuildClient.getConfigManager().getConfig().isShowButtons()) {
            Screens.getButtons(screen).add(new ReadBtn(screen.width - width, screen.height - height, width, height, message, action));
        }
    }
    public static void addToRightBottom(Screen screen, int width, int height, int xOffset, int yOffset, Text message, PressAction action) {
        if (QuickBuildClient.getConfigManager().getConfig().isShowButtons()) {
            Screens.getButtons(screen).add(new ReadBtn(screen.width - width + xOffset, screen.height - height + yOffset, width, height, message, action));
        }
    }
}

