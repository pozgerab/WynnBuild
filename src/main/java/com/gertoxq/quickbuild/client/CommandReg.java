package com.gertoxq.quickbuild.client;

import com.gertoxq.quickbuild.config.ConfigScreen;
import com.gertoxq.quickbuild.screens.ImportAtreeScreen;
import com.gertoxq.quickbuild.screens.builder.BuildScreen;
import com.gertoxq.quickbuild.screens.itemmenu.SavedItemsScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.gertoxq.quickbuild.client.QuickBuildClient.buildCrafted;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandReg {

    public static void init(MinecraftClient client) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("build")
                    .executes(context -> QuickBuildClient.build()).then(literal("help").executes(context -> {
                        var p = context.getSource().getClient().player;
                        if (p == null) return 0;
                        p.sendMessage(Text.literal("Welcome to QuickBuild").styled(style -> style.withColor(Formatting.GOLD)).append(
                                Text.literal("""
                                        
                                        This is a mod for quickly exporting your build with the use of wynnbuilder. As you run the '/build' command or click the build button on the right left side of your screen, this mod will generate you a wynnbuilder link that you can copy or share.
                                        You can configure the mod with /build config""")
                        ).styled(style -> style.withColor(Formatting.GOLD)));
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
                buildCrafted();
                return 1;
            }));
            dispatcher.register(literal("build").then(literal("buildcustomitem").executes(context -> {
                buildCrafted();
                return 1;
            })));
            dispatcher.register(literal("build").then(literal("saveditems").executes(context -> {
                client.send(() -> client.setScreen(new SavedItemsScreen(client.currentScreen)));
                return 1;
            })));
            dispatcher.register(literal("build").then(literal("builder").executes(context -> {
                client.send(() -> client.setScreen(new BuildScreen()));
                return 1;
            })));
        });
    }
}
