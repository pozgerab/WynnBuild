package com.gertoxq.quickbuild.screens.itemmenu;

import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.gertoxq.quickbuild.config.SavedItemType;
import com.gertoxq.quickbuild.custom.AllIDs;
import com.gertoxq.quickbuild.custom.CustomItem;
import com.gertoxq.quickbuild.custom.ID;
import com.gertoxq.quickbuild.screens.Button;
import com.gertoxq.quickbuild.screens.builder.BuildScreen;
import com.gertoxq.quickbuild.util.Task;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.gertoxq.quickbuild.client.QuickBuildClient.getConfigManager;
import static com.gertoxq.quickbuild.client.QuickBuildClient.idMap;
import static com.gertoxq.quickbuild.custom.CustomItem.getCustomFromHash;
import static com.gertoxq.quickbuild.custom.CustomItem.getItem;

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
    protected void init() {
        super.init();
        this.clearChildren();
        getConfigManager().loadConfig();
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
                CustomItem customItem = CustomItem.getCustomFromHash(code);
                if (customItem == null) {
                    info.setMessage(Text.literal("Invalid hash").styled(style -> style.withColor(Formatting.RED)));
                    new Task(() -> info.setMessage(Text.empty()), 100);
                    return;
                }
                SavedItemType savedItem = new SavedItemType(
                        nameInput.getText(),
                        customItem.get(AllIDs.TYPE),
                        code,
                        idMap.getOrDefault(customItem.get(AllIDs.NAME), -1)
                );
                var exisiting = getConfigManager().addSavedOrReturnExisting(savedItem);
                if (exisiting == null) {
                    savedItem.setName(getNotUsedName(savedItem.getName()));
                    getConfigManager().getConfig().getSavedItems().add(savedItem);
                    getConfigManager().saveConfig();
                } else {
                    client.player.sendMessage(Text.literal("You already have this item saved ( ").append(customItem.createItemShowcase())
                            .append(" ) under the name of: ").append(Text.literal(exisiting.getName()).styled(style -> style.withBold(true))));
                }
            } catch (Exception ignored) {
                client.player.sendMessage(Text.literal("Failed to save").styled(style -> style.withColor(Formatting.RED)));
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
                CustomItem customItem = getItem(item);
                if (customItem == null) {
                    throw new Exception("customItem = null");
                }
                ID.ItemType type = customItem.getType();
                SavedItemType savedItem = new SavedItemType(
                        nameInput.getText(),
                        type,
                        customItem.encodeCustom(true),
                        idMap.getOrDefault(customItem.get(AllIDs.NAME), -1)
                );
                var exisiting = getConfigManager().addSavedOrReturnExisting(savedItem);
                if (exisiting == null) {
                    savedItem.setName(getNotUsedName(savedItem.getName()));
                    getConfigManager().getConfig().getSavedItems().add(savedItem);
                    getConfigManager().saveConfig();
                } else {
                    client.player.sendMessage(Text.literal("You already have this item saved ( ").append(customItem.createItemShowcase())
                            .append(" ) under the name of: ").append(Text.literal(exisiting.getName()).styled(style -> style.withBold(true))));
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
            super(QuickBuildClient.client, width, height, top, itemHeight);
            this.setX(left);
            getConfigManager().getConfig().getSavedItems().forEach(savedItemType -> {
                addEntry(new Entry(savedItemType));
            });
            addDrawableChild(new Button(left - 100, top, 100, 20, Text.literal("Copy Hash"), button -> {
                if (getSelectedOrNull() == null) return;
                SavedItemType item = getSelectedOrNull().item;
                CustomItem customItem = CustomItem.getCustomFromHash(item.getHash());
                client.keyboard.setClipboard(getSelectedOrNull().item.getHash());
                client.player.sendMessage(Text.literal("Copied hash of ")
                        .append(customItem.createItemShowcase()));
            }));
            addDrawableChild(new Button(left - 100, top + 22, 100, 20, Text.literal("Builder"), button -> {
                client.execute(() -> client.setScreen(new BuildScreen()));
            }));
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
                    client.player.sendMessage(Text.literal("Deleted ").append(Text.literal(name)
                            .styled(style -> style.withUnderline(true).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("CLICK TO COPY: ").append(hash)))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, hash)))).append("  ==  ").append(getCustomFromHash(hash).createItemShowcase()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }

        public void createFilter() {

        }

        @Override
        public void setSelected(@Nullable SavedItemsScreen.SavedItemListWidget.Entry selected) {
            super.setSelected(selected);
        }

        @Override
        protected int getDefaultScrollbarX() {
            return getX() + getWidth();
        }

        @Override
        public int getRowWidth() {
            return 200;
        }

        public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            final SavedItemType item;
            final CustomItem custom;
            final ItemStack displayStack;

            protected Entry(SavedItemType item) {
                this.item = item;
                this.custom = CustomItem.getCustomFromHash(item.getHash());
                if (custom.getBaseItemId() != null) {
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
                ID.Tier tier = custom.get(AllIDs.TIER);
                context.drawTextWithShadow(SavedItemsScreen.this.textRenderer, Text.literal(item.getName() + ": " + custom.get(AllIDs.NAME)), x + 2, y + 5, Formatting.WHITE.getColorValue());
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
