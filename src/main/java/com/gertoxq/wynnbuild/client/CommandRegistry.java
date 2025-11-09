package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.config.ConfigScreen;
import com.gertoxq.wynnbuild.screens.aspect.AspectInfo;
import com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery;
import com.gertoxq.wynnbuild.webquery.BuilderDataManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegistry {

    public static void init(MinecraftClient client) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("build").then(literal("withAtreeRefresh").executes(context -> {
                                WynnBuild.buildWithArgs(true);
                                return 1;
                            })).executes(context -> {
                                WynnBuild.build();
                                return 1;
                            })
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
                            /*.then(literal("saveatree")
                                    .then(argument("hash", StringArgumentType.word()).executes(context -> {
                                        client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen, "", StringArgumentType.getString(context, "hash"))));
                                        return 1;
                                    }))
                                    .executes(context -> {
                                        client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                                        return 1;
                                    }))*/
                            .then(literal("buildcustomitem").executes(context -> {
                                WynnBuild.buildMainHand();
                                return 1;
                            }))
                            .then(literal("readtree").executes(context -> {
                                client.execute(() -> new AbilityTreeQuery().queryTree());
                                return 1;
                            }))
                            .then(literal("readAspects").executes(context -> {
                                client.execute(() -> new AspectInfo().queryAspectInfo());
                                return 1;
                            }))
                            .then(literal("reloadcache").executes(context -> {
                                BuilderDataManager.reloadBuilderData(true);
                                return 1;
                            }))
            );

            dispatcher.register(literal("buildcustomitem").executes(context -> {
                WynnBuild.buildMainHand();
                return 1;
            }));
        });
    }

}
