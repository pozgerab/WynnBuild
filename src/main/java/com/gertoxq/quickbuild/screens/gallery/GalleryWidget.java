package com.gertoxq.quickbuild.screens.gallery;

import com.gertoxq.quickbuild.screens.itemmenu.SelectableListWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class GalleryWidget<T> extends SelectableListWidget<T> {

    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("widget/scroller");
    private static final Identifier SCROLLER_BACKGROUND_TEXTURE = Identifier.ofVanilla("widget/scroller_background");
    protected final int itemWidth;
    protected final int colCount;
    protected Entry hoveredEntry;
    protected boolean scrolling;

    public GalleryWidget(int width, int height, int x, int y, int itemHeight, int itemWidth, List<T> items) {
        super(width, height, x, y, itemHeight, items);
        this.itemWidth = itemWidth;
        this.colCount = (int) Math.floor((double) (getRowWidth() - 5) / itemWidth); // 5 is scrollbar width
    }

    @Override
    protected @Nullable SelectableListWidget<T>.Entry getHoveredEntry() {
        return hoveredEntry;
    }

    public boolean isMouseOverElement(double mouseX, double mouseY) {
        return mouseY >= (double) this.getY() && mouseY <= (double) this.getBottom() && mouseX >= (double) this.getX() && mouseX <= (double) this.getRight() - 10;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        hoveredEntry = this.isMouseOverElement(mouseX, mouseY) ? this.entryAtPosition(mouseX, mouseY) : null;
        this.drawMenuListBackground(context);
        this.enableScissor(context);
        this.renderList(context, mouseX, mouseY, delta);
        context.disableScissor();
        this.drawHeaderAndFooterSeparators(context);
        if (this.isScrollbarVisible()) {
            int i = this.getScrollbarX();
            int j = (int) ((float) (this.height * this.height) / (float) this.getMaxPosition());
            j = MathHelper.clamp(j, 32, this.height - 8);
            int k = (int) this.getScrollAmount() * (this.height - j) / this.getMaxScroll() + this.getY();
            if (k < this.getY()) {
                k = this.getY();
            }

            RenderSystem.enableBlend();
            context.drawGuiTexture(SCROLLER_BACKGROUND_TEXTURE, i, this.getY(), 6, this.getHeight());
            context.drawGuiTexture(SCROLLER_TEXTURE, i, k, 6, j);
            RenderSystem.disableBlend();
        }

        this.renderDecorations(context, mouseX, mouseY);
        RenderSystem.disableBlend();
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

    public @Nullable Entry entryAtPosition(double x, double y) {
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isSelectButton(button)) {
            return false;
        } else {
            this.updateScrollingState(mouseX, mouseY, button);
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            } else {
                Entry entry = this.entryAtPosition(mouseX, mouseY);
                if (entry != null) {
                    if (entry.mouseClicked(mouseX, mouseY, button)) {
                        Entry entry2 = this.getFocused();
                        if (entry2 != entry && entry2 instanceof ParentElement parentElement) {
                            parentElement.setFocused(null);
                        }

                        this.setFocused(entry);
                        this.setDragging(true);
                        return true;
                    }
                } else if (this.clickedHeader(
                        (int) (mouseX - (double) (this.getX() + this.width / 2 - this.getRowWidth() / 2)), (int) (mouseY - (double) this.getY()) + (int) this.getScrollAmount() - 4
                )) {
                    return true;
                }

                return scrolling;
            }
        }
    }

    @Override
    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        scrolling = button == 0 && mouseX >= (double) this.getScrollbarX() && mouseX < (double) (this.getScrollbarX() + 6);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.getY()) {
                this.setScrollAmount(0.0);
            } else if (mouseY > (double) this.getBottom()) {
                this.setScrollAmount(this.getMaxScroll());
            } else {
                double d = Math.max(1, this.getMaxScroll());
                int i = this.height;
                int j = MathHelper.clamp((int) ((float) (i * i) / (float) this.getMaxPosition()), 32, i - 8);
                double e = Math.max(1.0, d / (double) (i - j));
                this.setScrollAmount(this.getScrollAmount() + deltaY * e);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        Entry entry = this.getEntry(index);
        entry.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
        if (this.isSelectedEntry(index)) {
            int i = this.isFocused() ? -1 : -8355712;
            this.drawSelectionHighlight(context, x, y, entryWidth, entryHeight, i, -16777216);
        }

        entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
    }

    protected void drawSelectionHighlight(DrawContext context, int x, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int xEnd = x + entryWidth;
        context.fill(x, y - 2, xEnd, y + entryHeight + 2, borderColor);
        context.fill(x + 1, y - 1, xEnd - 1, y + entryHeight + 1, fillColor);
    }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        } else {
            return this.hoveredEntry != null ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
        }
    }
}
