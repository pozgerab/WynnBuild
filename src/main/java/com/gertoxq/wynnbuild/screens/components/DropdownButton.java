package com.gertoxq.wynnbuild.screens.components;

import com.gertoxq.wynnbuild.screens.itemmenu.SelectableListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class DropdownButton<T> extends ButtonWidget {

    final protected DropdownListWidget dropdown;
    final protected Screen screen;
    final Function<@Nullable T, Text> textFactory;
    public boolean open = false;

    public DropdownButton(Screen screen, int x, int y, int width, int height, Function<T, Text> textFactory, List<T> entries) {
        super(x, y, width, height, textFactory.apply(null), button -> {
        }, textSupplier -> Text.empty());
        this.onPress = button -> toggle();
        this.dropdown = new DropdownListWidget();
        this.textFactory = textFactory;
        this.screen = screen;
        dropdown.replaceEntries(entries.stream().map(dropdown::create).toList());
    }

    public abstract void renderChild(SelectableListWidget<T>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    protected void updateMessage() {
        this.setMessage(textFactory.apply(dropdown.getSelectedOptional().map(SelectableListWidget.Entry::getValue).orElse(null)));
    }

    private void toggle(boolean open) {
        if (open && !this.open) {
            this.screen.addDrawableChild(dropdown);
            dropdown.setFocused(true);
        } else if (!open && this.open) {
            this.screen.remove(dropdown);
            this.setFocused(true);
        }
    }

    private void toggle() {
        open = !open;
        if (open) {
            screen.addDrawableChild(dropdown);
            dropdown.setFocused(true);
        } else {
            screen.remove(dropdown);
            this.setFocused(true);
        }
    }

    protected class DropdownListWidget extends SelectableListWidget<T> {

        public DropdownListWidget() {
            super(DropdownButton.this.width,
                    DropdownButton.this.height * 5,
                    DropdownButton.this.getX(),
                    DropdownButton.this.getY() + DropdownButton.this.height,
                    DropdownButton.this.height,
                    List.of());
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            if (mouseX > getX() && mouseX < getX() + getWidth()
                    && mouseY > getY() + getHeight() - 4 && mouseY < getY() + getHeight() + 4) {
                setHeight((int) (mouseY - getY()));
            }
            super.onDrag(mouseX, mouseY, deltaX, deltaY);
        }

        @Override
        public void setSelected(@Nullable SelectableListWidget<T>.Entry entry) {
            super.setSelected(entry);
            DropdownButton.this.updateMessage();
            DropdownButton.this.toggle(false);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!(focused || DropdownButton.this.isFocused())) {
                toggle(false);
            }
        }

        @Override
        public void renderChild(SelectableListWidget<T>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            DropdownButton.this.renderChild(entry, context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        }

    }
}
