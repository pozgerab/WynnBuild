package com.gertoxq.wynnbuild.screens.gallery;

import com.gertoxq.wynnbuild.screens.itemmenu.SelectableListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GalleryWidget<T> extends SelectableListWidget<T> {

    protected final int itemWidth;
    protected final int colCount;

    public GalleryWidget(int width, int height, int x, int y, int itemHeight, int itemWidth, List<T> items) {
        super(width, height, x, y, itemHeight, items);
        this.itemWidth = itemWidth;
        this.colCount = (int) Math.floor((double) (getRowWidth() - 5) / itemWidth); // 5 is scrollbar width
    }

    @Override
    protected int getMaxPosition() {
        return Math.ceilDiv(this.getEntryCount(), colCount) * this.itemHeight + this.headerHeight + 10;
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int rowLeft = this.getRowLeft();
        for (int i = 0; i < getEntryCount(); i++) {
            int currRow = Math.floorDiv(i, colCount);
            int currCol = i % colCount;
            int n = this.getRowTop(currRow);
            int o = this.getRowBottom(currRow);

            if (o >= this.getY() && n <= this.getBottom()) {
                double x = rowLeft + currCol * itemWidth + currCol;
                this.renderEntry(context, mouseX, mouseY, delta,
                        i,
                        (int) x,
                        n, itemWidth, itemHeight);
            }
        }
    }


    @Override
    protected @Nullable SelectableListWidget<T>.Entry getEntryAtPosition(double x, double y) {
        int left = getX();
        int right = getRight();
        if (x > right - 10) return null;
        double yDistFromTop = y - (double) this.getY();
        double xDistFromLeft = x - (double) left;
        int yOffset = MathHelper.floor(yDistFromTop - this.headerHeight + (int) this.getScrollAmount() - 4);
        int xOffset = MathHelper.floor(xDistFromLeft);
        int entryIndex = yOffset / this.itemHeight * colCount + xOffset / this.itemWidth;
        return (x >= (double) left && x <= (double) right && entryIndex >= 0 && yOffset >= 0 && entryIndex < this.getEntryCount() ? this.children().get(entryIndex) : null);
    }

    @Override
    protected void centerScrollOn(SelectableListWidget<T>.Entry entry) {
        int index = this.children().indexOf(entry);
        this.setScrollAmount(index / colCount * this.itemHeight + this.itemHeight / 2 - this.height / 2);
    }
}
