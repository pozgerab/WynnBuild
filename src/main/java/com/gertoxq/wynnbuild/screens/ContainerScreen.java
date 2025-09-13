package com.gertoxq.wynnbuild.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

    public abstract class AutoCompleteField<A> extends TextFieldWidget {

        protected final int itemHeight;
        protected final int maxOptionsHeight;
        protected SelectableListWidget<A> options;

        public AutoCompleteField(int x, int y, int width, int height, int itemHeight, Text text, int maxOptionsHeight) {
            super(ContainerScreen.this.textRenderer, x, y, width, height, text);
            this.itemHeight = itemHeight;
            this.maxOptionsHeight = maxOptionsHeight;
            loadPossible(this.getText());
            setChangedListener(this::changedListener);
        }

        public SelectableListWidget<A>.@Nullable Entry getSelectedOrNull() {
            return options.getSelectedOrNull();
        }

        public void changedListener(String input) {
            loadPossible(input);
            options.refreshScroll();
        }

        public abstract List<A> getOptions(String input);

        public abstract String textGetter(A option);

        public void setSelected(@Nullable SelectableListWidget<A>.Entry entry) {
            if (entry != null) setText(textGetter(entry.getValue()));
        }

        protected SelectableListWidget<A> optionsWidget(int width, int height, int x, int y, int itemHeight, List<A> options) {
            return new SelectableListWidget<>(width, height, x, y, itemHeight, options) {

                @Override
                public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    if (AutoCompleteField.this.isFocused()) super.renderWidget(context, mouseX, mouseY, delta);
                }

                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    return AutoCompleteField.this.isFocused() && super.mouseClicked(mouseX, mouseY, button);
                }

                @Override
                public void renderChild(SelectableListWidget<A>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                    AutoCompleteField.this.renderChild(entry, context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
                }

                @Override
                public void setSelected(@Nullable SelectableListWidget<A>.Entry entry) {
                    AutoCompleteField.this.setSelected(entry);
                    super.setSelected(entry);
                }
            };
        }

        public void renderChild(SelectableListWidget<A>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

        }

        public void loadPossible(String inputStr) {
            List<A> possible = getOptions(inputStr);
            int height = Math.min(possible.size() * 15 + 2, maxOptionsHeight);
            if (options != null) {
                options.replaceEntries(possible.stream().map(mutableText -> options.create(mutableText)).toList());
                options.setHeight(height);
                return;
            }
            options = addDrawableChild(optionsWidget(AutoCompleteField.this.width, height, getX(), getY() + getHeight() + 1, itemHeight, possible));
        }
    }
}
