package com.gertoxq.wynnbuild.screens.builder;

import com.gertoxq.wynnbuild.Cast;
import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.config.SavedBuildType;
import com.gertoxq.wynnbuild.config.SavedItemType;
import com.gertoxq.wynnbuild.custom.AllIDs;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.custom.ID;
import com.gertoxq.wynnbuild.screens.Button;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.itemmenu.SelectableListWidget;
import com.gertoxq.wynnbuild.util.Task;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;

public class BuildScreen extends Screen {
    public static final List<String> PRECISION_OPTIONS = List.of("OFF", "NEVER", "ON", "FORCE");
    public static final Map<Integer, Text> PRECISION_TOOLTIPS = Map.of(
            0, Text.literal("The item is passed to the builder as a default item (meaning an item with average rolls)"),
            1, Text.literal("The item is always passed as a default item unless it's a crafted or custom (average rolls always)"),
            2, Text.literal("The item is passed as custom item if the item is saved (not the currently used equipment) (the stats are precisely passed)"),
            3, Text.literal("The item is always passed as a custom item (uses your exact rolls, most precision)"));
    public static final String precisionTooltip = "\n" + PRECISION_OPTIONS.get(0) + " - " + PRECISION_TOOLTIPS.get(0).getString() + "\n" +
            "\n" + PRECISION_OPTIONS.get(1) + " - " + PRECISION_TOOLTIPS.get(1).getString() + "\n" +
            "\n" + PRECISION_OPTIONS.get(2) + " - " + PRECISION_TOOLTIPS.get(2).getString() + "\n" +
            "\n" + PRECISION_OPTIONS.get(3) + " - " + PRECISION_TOOLTIPS.get(3).getString();
    private static AtomicReference<SelectableListWidget<?>> currentSelect = new AtomicReference<>();
    private static List<String> buildIds = new ArrayList<>(Collections.nCopies(10, ""));
    private static List<String> buildNames;
    private static List<TextWidget> buildDisplays;
    private static List<String> buildHashes;
    private static List<Integer> preciseOptions;
    private static int universalPrecision = WynnBuildClient.getConfigManager().getConfig().getPrecision();
    private static boolean emptySafe = true;
    private static boolean loaded = false;
    private List<CustomItem> hotbarWeapons = new ArrayList<>();

    public BuildScreen() {
        super(Text.literal("Builder"));
    }

    private static void initIDs() {
        saveArmor();
        if (ids.size() == 9) {
            for (int i = 0; i < 9; i++) {
                if (ids.get(i) == -2) {
                    buildIds.set(i, craftedHashes.get(i));
                } else if (ids.get(i) != -1) {
                    buildIds.set(i, String.valueOf(ids.get(i)));
                }
            }
        }
        buildIds.set(9, "");   // atree
    }

    private void initState() {
        buildNames = new ArrayList<>(Collections.nCopies(10, ""));     //  10th is atree
        buildDisplays = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            buildDisplays.add(addDrawableChild(new TextWidget(
                    i != 8 ? (int) (Math.floor((double) i / 4) * 100 + 40) - 30 : 40 - 30,
                    i != 8 ? 50 * (i % 4) + 20 : 270,
                    100, 10, Text.empty(), textRenderer
            )));
        }
        buildDisplays.add(addDrawableChild(new TextWidget(
                110, 270, 100, 10, Text.empty(), textRenderer
        )));
        buildHashes = new ArrayList<>(Collections.nCopies(10, null));        //  10th is atree
        preciseOptions = new ArrayList<>(Collections.nCopies(9, WynnBuildClient.getConfigManager().getConfig().getPrecision())); // DEFAULT NEVER
        buildIds = new ArrayList<>(Collections.nCopies(10, ""));
        initIDs();
        currentSelect = new AtomicReference<>();
    }

    @Override
    public void init() {
        super.init();
        clearChildren();
        saveArmor();
        if (!loaded) initState();
        loaded = true;
        buildDisplays.forEach(this::addDrawableChild);
        hotbarWeapons = client.player.getInventory().main.subList(0, 9).stream().map(CustomItem::getItem).filter(Objects::nonNull).toList();

        List<ID.ItemType> armorTypes = List.of(ID.ItemType.Helmet, ID.ItemType.Chestplate, ID.ItemType.Leggings, ID.ItemType.Boots);
        for (int i = 0; i < 4; i++) {
            final ID.ItemType armorType = armorTypes.get(i);
            final int x = 40;
            final int y = 40 * i + 10 * i + 20;
            ClickableIcon icon = createTypeSelection(x, y, armorType, Identifier.of("wynnbuild", "iron_" + armorType.name().toLowerCase() + ".png"), i);
            addDrawableChild(icon);
        }

        List<ID.ItemType> accTypes = List.of(ID.ItemType.Ring, ID.ItemType.Ring, ID.ItemType.Bracelet, ID.ItemType.Necklace);
        for (int j = 0; j < 4; j++) {
            final ID.ItemType accType = accTypes.get(j);
            final int x = 140;
            final int y = 50 * j + 20;
            final int key = j + 4;
            ClickableIcon icon = createTypeSelection(x, y, accType, Identifier.of("wynnbuild", accType.name().toLowerCase() + ".png"), key);
            addDrawableChild(icon);
        }

        UI.addTo(this, Clickable.AXISPOS.END, Clickable.AXISPOS.END, 120, 20, Text.literal("CLEAR BUILD"), button -> {
            clearAndInit();
            initState();
        });
        addDrawableChild(ButtonWidget.builder(Text.literal("All Precise: ").append(PRECISION_OPTIONS.get(universalPrecision)), button -> {
            int option = (universalPrecision + 1) % PRECISION_OPTIONS.size();
            universalPrecision = option;
            preciseOptions = Collections.nCopies(9, option);
            button.setMessage(Text.literal("All Precise: ").append(PRECISION_OPTIONS.get(option)));
        }).size(120, 20).position(width - 120, height - 40).tooltip(Tooltip.of(Text.literal(
                """
                        CLICK TO APPLY TO ALL SLOTS
                        """ + precisionTooltip
        ))).build());

        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("ON"), Text.literal("OFF"))
                .tooltip(value -> Tooltip.of(Text.literal("Whether to use your currently equipped " +
                        "equipment when building if you haven't set an item for the specified slot. Setting this to false excludes skill points from the build")))
                .build(width - 120, height - 60, 120, 20, Text.literal("Empty Safe"), (button, value) -> {
                    emptySafe = value;
                }));

        List<ID.ItemType> weaponTypes = List.of(ID.ItemType.Spear, ID.ItemType.Bow, ID.ItemType.Dagger, ID.ItemType.Wand, ID.ItemType.Relik);
        ClickableIcon weaponIcon = createTypeSelection(40, 230, weaponTypes, Identifier.of("minecraft", "textures/item/iron_sword.png"), 8, cast.weapon);
        addDrawableChild(weaponIcon);

        ClickableIcon atreeIcon = new ClickableIcon(140, 230, 40, 40, Identifier.of("wynnbuild", "atreeicon.png"), clickableIcon -> {
            Cast currentCast = null;
            if (buildHashes.get(8) == null) {
                if (currentSelect.get() != null) currentSelect.get().dispose();
                return;
            }
            try {
                currentCast = CustomItem.getCustomFromHash(buildHashes.get(8)).getType().getCast();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (currentCast == null) {
                if (currentSelect.get() != null) currentSelect.get().dispose();
                return;
            }
            final var select = createAtreeSelect(currentCast, 120, 200, width / 2 + 40, 20);
            if (Objects.equals(cast, currentCast)) {
                var currentlyEq = select.addEntryToTop(new SavedBuildType("CURRENT", getConfigManager().getConfig().getAtreeEncoding(), cast));
                final var currentEntry = select.children().stream().filter(entry -> entry.getValue().getValue().equals(getConfigManager().getConfig().getAtreeEncoding())).findAny().orElse(null);
                if (currentEntry != null) {
                    var selectedEntry = select.addEntryToTop(currentEntry.getValue());
                    select.removeEntryWithoutScrolling(currentEntry);
                    select.setSelected(selectedEntry);
                } else select.setSelected(currentlyEq);
            }
            renderAndSetCurrentSelect(select);
        });
        addDrawableChild(atreeIcon);

        addDrawableChild(new Button(60, 290, 100, 20, Text.literal("BUILD").styled(style -> style.withColor(Formatting.GREEN)), button -> {
            List<String> finalIds = new ArrayList<>(Collections.nCopies(9, ""));
            try {
                for (int i = 0; i < 9; i++) {
                    final CustomItem item = CustomItem.getCustomFromHash(buildHashes.get(i));
                    final int preciseCode = preciseOptions.get(i);
                    if (buildIds.get(i).isEmpty() || item == null) {
                        if (i == 8) {
                            var err = addDrawableChild(new TextWidget(20, height - 40, 200, 20, Text.literal("You need to provide a weapon").styled(style -> style.withColor(Formatting.RED)), textRenderer));
                            new Task(() -> remove(err), 100);
                            return;
                        } else {
                            if (emptySafe) {
                                if (preciseCode == 3) {
                                    CustomItem customItem = CustomItem.getItem(items.get(i), types.size() > i ? types.get(i) : null);
                                    if (customItem != null) {
                                        finalIds.set(i, customItem.encodeCustom(true));
                                        continue;
                                    }
                                }
                                finalIds.set(i, String.valueOf(ids.get(i)));
                                continue;
                            }
                            finalIds.set(i, "-1"); //   INDICATES EMPTY
                        }
                        continue;
                    }
                    if (preciseCode == 0) {
                        if (buildIds.get(i).isEmpty()) {
                            finalIds.set(i, String.valueOf(ids.get(i)));
                        } else finalIds.set(i, buildIds.get(i));
                    } else if (preciseCode == 1) {
                        if (item.getBaseItemId() == null) {
                            finalIds.set(i, emptyEquipmentPrefix.get(i));
                        } else finalIds.set(i, String.valueOf(item.getBaseItemId()));
                    } else if (preciseCode == 2) {
                        if (buildIds.get(i).isEmpty()) {
                            finalIds.set(i, String.valueOf(ids.get(i)));
                        } else finalIds.set(i, buildIds.get(i));
                    } else if (preciseCode == 3) {
                        finalIds.set(i, item.encodeCustom(true));
                    }
                }
                WynnBuildClient.buildWithArgs(finalIds, buildIds.get(9), emptySafe, null);
            } catch (Exception e) {
                client.player.sendMessage(Text.literal("Something went wrong when trying to build"), false);
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
                e.printStackTrace();
            }
        }));
    }

    private ClickableIcon createTypeSelection(final int x, final int y, final ID.ItemType type, final Identifier identifier, final int key) {
        return createTypeSelection(x, y, List.of(type), identifier, key, type);
    }

    private ClickableIcon createTypeSelection(final int x, final int y, final List<ID.ItemType> types, final Identifier identifier, final int key, ID.ItemType currentType) {
        return new ClickableIcon(x, y, 40, 40, identifier, clickableIcon -> {
            final var select = createTypeSelect(types, 120, 200, width / 2 + 40, 20, key);
            final CustomItem item = CustomItem.getItem(items.get(key), currentType);
            if (item != null && types.contains(item.getType())) {
                var currentlyEq = select.addEntryToTop(new Custom(new SavedItemType("CURRENT", currentType, item.encodeCustom(true), ids.get(key)), item, true));
                final var currentEntry = select.children().stream().filter(entry -> entry.getValue().saved.getHash().equals(buildHashes.get(key))).findAny().orElse(null);
                if (currentEntry != null) {
                    var selectedEntry = select.addEntryToTop(currentEntry.getValue());
                    select.removeEntryWithoutScrolling(currentEntry);
                    select.setSelected(selectedEntry);
                } else select.setSelected(currentlyEq);
            }
            renderAndSetCurrentSelect(select);
        });
    }


    private void renderAndSetCurrentSelect(SelectableListWidget<?> select) {
        if (currentSelect.get() != null) currentSelect.get().dispose();
        currentSelect.set(addDrawableChild(select));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    private ItemSelect createTypeSelect(ID.ItemType type, int width, int height, int x, int y, int key) {
        return createTypeSelect(List.of(type), width, height, x, y, key);
    }

    private AtreeSelect createAtreeSelect(Cast cast, int width, int height, int x, int y) {
        return new AtreeSelect(width, height, x, y, 30,
                getConfigManager().getConfig().getSavedAtrees().stream().filter(savedBuildType -> savedBuildType.getCast().equals(cast)).toList()
        );
    }

    private ItemSelect createTypeSelect(List<ID.ItemType> types, int width, int height, int x, int y, int key) {
        return new ItemSelect(
                width,
                height, x, y, 30,
                getConfigManager().getConfig().getSavedItems().stream()
                        .filter(savedItemType -> types.contains(savedItemType.getType()))
                        .map(savedItemType -> new Custom(savedItemType, CustomItem.getCustomFromHash(savedItemType.getHash()), false))
                        .toList(),
                key
        );
    }

    public record Custom(SavedItemType saved, CustomItem item, boolean current) {
    }

    public static class ClickableIcon extends ClickableWidget {

        public final Identifier texture;
        public final int textureWidth;
        public final int textureHeight;
        Consumer<ClickableIcon> onClick;

        public ClickableIcon(int x, int y, int width, int height, Identifier texture, Consumer<ClickableIcon> onClick) {
            super(x, y, width, height, Text.empty());
            this.texture = texture;
            this.textureHeight = height;
            this.textureWidth = width;
            this.onClick = onClick;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), Formatting.WHITE.getColorValue());
            context.drawTexture(
                    RenderLayer::getGuiTextured,
                    this.texture,
                    this.getX(),
                    this.getY(),
                    0.0F,
                    0.0F,
                    this.getWidth(),
                    this.getHeight(),
                    this.getWidth(),
                    this.getHeight(),
                    this.textureWidth,
                    this.textureHeight
            );
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onClick.accept(this);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }

    public class ItemSelect extends SelectableListWidget<Custom> {

        private final int key;
        private final ButtonWidget selectBtn;
        private final ButtonWidget currentBtn;
        private final ButtonWidget preciseBtn;

        public ItemSelect(int width, int height, int x, int y, int itemHeight, List<Custom> listContent, int key) {
            super(width, height, x, y, itemHeight, listContent);
            this.key = key;
            selectBtn = addDrawableChild(new Button(x, y + 200, 60, 20, Text.literal("Select"), button -> {
                if (getSelectedOrNull() == null) {
                    return;
                }
                var before = getSelectedOrNull();
                if (getSelectedOrNull().getValue().current) {
                    if (ids.get(this.key) == -2) {
                        try {
                            buildIds.set(this.key, craftedHashes.get(this.key));
                            buildNames.set(this.key, CustomItem.getCustomFromHash(craftedHashes.get(this.key)).getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ids.get(this.key) != -1) {
                        buildIds.set(this.key, String.valueOf(ids.get(this.key)));
                        buildNames.set(this.key, eqipmentNames.get(this.key));
                    }
                } else {
                    buildIds.set(this.key, getSelectedOrNull().getValue().saved.getHash());
                    buildNames.set(this.key, getSelectedOrNull().getValue().item.getName());
                }
                buildHashes.set(this.key, getSelectedOrNull().getValue().saved.getHash());
                ID.Tier tier = getSelectedOrNull().getValue().item.getTier();

                buildDisplays.get(this.key).setMessage(Text.literal(buildNames.get(this.key)).styled(style -> style.withColor(tier.format)));
                if (this.key == 8) { // IF WEAPON TYPE CHANGES RESET ATREE VALUE
                    if (!Objects.equals(CustomItem.getCustomFromHash(buildHashes.get(this.key)).getType(), before.getValue().item.getType())) {
                        buildDisplays.get(9).setMessage(Text.empty());
                        buildNames.set(9, "");
                        buildIds.set(9, "");
                        buildHashes.set(9, "");
                    }
                }
            }));

            currentBtn = addDrawableChild(ButtonWidget.builder(Text.literal("Current"), button -> {
                        this.children().stream().filter(entry -> entry.getValue().current).findAny().ifPresent(this::setSelected);
                    })
                    .size(60, 20).position(x + 60, y + 200)
                    .tooltip(Tooltip.of(Text.literal("Click to highlight currently equipped")))
                    .build());

            preciseBtn = addDrawableChild(ButtonWidget.builder(Text.literal("Precise: ").append(PRECISION_OPTIONS.get(preciseOptions.get(this.key))), button -> {
                int option = (preciseOptions.get(this.key) + 1) % PRECISION_OPTIONS.size();
                preciseOptions.set(this.key, option);
                button.setMessage(Text.literal("Precise: ").append(PRECISION_OPTIONS.get(option)));
            }).size(120, 20).position(x, y + 220).tooltip(Tooltip.of(Text.literal(
                    precisionTooltip
            ))).build());

        }

        @Override
        public void dispose() {
            BuildScreen.this.remove(this);
            BuildScreen.this.remove(selectBtn);
            BuildScreen.this.remove(currentBtn);
            BuildScreen.this.remove(preciseBtn);
        }

        @Override
        public void renderChild(SelectableListWidget<Custom>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            CustomItem custom = entry.getValue().item;
            SavedItemType item = entry.getValue().saved;
            if (item.getHash().equals(buildHashes.get(this.key)))
                context.fill(x, y, x + entryWidth, y + entryHeight, Formatting.GREEN.getColorValue());
            ID.Tier tier = custom.get(AllIDs.TIER);
            context.drawTextWithShadow(textRenderer, Text.literal(custom.get(AllIDs.NAME)).styled(style -> style.withColor(tier.format)), x + 2, y + 5, Formatting.WHITE.getColorValue());
            context.drawTextWithShadow(textRenderer, Text.literal(item.getName() + ": ").styled(style -> style.withColor(Formatting.WHITE)).append(Text.literal(item.getType().name()).styled(style -> style.withColor(tier.format))), x + 2, y + 15, Formatting.WHITE.getColorValue());
            if (getSelectedOrNull() != null)
                context.drawTooltip(textRenderer, getSelectedOrNull().getValue().item.buildLore(), x + width, ItemSelect.this.getY() + 17);
        }
    }

    public class AtreeSelect extends SelectableListWidget<SavedBuildType> {

        private final Button selectBtn;
        private final int key = 9;

        public AtreeSelect(int width, int height, int x, int y, int itemHeight, List<SavedBuildType> items) {
            super(width, height, x, y, itemHeight, items);
            selectBtn = addDrawableChild(new Button(x, y + 220, 120, 20, Text.literal("Select"), button -> {
                if (getSelectedOrNull() == null) return;
                buildIds.set(key, getSelectedOrNull().getValue().getValue());
                buildNames.set(key, getSelectedOrNull().getValue().getName());
                buildDisplays.get(this.key).setMessage(Text.literal(buildNames.get(this.key)));
            }));
        }

        @Override
        public void dispose() {
            BuildScreen.this.remove(this);
            BuildScreen.this.remove(selectBtn);
        }

        @Override
        public void renderChild(SelectableListWidget<SavedBuildType>.Entry entry, DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(entry.getValue().getName()), x + entryWidth / 2, y + 5, Formatting.WHITE.getColorValue());
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(entry.getValue().getValue()), x + entryWidth / 2, y + 15, Formatting.WHITE.getColorValue());
        }
    }

}
