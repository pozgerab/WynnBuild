package com.gertoxq.wynnbuild.screens.components;

import com.gertoxq.wynnbuild.screens.itemmenu.SelectableListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class DropdownScreen extends Screen {

    public DropdownScreen() {
        super(Text.literal("Dropdown Example"));
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 200;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height / 4;

        var dropdownInput = new QueryInput(this, x, y, buttonWidth, buttonHeight, Text.literal("Placeholder"),
                List.of("lorem", "ipsum", "dolor", "sit", "amet"));

        var drop = new Drop(this, x, y + 40, buttonWidth, buttonHeight,
                List.of("lorem", "ipsum", "dolor", "sit", "amet"));

        this.addDrawableChild(dropdownInput);
        this.addDrawableChild(drop);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    public static class QueryInput extends DropdownInput<String> {

        public QueryInput(Screen screen, int x, int y, int width, int height, Text placeholder, List<String> entries) {
            super(screen, x, y, width, height, placeholder, entries, string -> string);
        }

        @Override
        public void renderChild(SelectableListWidget<String>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(MinecraftClient.getInstance().textRenderer, entry.getValue(), x + 4, y + 2, 0xFFFFFF, false);
        }
    }

    public static class Drop extends DropdownButton<String> {

        public Drop(Screen screen, int x, int y, int width, int height, List<String> entries) {
            super(screen, x, y, width, height, string -> Text.literal("Selected: " + (string == null ? "" : string)), entries);
        }

        @Override
        public void renderChild(SelectableListWidget<String>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(MinecraftClient.getInstance().textRenderer, entry.getValue(), x + 4, y + 2, 0xFFFFFF, false);
        }
    }
}
