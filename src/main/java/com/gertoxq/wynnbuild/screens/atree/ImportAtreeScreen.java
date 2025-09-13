package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.atreeimport.ImportAtree;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.config.SavedBuild;
import com.gertoxq.wynnbuild.screens.SelectableListWidget;
import com.wynntils.core.components.Models;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ImportAtreeScreen extends Screen {
    @Nullable String nameVal;
    @Nullable String codeVal;

    public ImportAtreeScreen(Screen parent) {
        this(parent, null, null);
    }

    public ImportAtreeScreen(Screen parent, @Nullable String nameVal, @Nullable String codeVal) {
        super(Text.literal("Import atree"));
        this.nameVal = nameVal;
        this.codeVal = codeVal;
    }

    @Override
    protected void init() {
        super.init();

        AtreeList atreeList = addDrawableChild(new AtreeList(width / 2 - 100, height / 2 - 50));

        var nameInput = new TextFieldWidget(textRenderer, width / 2 - 100, height / 4 + 24, 100, 20, Text.empty());
        nameInput.setPlaceholder(Text.literal("Name"));
        if (nameVal != null) nameInput.setText(nameVal);

        var codeInput = new TextFieldWidget(textRenderer, width / 2 - 39, height / 4 + 24, 100, 20, Text.empty());
        codeInput.setPlaceholder(Text.literal("tree code"));
        if (codeVal != null) codeInput.setText(codeVal);

        GridWidget grid = new GridWidget().setSpacing(5);
        grid.getMainPositioner().alignHorizontalCenter();
        var adder = grid.createAdder(6);

        adder.add(nameInput, 3);
        adder.add(codeInput, 3);

        adder.add(ButtonWidget.builder(Text.literal("Save Build"), button -> {
            var code = codeInput.getText();
            if (code.isEmpty()) return;
            if (nameInput.getText().isEmpty()) return;
            AtreeCoder coder = WynnBuild.getAtreeCoder();
            var recoded = coder.encode_atree(coder.decode_atree(code)).toB64();
            if (!Objects.equals(recoded, code)) {
                WynnBuild.displayErr("Invalid code");
                return;
            }
            ImportAtree.addBuild(nameInput.getText(), code);
            atreeList.refresh();
            nameInput.setText("");
            codeInput.setText("");
        }).width(70).build(), 2);

        adder.add(ButtonWidget.builder(Text.literal("Copy Code"), button -> {
            if (atreeList.getSelectedOrNull() != null) {
                client.keyboard.setClipboard(atreeList.getSelectedOrNull().getValue().getCode());
                button.setMessage(Text.literal("Copied"));
            }
        }).width(70).build(), 2);

        adder.add(ButtonWidget.builder(Text.literal("DEL").styled(style -> style.withColor(Formatting.RED).withBold(true)),
                button -> {
                    WynnBuild.getConfigManager().getConfig().getSavedAtrees().remove(atreeList.getSelectedOptional().map(SelectableListWidget.Entry::getValue).orElse(null));
                    WynnBuild.getConfigManager().saveConfig();
                    atreeList.refresh();
                }).width(70).build(), 2);

        grid.refreshPositions();
        grid.setPosition(width / 2 - grid.getWidth() / 2, height / 2 + 60);
        grid.forEachChild(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Import Ability Tree"), width / 2, height / 4, 0xffffff);
        super.render(context, mouseX, mouseY, delta);
    }

    private class AtreeList extends SelectableListWidget<SavedBuild> {
        public AtreeList(int x, int y) {
            super(200, 100, x, y, 28, WynnBuild.getConfigManager().getConfig().getSavedAtrees());
        }

        public void refresh() {
            replaceEntries(WynnBuild.getConfigManager().getConfig().getSavedAtrees().stream().map(this::create).toList());
        }

        @Override
        public void renderChild(SelectableListWidget<SavedBuild>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawTextWithShadow(textRenderer, Text.literal(entry.getValue().getName())
                            .append(" ")
                            .append(Text.literal(entry.getValue().getCast().name()).styled(style -> style.withColor(entry.getValue().getCast() == Models.Character.getClassType() ? Formatting.GREEN : Formatting.WHITE))),
                    x + 3, y + 3, 0xffffff);
            context.drawTextWithShadow(textRenderer,
                    Text.literal(entry.getValue().getCode()),
                    x + 3, y + 13, Formatting.GRAY.getColorValue());
        }
    }
}
