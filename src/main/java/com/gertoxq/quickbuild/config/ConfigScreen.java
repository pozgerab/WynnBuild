package com.gertoxq.quickbuild.config;

import com.gertoxq.quickbuild.client.ClickButton;
import com.gertoxq.quickbuild.client.QuickBuildClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private final Screen parent;
    public ConfigScreen(Screen parent) {
        super(Text.literal("Wynnbuild config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        ConfigType config = QuickBuildClient.getConfigManager().getConfig();

        addDrawableChild(new ClickButton(this.width / 2 - 100, this.height / 4, 200, 20,
                Text.literal("Buttons: ").append(config.isShowButtons() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))),
                button -> {
                    config.setShowButtons(!config.isShowButtons());
                    button.setMessage(Text.literal("Buttons: ").append(config.isShowButtons() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))));
                    QuickBuildClient.getConfigManager().saveConfig();
                }));
        addDrawableChild(new ClickButton(this.width / 2 - 100, this.height / 4+24, 200, 20,
                Text.literal("Atree Presets: ").append(config.isShowTreeLoader() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))),
                button -> {
                    config.setShowTreeLoader(!config.isShowTreeLoader());
                    button.setMessage(Text.literal("Atree Presets: ").append(config.isShowTreeLoader() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))));
                    QuickBuildClient.getConfigManager().saveConfig();
                }));
        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4+48, 100, 20, Text.literal("Atree code: "), textRenderer));

        var input = new TextFieldWidget( textRenderer, this.width / 2, this.height / 4 + 48, 100, 20, Text.literal(config.getAtreeEncoding()));
        input.setText(config.getAtreeEncoding());
        addDrawableChild(input);

        addDrawableChild(new ClickButton(this.width / 2 - 50, this.height / 4 + 72, 100, 20,
                Text.literal("Close"),
                button -> client.setScreen(parent)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 30, 0xffffff);
    }
}
