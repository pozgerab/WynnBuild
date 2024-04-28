package com.gertoxq.quickbuild.screens;

import com.gertoxq.quickbuild.util.ScreenClicker;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;

public abstract class BuilderScreen {
    protected final GenericContainerScreen screen;
    protected final GenericContainerScreenHandler handler;
    public BuilderScreen(GenericContainerScreen screen) {
        this.screen = screen;
        this.handler = screen.getScreenHandler();
    }
    public GenericContainerScreen getScreen() {
        return screen;
    }

    public GenericContainerScreenHandler getHandler() {
        return handler;
    }
    public ScreenClicker getClicker() {
        return new ScreenClicker(screen);
    }
}
