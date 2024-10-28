package com.gertoxq.quickbuild.screens.gallery;

import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.gertoxq.quickbuild.config.SavedItemType;
import com.gertoxq.quickbuild.custom.CustomItem;
import com.gertoxq.quickbuild.custom.ID;
import com.gertoxq.quickbuild.custom.TypedID;
import com.gertoxq.quickbuild.screens.BScreen;
import com.gertoxq.quickbuild.screens.itemmenu.SelectableListWidget;
import com.gertoxq.quickbuild.util.WynnData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.*;

public class GalleryScreen extends Screen {
    private final static List<DupeInstance> dupeButtons = new ArrayList<>();
    private static final List<GalleryItem> wynnItems = WynnData.getData().values().stream()
            .map(itemData -> new GalleryItem(itemData.baseItem(), itemData.baseItem().createStack())).toList();
    private static String filterString = "$";
    private static List<GalleryItem> matchItems = new ArrayList<>();
    private static ItemGallery gallery;

    public GalleryScreen() {
        super(Text.literal("Gallery"));
    }

    @Override
    protected void init() {
        super.init();

        TextFieldWidget widget = getTextFieldWidget();

        addDrawableChild(widget);

        gallery = new ItemGallery(205, height, width / 2 - 100, 0, 32, 32, List.of());
        gallery.replaceEntries(filterItems(filterString).stream().map(gallery::create).toList());
        gallery.refreshScroll();
        addDrawableChild(gallery);

    }

    private @NotNull TextFieldWidget getTextFieldWidget() {
        TextFieldWidget widget = new TextFieldWidget(textRenderer, width / 2 - 200, 20, Text.empty());
        widget.setText(filterString);
        widget.setMaxLength(Integer.MAX_VALUE);
        widget.setChangedListener(s -> {
            filterString = s.strip().toLowerCase();
            matchItems = filterItems(filterString);
            gallery.replaceEntries(matchItems.stream().map(gallery::create).toList());
            gallery.refreshScroll();
        });
        return widget;
    }

    public List<GalleryItem> filterItems(String filterString) {
        List<GalleryItem> items = new ArrayList<>(QuickBuildClient.getConfigManager().getConfig().getSavedItems().stream().map(savedItemType -> {
            CustomItem item = CustomItem.getCustomFromHash(savedItemType.getHash());
            return new GalleryItem(item, item.createStack());
        }).toList());
        if (!filterString.startsWith("$")) {
            items.addAll(wynnItems);
        } else {
            filterString = filterString.substring(1);
        }
        if (filterString.isBlank()) {
            return items;
        }

        String[] tokens = filterString.split(" ");
        int tokenStartIndex = -1;
        String lastToken = "";
        final Map<TypedID<?>, List<Filter<?>>> filters = new HashMap<>();
        for (String token : tokens) {
            tokenStartIndex += lastToken.length() + 1;
            lastToken = token;

            if (token.contains(":")) {
                String keyString = token.substring(0, token.indexOf(':'));
                TypedID<?> id = (TypedID<?>) ID.getByNameIgnoreCase(keyString);
                if (id == null) continue;
                String inputString = token.substring(token.indexOf(':') + 1);

                String[] matchers = inputString.split(",");
                for (String matcher : matchers) {
                    Filter<?> filter;
                    if (id.getType() == Integer.class) {
                        @SuppressWarnings("unchecked")
                        Filter<Integer> filterr = new Filter.Inttype((TypedID<Integer>) id, matcher);
                        filter = filterr;
                    } else if (id.getType() == String.class) {
                        @SuppressWarnings("unchecked")
                        Filter<String> filterr = new Filter.Stringtype((TypedID<String>) id, matcher);
                        filter = filterr;
                    } else {
                        continue;
                    }
                    if (filter.isValid()) {
                        List<Filter<?>> prev = filters.getOrDefault(filter.watchedId, new ArrayList<>());
                        prev.add(filter);
                        filters.put(filter.watchedId, prev);
                    }
                }
            } else {
                Filter<String> filter = new Filter.NameFilter(token);
                List<Filter<?>> prev = filters.getOrDefault(filter.watchedId, new ArrayList<>());
                prev.add(filter);
                filters.put(filter.watchedId, prev);
            }
        }

        return items.stream().filter(galleryItem -> {
            List<Boolean> differentIdQuery = new ArrayList<>();
            filters.forEach((checkedId, list) -> {
                boolean sameIdQuery = list.stream().map(idFilter -> idFilter
                                .parseString(galleryItem.customItem))
                        .reduce(false, (aBoolean, aBoolean2) -> aBoolean || aBoolean2);
                differentIdQuery.add(sameIdQuery);
            });
            return differentIdQuery.stream()
                    .reduce(true, (aBoolean, aBoolean2) -> aBoolean && aBoolean2);
        }).toList();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    public static class DupeInstance {

        final ButtonWidget buttonWidget;
        SavedItemType savedItem;
        CustomItem customItem;
        GalleryItem originalItem;
        CustomItem originalCustom;
        boolean visible = false;

        public DupeInstance(ButtonWidget buttonWidget, GalleryItem originalItem, SavedItemType item) {
            this.buttonWidget = buttonWidget;
            this.originalItem = originalItem;
            this.originalCustom = originalItem.customItem();
            setItem(item);
        }

        public static @NotNull Map<ID, Object> getDiff(CustomItem original, CustomItem changed) {
            if (!Objects.equals(original.getBaseItemId(), changed.getBaseItemId())) return new HashMap<>();

            Map<ID, Object> diffs = new HashMap<>();
            original.statMap.forEach((s, o) -> {
                ID key = ID.getByName(s);
                Object changedValue = changed.statMap.getOrDefault(s, o);
                if (!Objects.equals(o, changedValue)) {
                    diffs.put(key, changedValue);
                }
            });
            return diffs;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public void setTooltip(Tooltip tooltip) {
            buttonWidget.setTooltip(tooltip);
        }

        public void setItem(SavedItemType newItem) {
            savedItem = newItem;
            customItem = CustomItem.getCustomFromHash(savedItem.getHash());
            if (customItem == null) return;

            final Map<ID, Object> diffs = getDiff(originalCustom, customItem);
            List<Text> newTt = new ArrayList<>();
            diffs.forEach((id, o) -> {
                if (id.displayName.isEmpty() || id.displayName.startsWith("&")) {
                    return;
                }
                newTt.add(Text.literal(id.displayName.trim()).styled(style -> style.withColor(Formatting.GOLD))
                        .append(Text.literal(": "))
                        .append(Text.literal(originalCustom.get(id).toString()))
                        .append(Text.literal(" -> "))
                        .append(Text.literal(o.toString()))
                );
            });
            setTooltip(Tooltip.of(QuickBuildClient.reduceTextList(newTt)));
        }
    }

    public record GalleryItem(CustomItem customItem, ItemStack itemStack) {
    }

    record Positioner(int screenW, int screenH) implements TooltipPositioner {

        @Override
        public Vector2ic getPosition(int screenWidth, int screenHeight, int x, int y, int width, int height) {
            Vector2i vector2i = new Vector2i(x, y);
            this.preventOverflow(this.screenW, this.screenH, vector2i, width, height);
            return vector2i;
        }

        private void preventOverflow(int screenWidth, int screenHeight, Vector2i pos, int width, int height) {
            if (pos.x + width > screenWidth) {
                pos.x = Math.max(pos.x - width, 4);
            }

            int i = height + 3;
            if (pos.y + i > screenHeight) {
                pos.y = screenHeight - i;
            }
        }
    }

    public class ItemGallery extends GalleryWidget<GalleryItem> {

        public List<SavedItemType> savedVersions = new ArrayList<>();

        public ItemGallery(int width, int height, int x, int y, int itemHeight, int itemWidth, List<GalleryItem> items) {
            super(width, height, x, y, itemHeight, itemWidth, items);
        }

        @Override
        public void setSelected(@Nullable SelectableListWidget<GalleryItem>.Entry entry) {
            super.setSelected(entry);
//            savedVersions = QuickBuildClient.getConfigManager().getConfig().getSavedItems().stream()
//                    .filter(savedItemType -> savedItemType.getBaseItemId() == entry.getValue().customItem().getBaseItemId()).toList();
//
//            for (int i = 0; i < savedVersions.size(); i++) {
//                SavedItemType item = savedVersions.get(i);
//
//                dupeButtons.get(i).setItem(item);
//
//            }
        }

        @Override
        public void dispose() {

        }

        @Override
        protected int getDefaultScrollbarX() {
            return getRight() - 5;
        }


        @Override
        public void renderChild(SelectableListWidget<GalleryItem>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            BScreen.renderItem(context, entry.getValue().itemStack(), x, y, 24, (float) (entryWidth - 16) / 2, (float) (entryHeight - 16) / 2);

            if (getSelectedOrNull() != null) {
                context.drawTooltip(GalleryScreen.this.textRenderer,
                        getSelectedOrNull().getValue().customItem.buildLore(),
                        ItemGallery.this.getX() + width, ItemGallery.this.getY() + 17);
            }

            context.disableScissor();
            if (hovered) {
                List<Text> lore = entry.getValue().customItem.buildLore();
                List<OrderedText> orderedTexts = lore.stream().map(Text::asOrderedText).toList();
                context.drawTooltip(GalleryScreen.this.textRenderer, orderedTexts, new Positioner(
                        ItemGallery.this.getX() + ItemGallery.this.width,
                        ItemGallery.this.getY() + ItemGallery.this.height
                ), mouseX, mouseY);
            }
            context.enableScissor(0, 0, GalleryScreen.this.width, GalleryScreen.this.height);
        }
    }

    public class DupeSelector extends GalleryWidget<DupeInstance> {

        public DupeSelector(int x, int y, List<DupeInstance> items) {
            super(25, GalleryScreen.this.height, x, y, 24, 24, items);
        }

        @Override
        public void dispose() {

        }

        @Override
        public int getRowWidth() {
            return 26;
        }

        @Override
        public void renderChild(SelectableListWidget<DupeInstance>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

        }
    }
}
