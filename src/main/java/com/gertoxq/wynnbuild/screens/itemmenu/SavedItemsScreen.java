package com.gertoxq.wynnbuild.screens.itemmenu;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.custom.CustomUtil;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.config.SavedItem;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.screens.Button;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.WynnData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.gertoxq.wynnbuild.WynnBuild.getConfigManager;

public class SavedItemsScreen extends Screen {
    private static TextWidget info;
    private final Screen parent;
    private final String defHash;

    public SavedItemsScreen(Screen parent) {
        this(parent, "");
    }

    public SavedItemsScreen(Screen parent, String defHash) {
        super(Text.literal("Saved Items"));
        this.parent = parent;
        this.defHash = defHash;
    }

    public static String getNotUsedName(String name) {
        if (getConfigManager().getConfig().getSavedItems().stream().noneMatch(si -> Objects.equals(si.getName(), name))) {
            return name;
        }
        int no = 0;
        AtomicReference<String> newName = new AtomicReference<>(name);
        do {
            var unversioned = Arrays.stream(name.split("#")).toList();
            try {
                no = Integer.parseInt(unversioned.getLast());
                unversioned.removeLast();
            } catch (NumberFormatException ignored) {
            }
            newName.set(String.join("#", unversioned) + "#" + (++no));
        } while (
                getConfigManager().getConfig().getSavedItems().stream().anyMatch(si -> Objects.equals(si.getName(), newName.get()))
        );
        return newName.get();
    }

    @Override
    public void init() {
        super.init();
        this.clearChildren();
        info = new TextWidget(width / 2 + 120, 40, 200, 20, Text.literal(""), textRenderer);
        var nameInput = new TextFieldWidget(textRenderer, width / 2 - 100, height - 75, 58, 20, Text.empty());
        nameInput.setPlaceholder(Text.literal("Name"));
        addDrawableChild(nameInput);
        var hashInput = new TextFieldWidget(textRenderer, width / 2 - 39, height - 75, 138, 20, Text.empty());
        hashInput.setPlaceholder(Text.literal("Custom hash"));
        hashInput.setText(defHash);
        hashInput.setMaxLength(Integer.MAX_VALUE);
        addDrawableChild(hashInput);

        addDrawableChild(new SavedItemListWidget(200, 250, 20, width / 2 - 100, 35));


        addDrawableChild(new Button(width / 2 + 1, height - 50, 99, 20, Text.literal("Save Hash"), button -> {
            try {
                var code = hashInput.getText();
                if (code.isEmpty()) {
                    info.setMessage(Text.literal("Input a code").styled(style -> style.withColor(Formatting.RED)));
                    new Task(() -> info.setMessage(Text.empty()), 100);
                    return;
                }
                if (nameInput.getText().isEmpty()) {
                    info.setMessage(Text.literal("Input a name").styled(style -> style.withColor(Formatting.RED)));
                    new Task(() -> info.setMessage(Text.empty()), 100);
                    return;
                }
                Custom customItem = Custom.decodeCustom(null, code);
                SavedItem savedItem = new SavedItem(
                        nameInput.getText(),
                        customItem.getType(),
                        code,
                        WynnData.getIdMap().getOrDefault(customItem.getName(), -1)
                );
                var exisiting = getConfigManager().addSavedOrReturnExisting(savedItem);
                if (exisiting == null) {
                    savedItem.setName(getNotUsedName(savedItem.getName()));
                    getConfigManager().getConfig().getSavedItems().add(savedItem);
                    getConfigManager().saveConfig();
                } else {
                    WynnBuild.message(Text.literal("You already have this item saved ( ").append(customItem.createItemShowcase())
                            .append(" ) under the name of: ").append(Text.literal(exisiting.getName()).styled(style -> style.withBold(true))));
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
                }
            } catch (Exception ignored) {
                WynnBuild.message(Text.literal("Failed to save").styled(style -> style.withColor(Formatting.RED)));
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
            }
            init();
        }));

        addDrawableChild(new Button(width / 2 - 100, height - 50, 99, 20, Text.literal("Save Hand"), button -> {
            if (nameInput.getText().isEmpty()) {
                info.setMessage(Text.literal("Input a name").styled(style -> style.withColor(Formatting.RED)));
                new Task(() -> info.setMessage(Text.empty()), 100);
                return;
            }
            try {
                ItemStack item = client.player.getMainHandStack();
                Custom customItem = CustomUtil.getFromStack(item);
                ItemType type = customItem.getType();
                SavedItem savedItem = new SavedItem(
                        nameInput.getText(),
                        type,
                        customItem.encodeCustom(true).toB64(),
                        WynnData.getIdMap().getOrDefault(customItem.getName(), -1)
                );
                var exisiting = getConfigManager().addSavedOrReturnExisting(savedItem);
                if (exisiting == null) {
                    savedItem.setName(getNotUsedName(savedItem.getName()));
                    getConfigManager().getConfig().getSavedItems().add(savedItem);
                    getConfigManager().saveConfig();
                } else {
                    WynnBuild.message(Text.literal("You already have this item saved ( ").append(customItem.createItemShowcase())
                            .append(" ) under the name of: ").append(Text.literal(exisiting.getName()).styled(style -> style.withBold(true))));
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
                }
            } catch (Exception e) {
                info.setMessage(Text.literal("Invalid item").styled(style -> style.withColor(Formatting.RED)));
                new Task(() -> info.setMessage(Text.empty()), 100);
                e.printStackTrace();
            }
            init();
        }));
    }

    public class SavedItemListWidget extends AlwaysSelectedEntryListWidget<SavedItemListWidget.Entry> {

        public SavedItemListWidget(int width, int height, int top, int left, int itemHeight) {
            super(WynnBuild.client, width, height, top, itemHeight);
            this.setX(left);
            getConfigManager().getConfig().getSavedItems().forEach(savedItemType -> addEntry(new Entry(savedItemType)));
            addDrawableChild(new Button(left - 100, top, 100, 20, Text.literal("Copy Hash"), button -> {
                if (getSelectedOrNull() == null) return;
                SavedItem item = getSelectedOrNull().item;
                Custom customItem = Custom.decodeCustom(null, item.getHash());
                client.keyboard.setClipboard(getSelectedOrNull().item.getHash());
                WynnBuild.message(Text.literal("Copied hash of ")
                        .append(customItem.createItemShowcase()));
            }));
            //addDrawableChild(new Button(left - 100, top + 22, 100, 20, Text.literal("Builder"), button -> client.execute(() -> client.setScreen(new BuildScreen()))));
            addDrawableChild(new Button(left - 100, top + 44, 100, 20, Text.literal("DELETE").styled(style -> style.withColor(Formatting.DARK_RED)), button -> {
                if (getSelectedOrNull() == null) return;
                String name;
                String hash;
                try {
                    name = getSelectedOrNull().item.getName();
                    hash = getSelectedOrNull().item.getHash();
                    getConfigManager().getConfig().getSavedItems().removeIf(savedItemType ->
                            Objects.equals(savedItemType.getHash(), getSelectedOrNull().item.getHash()) &&
                                    Objects.equals(savedItemType.getName(), getSelectedOrNull().item.getName()));
                    getConfigManager().saveConfig();
                    removeEntry(getSelectedOrNull());
                    WynnBuild.message(Text.literal("Deleted ").append(Text.literal(name)
                            .styled(style -> style.withUnderline(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("CLICK TO COPY: ").append(hash)))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, hash)))).append("  ==  ").append(Custom.decodeCustom(null, hash).createItemShowcase()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }


        @Override
        public void setSelected(@Nullable SavedItemsScreen.SavedItemListWidget.Entry selected) {
            super.setSelected(selected);
        }

        @Override
        protected int getScrollbarX() {
            return getX() + getWidth();
        }

        @Override
        public int getRowWidth() {
            return 200;
        }

        public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            final SavedItem item;
            final Custom custom;
            final ItemStack displayStack;

            protected Entry(SavedItem item) {
                this.item = item;
                this.custom = Custom.decodeCustom(null, item.getHash());
                if (item.getBaseItemId() != null) {
                    this.displayStack = custom.createStack();
                } else displayStack = null;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                this.onPressed();
                return true;
            }

            void onPressed() {
                SavedItemListWidget.this.setSelected(this);
                setFocused(true);
            }

            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                Tier tier = custom.getTier();
                context.drawTextWithShadow(SavedItemsScreen.this.textRenderer, Text.literal(item.getName() + ": " + custom.statMap.get(IDs.NAME)), x + 2, y + 5, Formatting.WHITE.getColorValue());
                context.drawTextWithShadow(SavedItemsScreen.this.textRenderer, Text.literal(item.getType().name()).styled(style -> style.withColor(tier.format)), x + 2, y + 15, Formatting.WHITE.getColorValue());
                if (displayStack != null) {
                    float width = 16;
                    float height = 16;
                    float xOffset = 200 - 30;
                    float yOffset = (float) (35 - 24) / 2;
                    float scaleX = 1.2f;
                    float scaleY = 1.2f;

                    float centerX = x + width / 2 + xOffset;
                    float centerY = y + height / 2 + yOffset;

                    context.getMatrices().push();

                    context.getMatrices().translate(centerX, centerY, 0);

                    context.getMatrices().scale(scaleX, scaleY, 1.0f);

                    context.getMatrices().translate(-centerX, -centerY, 0);

                    context.drawItem(displayStack, (int) (x + xOffset), (int) (y + yOffset));
                    context.getMatrices().pop();
                }
                if (getSelectedOrNull() != null)
                    context.drawTooltip(textRenderer, getSelectedOrNull().custom.buildLore(), x + width, SavedItemListWidget.this.getY() + 17);
            }

            @Override
            public Text getNarration() {
                return Text.empty();
            }
        }
    }
}
