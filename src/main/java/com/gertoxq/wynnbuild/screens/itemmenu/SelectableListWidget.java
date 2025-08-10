package com.gertoxq.wynnbuild.screens.itemmenu;

import com.gertoxq.wynnbuild.WynnBuild;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class SelectableListWidget<T> extends AlwaysSelectedEntryListWidget<SelectableListWidget<T>.Entry> {

    private final int right;

    public SelectableListWidget(int width, int height, int x, int y, int itemHeight, List<T> items) {
        super(WynnBuild.client, width, height, y, itemHeight);
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

    public void addEntry(T entry) {
        super.addEntry(new Entry(entry));
    }

    @Override
    public int getRowWidth() {
        return width - 5;
    }

    @Override
    protected int getScrollbarX() {
        return right - 5;
    }

    public Optional<Entry> getSelectedOptional() {
        return Optional.ofNullable(getSelectedOrNull());
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.getRowLeft();
        int j = this.getRowRight();
        context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
        context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    @Override
    public void replaceEntries(Collection<Entry> newEntries) {
        super.replaceEntries(newEntries);
    }

    public Entry create(T value) {
        return new Entry(value);
    }

    @Override
    public Entry getEntry(int index) {
        return super.getEntry(index);
    }

    @Override
    public int getEntryCount() {
        return super.getEntryCount();
    }

    @Override
    public boolean isSelectedEntry(int index) {
        return super.isSelectedEntry(index);
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
            if (isMouseOver(mouseX, mouseY)) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x808080);
            }
        }
    }
}
