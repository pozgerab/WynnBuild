package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.config.ConfigScreen;
import com.gertoxq.wynnbuild.webquery.BuilderDataManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sun.jdi.connect.Connector;
import com.wynntils.core.components.Services;
import com.wynntils.models.abilitytree.type.SavableAbilityTree;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.services.loadout.type.Loadout;
import com.wynntils.services.loadout.type.LoadoutType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.gertoxq.wynnbuild.WynnBuild.getConfig;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegistry {

    public static void init(MinecraftClient client) {

        registerSimpleCommand("withAtreeRefresh", () -> WynnBuild.buildWithArgs(true));
        registerCommand(loadoutCommand("loadout"));
        registerSimpleCommand("help", CommandRegistry::showHelpMessage);
        registerSimpleCommand("config", () -> openConfigScreen(client));
        registerSimpleCommand("encodehelditem", WynnBuild::buildMainHand);
        registerSimpleCommand("reloadcache", () -> BuilderDataManager.reloadBuilderData(true));
        registerSimpleCommand("debug", CommandRegistry::toggleDebugNotifyClient);
        registerSimpleCommand("issue", CommandRegistry::showIssueMsg);
        registerCommand(importAbilityTreeCommand("importtree"));
        registerCommand(exportAbilityTreeCommand("exporttree"));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var buildCommand = literal("build").executes(context -> {
                WynnBuild.build();
                return 1;
            });
            for (var arg : arguments) {
                buildCommand.then(arg);
            }
            dispatcher.register(buildCommand);
        });
    }

    private static final List<ArgumentBuilder<FabricClientCommandSource, ?>> arguments = new ArrayList<>();

    public static void registerCommand(ArgumentBuilder<FabricClientCommandSource, ?> argument) {
        arguments.add(argument);
    }

    public static void registerSimpleCommand(String name, Runnable action) {
        arguments.add(literal(name).executes(context -> {
            action.run();
            return 1;
        }));
    }

    public static ArgumentBuilder<FabricClientCommandSource, ?> loadoutCommand(String name) {
        return literal(name)
                .then(argument("loadout_name", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            Services.loadout.loadouts.get().values().stream().filter(loadout -> loadout.type() == LoadoutType.BUILD).map(Loadout::name).forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(commandContext
                                -> loadoutCommandCore(commandContext, null)));
    }

    private static int loadoutCommandCore(CommandContext<FabricClientCommandSource> context, @Nullable Boolean precise) {
        String loadoutName = context.getArgument("loadout_name", String.class);
        Loadout loadout = Services.loadout.loadouts.get().get(loadoutName);
        if (loadout == null) {
            WynnBuild.displayErr("No loadout found with name " + loadoutName);
            return 0;
        }
        if (loadout.type() != LoadoutType.BUILD) {
            WynnBuild.displayErr("Loadout " + loadoutName + " is a " + loadout.type() +" loadout, not a build loadout");
            return 0;
        }
        Build.fromLoadout(loadout, precise == null ? getConfig().getPrecision() == 1 : precise).display();

        return 1;
    }

    public static void showHelpMessage() {
        WynnBuild.message(Text.literal("\tWelcome to WynnBuild! Instructions ")
                    .append(Text.literal("HERE").styled(style -> style.withUnderline(true)
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click for Modrinth page")))
                            .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/wynnbuild")))))
                    .styled(style -> style.withColor(Formatting.GOLD)));
    }

    public static void openConfigScreen(MinecraftClient client) {
        client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen)));
    }

    public static void toggleDebugNotifyClient() {
        WynnBuild.toggleDebug();
        WynnBuild.message(Text.literal("Debug mode is now " + (WynnBuild.isDebug() ? "enabled" : "disabled")).styled(style -> style.withColor(WynnBuild.isDebug() ? Formatting.GREEN : Formatting.RED)));
    }

    public static void showIssueMsg() {
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
    }

    public static ArgumentBuilder<FabricClientCommandSource, ?> importAbilityTreeCommand(String name) {
        return literal(name).then(argument("tree_code", StringArgumentType.word())
                .then(argument("classType", StringArgumentType.word()).suggests((context, builder) -> {
                    for (ClassType classType : List.of(ClassType.ARCHER, ClassType.WARRIOR, ClassType.MAGE, ClassType.ASSASSIN, ClassType.SHAMAN)) {
                        builder.suggest(classType.getName());
                    }
                    return builder.buildFuture();
                }).then(argument("loadout_name", StringArgumentType.greedyString())
                .executes(context -> {
                    String treeCode = context.getArgument("tree_code", String.class);
                    String classTypeStr = context.getArgument("classType", String.class);
                    ClassType classType = ClassType.fromName(classTypeStr);
                    if (classType == ClassType.NONE) {
                        WynnBuild.displayErr("Invalid class type: \"" + classTypeStr + "\"");
                        return 0;
                    }
                    String loadoutName = context.getArgument("loadout_name", String.class);
                    if (Services.loadout.hasLoadout(loadoutName)) {
                        WynnBuild.displayErr("Loadout with name \"" + loadoutName + "\" already exists.");
                        return 0;
                    }
                    SavableAbilityTree abilityTree = Build.AbilityTree.decode(treeCode, classType);
                    Services.loadout.saveAbilityTreeLoadout(loadoutName, abilityTree);

                    Text importMessage = Text.literal("\nImported ")
                            .styled(style -> style.withColor(Formatting.GRAY))
                            .append(Text.literal(classType.getName())
                                    .styled(style -> style.withColor(Formatting.AQUA)))
                            .append(Text.literal(" tree under the name ")
                                    .styled(style -> style.withColor(Formatting.GRAY)))
                            .append(Text.literal(loadoutName)
                                    .styled(style -> style.withColor(Formatting.WHITE)))
                            .append(Text.literal(". Check it out inside the Wynntils ")
                                    .styled(style -> style.withColor(Formatting.GRAY)))
                            .append(Text.literal("Ability Tree Loadouts")
                                    .styled(style -> style.withColor(Formatting.GREEN)))
                            .append(Text.literal(".\n")
                                    .styled(style -> style.withColor(Formatting.GRAY)));
                    WynnBuild.message(importMessage);
                    return 1;
                }))));
    }

    public static ArgumentBuilder<FabricClientCommandSource, ?> exportAbilityTreeCommand(String name) {
        return literal(name).then(argument("loadout_name", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    Services.loadout.loadouts.get().values().stream()
                            .filter(loadout -> loadout.type() == LoadoutType.ABILITY_TREE || loadout.type() == LoadoutType.BUILD)
                            .map(Loadout::name).forEach(builder::suggest);
                    return builder.buildFuture();
                })
                .executes(
                context -> {
                    String loadoutName = context.getArgument("loadout_name", String.class);
                    Loadout loadout = Services.loadout.loadouts.get().get(loadoutName);
                    if (loadout == null) {
                        WynnBuild.displayErr("No loadout found with name " + loadoutName);
                        return 0;
                    }
                    if (loadout.type() != LoadoutType.BUILD && loadout.type() != LoadoutType.ABILITY_TREE) {
                        WynnBuild.displayErr("Loadout " + loadoutName + " is a " + loadout.type() +" loadout, not a build loadout");
                        return 0;
                    }

                    String encoded = Build.AbilityTree.encode(loadout.abilityTree()).toB64();
                    showSuccessfulAtreeEncoding(encoded, loadoutName);
                    return 1;
                }
        ));
    }

    private static void showSuccessfulAtreeEncoding(String encodedStr, String loadoutName) {
        Text message = Text.literal("\nExported loadout ")
                .styled(style -> style.withColor(Formatting.GREEN))
                .append(Text.literal(loadoutName)
                        .styled(style -> style.withColor(Formatting.WHITE)))
                .append(Text.literal("\n"))
                .append(Text.literal("Encoded Hash: ")
                        .styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal(encodedStr)
                        .styled(style -> style.withColor(Formatting.YELLOW)))
                .append(Text.literal("\n"))
                .append(Text.literal("[ Copy Hash ]")
                        .styled(style -> style
                                .withColor(Formatting.AQUA)
                                .withBold(true)
                                .withUnderline(true)
                                .withHoverEvent(new HoverEvent.ShowText(
                                        Text.literal("Click to copy to clipboard")
                                                .styled(s -> s.withColor(Formatting.WHITE))
                                ))
                                .withClickEvent(new ClickEvent.CopyToClipboard(encodedStr))
                        )
                )
                .append(Text.literal("\n"))
                .append(Text.literal("Paste this hash into the ")
                        .styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("Ability Tree")
                        .styled(style -> style.withColor(Formatting.WHITE)))
                .append(Text.literal(" section on the ")
                        .styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("WynnBuilder")
                        .styled(style -> style.withColor(Formatting.GREEN)))
                .append(Text.literal(" website.\n")
                        .styled(style -> style.withColor(Formatting.GRAY)));
        WynnBuild.message(message);
    }
}
