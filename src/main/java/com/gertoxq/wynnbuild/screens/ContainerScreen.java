package com.gertoxq.wynnbuild.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

public class ContainerScreen<T extends ContainerScreenHandler> extends GenericContainerScreen {
    public ContainerScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getScreenHandler() {
        return (T) super.getScreenHandler();
    }

    public Button createButton(Clickable.AXISPOS xAxis, Clickable.AXISPOS yAxis, int width, int height, int xOffset, int yOffset, Text message, ButtonWidget.PressAction action, BooleanSupplier condition) {
        return new Button(
                (xAxis == Clickable.AXISPOS.START ? 0 : xAxis == Clickable.AXISPOS.CENTER ? this.width / 2 - width / 2 : this.width - width) + xOffset,
                (yAxis == Clickable.AXISPOS.START ? 0 : yAxis == Clickable.AXISPOS.CENTER ? this.height / 2 - height / 2 : this.height - height) + yOffset,
                width, height, message, action
        ) {
            @Override
            protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                if (condition.getAsBoolean()) super.renderWidget(context, mouseX, mouseY, delta);
            }
        };
    }

    public Button createButton(Clickable.AXISPOS xAxis, Clickable.AXISPOS yAxis, int width, int height, int xOffset, int yOffset, Text message, ButtonWidget.PressAction action) {
        return createButton(xAxis, yAxis, width, height, xOffset, yOffset, message, action, () -> true);
    }
}
