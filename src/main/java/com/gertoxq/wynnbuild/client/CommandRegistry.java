package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.custom.CustomUtil;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.config.ConfigScreen;
import com.gertoxq.wynnbuild.config.SavedItem;
import com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery;
import com.gertoxq.wynnbuild.screens.atree.ImportAtreeScreen;
import com.gertoxq.wynnbuild.screens.components.DropdownScreen;
import com.gertoxq.wynnbuild.screens.itemmenu.SavedItemsScreen;
import com.gertoxq.wynnbuild.util.WynnData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegistry {

    public static void init(MinecraftClient client) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("build").then(literal("withAtreeRefresh").executes(context -> {
                                WynnBuild.buildWithArgs(WynnBuild.getConfigManager().getConfig().getPrecision() == 1, true);
                                return 1;
                            })).executes(context -> WynnBuild.build())
                            .then(literal("help").executes(context -> {
                                WynnBuild.message(Text.literal("\tWelcome to WynnBuild! Instructions ")
                                        .append(Text.literal("HERE").styled(style -> style.withUnderline(true)
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click for Modrinth page")))
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/wynnbuild"))))
                                        .styled(style -> style.withColor(Formatting.GOLD)));
                                return 1;
                            }))
                            .then(literal("config").executes(context -> {
                                client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen)));
                                return 1;
                            }))
                            .then(literal("saveatree")
                                    .then(argument("hash", StringArgumentType.word()).executes(context -> {
                                        client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen, "", StringArgumentType.getString(context, "hash"))));
                                        return 1;
                                    }))
                                    .executes(context -> {
                                        client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                                        return 1;
                                    }))
                            .then(literal("buildcustomitem").executes(context -> {
                                WynnBuild.buildMainHand();
                                return 1;
                            }))
                            .then(literal("readtree").executes(context -> {
                                client.execute(() -> new AbilityTreeQuery().queryTree());
                                return 1;
                            }))
            );

            dispatcher.register(literal("buildcustomitem").executes(context -> {
                WynnBuild.buildMainHand();
                return 1;
            }));

            //registerTestCommands(dispatcher);
            //registerSaveCommands(dispatcher);
            //registerBetaCommands(dispatcher);
        });
    }

    public static void registerTestCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("dropdowntest").executes(context -> {
            WynnBuild.client.send(() -> WynnBuild.client.setScreen(new DropdownScreen()));
            return 1;
        }));
    }

    public static void registerSaveCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("build").then(literal("saveditems").executes(context -> {
            WynnBuild.client.send(() -> WynnBuild.client.setScreen(new SavedItemsScreen(WynnBuild.client.currentScreen)));
            return 1;
        })));
        dispatcher.register(literal("build").then(literal("saveitem").executes(context -> {
            try {
                ItemStack item = WynnBuild.client.player.getMainHandStack();
                Custom customItem = CustomUtil.getFromStack(item);
                if (customItem.isNone()) {
                    // empty item
                    return 0;
                }
                String itemName = customItem.getName();
                ItemType type = customItem.getType();
                SavedItem savedItem = new SavedItem(
                        "h:" + itemName,
                        type,
                        customItem.encodeCustom(true).toB64(),
                        WynnData.getIdMap().getOrDefault(itemName, null)
                );
                var exisiting = WynnBuild.getConfigManager().addSavedOrReturnExisting(savedItem);
                if (exisiting == null) {
                    WynnBuild.message(Text.literal("Saved ").append(customItem.createItemShowcase())
                            .append(" under the name of: ").append(Text.literal(savedItem.getName()).styled(style -> style.withBold(true))));
                } else {
                    WynnBuild.message(Text.literal("You already have this item saved ( ").append(customItem.createItemShowcase())
                            .append(" ) under the name of: ").append(Text.literal(exisiting.getName()).styled(style -> style.withBold(true))));
                    WynnBuild.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
                }
            } catch (Exception e) {
                e.printStackTrace();
                WynnBuild.displayErr("Can't save this item");
            }
            return 1;
        })));
    }

    public static void registerBetaCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("build").then(literal("beta").then(literal("gallery").executes(context -> {
            WynnBuild.message(Text.literal("Disabled feature").styled(style -> style.withColor(Formatting.RED)));
            //client.send(() -> client.setScreen(new GalleryScreen()));
            return 1;
        }))));
    }
}
