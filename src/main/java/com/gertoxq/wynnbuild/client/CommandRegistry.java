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

import java.net.URI;

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
                                                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click for Modrinth page")))
                                                .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/wynnbuild")))))
                                        .styled(style -> style.withColor(Formatting.GOLD)));
                                return 1;
                            }))
                            .then(literal("config").executes(context -> {
                                client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen)));
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
                            .then(literal("readAspects").executes(context -> {
                                client.execute(() -> new AspectInfo().queryAspectInfo());
                                return 1;
                            }))
                            .then(literal("reloadcache").executes(context -> {
                                BuilderDataManager.reloadBuilderData(true);
                                return 1;
                            }))
                            .then(literal("debug").executes(context -> {
                                WynnBuild.toggleDebug();
                                WynnBuild.message(Text.literal("Debug mode is now " + (WynnBuild.isDebug() ? "enabled" : "disabled")).styled(style -> style.withColor(WynnBuild.isDebug() ? Formatting.GREEN : Formatting.RED)));
                                return 1;
                            }).then(literal("clearatreestate").executes(commandContext -> {
                                WynnBuild.atreeState.clear();
                                WynnBuild.saveAtreeCache();
                                WynnBuild.message(Text.literal("Cleared current ability tree"));
                                return 1;
                            })))
                            .then(literal("issue").executes(context -> {
                                WynnBuild.message(Text.literal("If your build did not generate correctly, try ")
                                        .append(Text.literal("reloading the cache").styled(style -> style.withUnderline(true).withClickEvent(
                                                new ClickEvent.SuggestCommand("/build reloadcache"))))
                                        .append(" or ")
                                        .append(Text.literal("fetching the ability tree").styled(style -> style.withUnderline(true).withClickEvent(
                                                new ClickEvent.SuggestCommand("/build readtree")
                                        )))
                                        .append(" and try again. If neither work or you have other issues, ")
                                        .append(Text.literal("join the discord").styled(style -> style.withUnderline(true).withClickEvent(
                                                        new ClickEvent.OpenUrl(URI.create("https://discord.gg/5aPfDYGMKm")))
                                                .withHoverEvent(
                                                        new HoverEvent.ShowText(Text.literal("https://discord.gg/5aPfDYGMKm"))))
                                        ).styled(style -> style.withColor(Formatting.GRAY)));
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
