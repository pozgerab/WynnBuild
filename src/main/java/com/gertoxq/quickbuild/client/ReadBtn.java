package com.gertoxq.quickbuild.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ReadBtn extends ButtonWidget {

    private final Text toolTip;
    public ReadBtn(int x, int y, int width, int height, Text message, PressAction onPress, Text toolTip) {
        super(x, y, width, height, message, onPress, textSupplier -> Text.literal("idk"));
        this.toolTip = toolTip;
    }

}

