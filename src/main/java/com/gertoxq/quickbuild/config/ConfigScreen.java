package com.gertoxq.quickbuild.config;

import com.gertoxq.quickbuild.EncodeATree;
import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.gertoxq.quickbuild.client.ReadBtn;
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

        addDrawableChild(new ReadBtn(this.width / 2 - 100, this.height / 4, 200, 20,
                Text.literal("Buttons: ").append(config.isShowButtons() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))),
                button -> {
                    config.setShowButtons(!config.isShowButtons());
                    button.setMessage(Text.literal("Buttons: ").append(config.isShowButtons() ? Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)) : Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED))));
                    QuickBuildClient.getConfigManager().saveConfig();
                }));
        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4+24, 100, 20, Text.literal("Atree code: "), textRenderer));

        var input = new TextFieldWidget( textRenderer, this.width / 2, this.height / 4 + 24, 100, 20, Text.literal(config.getAtreeEncoding()));
        input.setText(config.getAtreeEncoding());
        addDrawableChild(input);
        addDrawableChild(new ReadBtn(this.width / 2 - 100, this.height / 4 + 44, 200, 20, Text.literal("Save atree code").styled(style -> style.withColor(Formatting.GREEN)), button -> {
            try {
                EncodeATree.decode_atree(input.getText());
                config.setAtreeEncoding(input.getText());
                QuickBuildClient.getConfigManager().saveConfig();
            } catch (Exception e) {
                client.player.sendMessage(Text.literal("Invalid code!").styled(style -> style.withColor(Formatting.RED)));
            }

        }));

        addDrawableChild(new ReadBtn(this.width / 2 - 50, this.height / 4 + 64, 100, 20,
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
