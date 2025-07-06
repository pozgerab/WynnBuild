package com.gertoxq.wynnbuild.screens.components;

import com.gertoxq.wynnbuild.screens.itemmenu.SelectableListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class DropdownInput<T> extends TextFieldWidget {

    final protected DropdownListWidget dropdown;
    final protected Screen screen;
    final List<SelectableListWidget<T>.Entry> entries;
    final Function<@Nullable T, String> queryingField;

    public DropdownInput(Screen screen, int x, int y, int width, int height, Text placeholder, List<T> entries, Function<T, String> queryingField) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, placeholder);
        this.dropdown = new DropdownListWidget();
        this.queryingField = queryingField;
        this.setPlaceholder(placeholder);
        this.screen = screen;
        dropdown.replaceEntries(entries.stream().map(dropdown::create).toList());
        this.entries = List.copyOf(dropdown.children());
        setChangedListener(query -> {
            dropdown.replaceEntries(this.entries.stream().filter(entry -> queryingField.apply(entry.getValue()).contains(query)).toList());
            dropdown.setHeight(Math.min(dropdown.getEntryCount() * this.getHeight(), this.getHeight() * 5));
        });
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            screen.addDrawableChild(dropdown);
        } else if (!dropdown.isFocused()) {
            screen.remove(dropdown);
        }
    }

    public abstract void renderChild(SelectableListWidget<T>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    protected void updateText() {
        this.setText(queryingField.apply(dropdown.getSelectedOptional().map(SelectableListWidget.Entry::getValue).orElse(null)));
    }

    protected class DropdownListWidget extends SelectableListWidget<T> {

        public DropdownListWidget() {
            super(DropdownInput.this.width,
                    DropdownInput.this.height * 5,
                    DropdownInput.this.getX(),
                    DropdownInput.this.getY() + DropdownInput.this.height,
                    DropdownInput.this.height,
                    List.of());
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!(focused || DropdownInput.this.isFocused())) {
                screen.remove(this);
            }
        }

        @Override
        public void setSelected(@Nullable SelectableListWidget<T>.Entry entry) {
            super.setSelected(entry);
            DropdownInput.this.updateText();
        }

        @Override
        public void renderChild(SelectableListWidget<T>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            DropdownInput.this.renderChild(entry, context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        }

    }
}
