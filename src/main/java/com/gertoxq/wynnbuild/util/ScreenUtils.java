package com.gertoxq.wynnbuild.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ScreenUtils {

    public static void renderItem(DrawContext context, ItemStack item, int x, int y, int targetSize, float xOffset, float yOffset) {

        int size = 16;
        float scale = (float) targetSize / 16;

        float centerX = x + (float) size / 2 + xOffset;
        float centerY = y + (float) size / 2 + yOffset;

        context.getMatrices().push();

        context.getMatrices().translate(centerX, centerY, 0);

        context.getMatrices().scale(scale, scale, 1.0f);

        context.getMatrices().translate(-centerX, -centerY, 0);

        context.drawItem(item, (int) (x + xOffset), (int) (y + yOffset));
        context.getMatrices().pop();
    }
}
