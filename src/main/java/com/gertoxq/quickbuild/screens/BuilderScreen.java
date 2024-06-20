package com.gertoxq.quickbuild.screens;

import com.gertoxq.quickbuild.util.ScreenClicker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

public class BuilderScreen {
    protected final HandledScreen<?> screen;
    protected final ScreenHandler handler;

    public BuilderScreen(HandledScreen<?> screen) {
        this.screen = screen;
        this.handler = screen.getScreenHandler();
    }

    public Screen getScreen() {
        return screen;
    }

    public ScreenHandler getHandler() {
        return handler;
    }

    public ScreenClicker getClicker() {
        return new ScreenClicker(screen);
    }
}
