package com.gertoxq.quickbuild.screens.itemmenu;

import com.gertoxq.quickbuild.client.QuickBuildClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

import java.util.List;

public abstract class SelectableListWidget<T> extends AlwaysSelectedEntryListWidget<SelectableListWidget<T>.Entry> {

    private final int right;

    public SelectableListWidget(int width, int height, int x, int y, int itemHeight, List<T> items) {
        super(QuickBuildClient.client, width, height, y + height, itemHeight);
        this.setX(x);
        this.right = x + width;
        items.forEach(t -> addEntry(new Entry(t)));
    }

    @Override
    public boolean removeEntryWithoutScrolling(Entry entry) {
        return super.removeEntryWithoutScrolling(entry);
    }

    public Entry addEntryToTop(T entry) {
        var newEntry = new Entry(entry);
        super.addEntryToTop(newEntry);
        return newEntry;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    public abstract void dispose();

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected int getDefaultScrollbarX() {
        return right;
    }

    public abstract void renderChild(Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<SelectableListWidget<T>.Entry> {

        private final T value;

        public Entry(T entry) {
            this.value = entry;
        }

        public T getValue() {
            return value;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.onPressed();
            return true;
        }

        void onPressed() {
            SelectableListWidget.this.setSelected(this);
            setFocused(true);
        }

        @Override
        public Text getNarration() {
            return Text.empty();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            SelectableListWidget.this.renderChild(this, context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        }
    }
}
