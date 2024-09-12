package com.gertoxq.quickbuild.config;

import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.gertoxq.quickbuild.screens.Button;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Wynnbuild Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        addDrawableChild(new Button(this.width / 2 - 100, this.height / 4, 200, 20,
                Text.literal("Buttons: ").append(QuickBuildClient.getConfigManager().getConfig().isShowButtons() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))),
                button -> {
                    QuickBuildClient.getConfigManager().getConfig().setShowButtons(!QuickBuildClient.getConfigManager().getConfig().isShowButtons());
                    button.setMessage(Text.literal("Buttons: ").append(QuickBuildClient.getConfigManager().getConfig().isShowButtons() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))));
                    QuickBuildClient.getConfigManager().saveConfig();
                }));
        addDrawableChild(new Button(this.width / 2 - 100, this.height / 4 + 24, 200, 20,
                Text.literal("Atree Presets: ").append(QuickBuildClient.getConfigManager().getConfig().isShowTreeLoader() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))),
                button -> {
                    QuickBuildClient.getConfigManager().getConfig().setShowTreeLoader(!QuickBuildClient.getConfigManager().getConfig().isShowTreeLoader());
                    button.setMessage(Text.literal("Atree Presets: ").append(QuickBuildClient.getConfigManager().getConfig().isShowTreeLoader() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))));
                    QuickBuildClient.getConfigManager().saveConfig();
                }));
        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 48, 100, 20, Text.literal("Atree code: "), textRenderer));

        var input = new TextFieldWidget(textRenderer, this.width / 2, this.height / 4 + 48, 100, 20, Text.literal(QuickBuildClient.getConfigManager().getConfig().getAtreeEncoding()));
        input.setText(QuickBuildClient.getConfigManager().getConfig().getAtreeEncoding());
        addDrawableChild(input);

        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 72, 100, 20, Text.literal("Powder level: "), textRenderer));

        var powderLevelInput = new TextFieldWidget(textRenderer, this.width / 2, this.height / 4 + 72, 30, 20, Text.literal(String.valueOf(QuickBuildClient.getConfigManager().getConfig().getDefaultPowderLevel())));
        powderLevelInput.setText(String.valueOf(QuickBuildClient.getConfigManager().getConfig().getDefaultPowderLevel()));
        addDrawableChild(powderLevelInput);

        addDrawableChild(new Button(this.width / 2 + 32, this.height / 4 + 72, 68, 20, Text.literal("Save").styled(style -> style.withColor(Formatting.GREEN)), button -> {
            try {
                int powderLevel = Integer.parseInt(powderLevelInput.getText());
                if (powderLevel < 1 || powderLevel > 6) throw new Exception("Not between 1 and 6");
                QuickBuildClient.getConfigManager().getConfig().setDefaultPowderLevel(powderLevel);
                QuickBuildClient.getConfigManager().saveConfig();
                client.player.sendMessage(Text.literal("Saved powder level"));
            } catch (Exception e) {
                client.player.sendMessage(Text.literal("Not a valid integer between 1 and 6"));
            }
        }));

        addDrawableChild(new Button(this.width / 2 - 50, this.height / 4 + 96, 100, 20,
                Text.literal("Close"),
                button -> client.setScreen(parent)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 30, 0xffffff);
    }
}
