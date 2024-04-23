package com.gertoxq.quickbuild.client;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ReadBtn extends ButtonWidget {

    public ReadBtn(int x, int y, int width, int height, Text message, PressAction onPress, Text toolTip) {
        super(x, y, width, height, message, onPress, textSupplier -> Text.literal("idk"));
    }

}

