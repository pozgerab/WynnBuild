package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.config.ConfigScreen;
import com.gertoxq.wynnbuild.config.SavedItem;
import com.gertoxq.wynnbuild.custom.AllIDs;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.custom.ID;
import com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery;
import com.gertoxq.wynnbuild.screens.atree.ImportAtreeScreen;
import com.gertoxq.wynnbuild.screens.components.DropdownScreen;
import com.gertoxq.wynnbuild.screens.itemmenu.SavedItemsScreen;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.WynnData;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;
import static com.gertoxq.wynnbuild.custom.CustomItem.getItem;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegistry {

    public static void init(MinecraftClient client) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("build")
                    .executes(context -> WynnBuildClient.build()).then(literal("help").executes(context -> {
                        var p = context.getSource().getClient().player;
                        if (p == null) return 0;
                        p.sendMessage(Text.literal("Welcome to WynnBuild").styled(style -> style.withColor(Formatting.GOLD)).append(
                                Text.literal("""
                                        
                                        This is a mod for quickly exporting your build with the use of wynnbuilder. As you run the '/build' command or click the build button on the right left side of your screen, this mod will generate you a wynnbuilder link that you can copy or share.
                                        You can configure the mod with /build config""")
                        ).styled(style -> style.withColor(Formatting.GOLD)), false);
                        return 1;
                    })));
            dispatcher.register(literal("build").then(literal("config").executes(context -> {
                client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen)));
                return 1;
            })));
            dispatcher.register(literal("build").then(literal("saveatree").executes(context -> {
                client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                return 1;
            })));
            dispatcher.register(literal("saveatree").executes(context -> {
                client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                return 1;
            }));
            dispatcher.register(literal("buildcustomitem").executes(context -> {
                buildCraftedMainHand();
                return 1;
            }));
            dispatcher.register(literal("build").then(literal("buildcustomitem").executes(context -> {
                buildCraftedMainHand();
                return 1;
            })));

            dispatcher.register(literal("readtree").executes(context -> {
                client.execute(() -> new AbilityTreeQuery().queryTree());
                return 1;
            }));

            //registerTestCommands(dispatcher);
            //registerSaveCommands(dispatcher);
            //registerBetaCommands(dispatcher);
        });
    }

    public static void registerTestCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("dropdowntest").executes(context -> {
            client.send(() -> client.setScreen(new DropdownScreen()));
            return 1;
        }));
    }

    public static void registerSaveCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("build").then(literal("saveditems").executes(context -> {
            client.send(() -> client.setScreen(new SavedItemsScreen(client.currentScreen)));
            return 1;
        })));
        dispatcher.register(literal("build").then(literal("saveitem").executes(context -> {
            try {
                ItemStack item = client.player.getMainHandStack();
                CustomItem customItem = getItem(item);
                if (customItem == null) {
                    throw new Exception("customItem = null");
                }
                String itemName = customItem.get(AllIDs.NAME);
                ID.ItemType type = customItem.getType();
                SavedItem savedItem = new SavedItem(
                        "h:" + itemName,
                        type,
                        customItem.encodeCustom(true),
                        WynnData.getIdMap().getOrDefault(itemName, null)
                );
                var exisiting = getConfigManager().addSavedOrReturnExisting(savedItem);
                if (exisiting == null) {
                    client.player.sendMessage(Text.literal("Saved ").append(customItem.createItemShowcase())
                            .append(" under the name of: ").append(Text.literal(savedItem.getName()).styled(style -> style.withBold(true))), false);
                } else {
                    client.player.sendMessage(Text.literal("You already have this item saved ( ").append(customItem.createItemShowcase())
                            .append(" ) under the name of: ").append(Text.literal(exisiting.getName()).styled(style -> style.withBold(true))), false);
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
                }
            } catch (Exception e) {
                e.printStackTrace();
                displayErr("Can't save this item");
            }
            return 1;
        })));
    }

    public static void registerBetaCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("build").then(literal("beta").then(literal("gallery").executes(context -> {
            client.player.sendMessage(Text.literal("Disabled feature").styled(style -> style.withColor(Formatting.RED)), false);
            //client.send(() -> client.setScreen(new GalleryScreen()));
            return 1;
        }))));
    }
}
