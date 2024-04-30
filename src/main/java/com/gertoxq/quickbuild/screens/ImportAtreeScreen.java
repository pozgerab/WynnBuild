package com.gertoxq.quickbuild.screens;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ImportAtreeScreen extends Screen {
    private final Screen parent;
    public ImportAtreeScreen(Screen parent) {
        super(Text.literal("Import atree"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        var input = new TextFieldWidget(textRenderer, width/2-100,height/4+24, 200, 20, Text.empty());
        input.setPlaceholder(Text.literal("Input your wynnbuilder link"));
        addDrawableChild(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Import Ability Tree"), width/2, height/4, 0xffffff);
        var text = MultilineText.create(textRenderer, Text.literal("This is an upcoming feature. This lets you apply ability trees automatically from a wynnbuilder link"), 200);
        text.drawCenterWithShadow(context, width/2, height/4+60);
        super.render(context, mouseX, mouseY, delta);
    }
}
