package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.screens.Button;
import com.gertoxq.wynnbuild.screens.builder.BuildScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
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
    public void init() {
        super.init();

        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)), Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED)))
                .initially(WynnBuildClient.getConfigManager().getConfig().isShowButtons())
                .build(this.width / 2 - 100, this.height / 4, 200, 20, Text.literal("Buttons"), (button, value) -> {
                    WynnBuildClient.getConfigManager().getConfig().setShowButtons(value);
                    WynnBuildClient.getConfigManager().saveConfig();
                }));
        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)), Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED)))
                .initially(WynnBuildClient.getConfigManager().getConfig().isShowTreeLoader())
                .build(this.width / 2 - 100, this.height / 4 + 24, 200, 20, Text.literal("Atree Presets"),
                        (button, value) -> {
                            WynnBuildClient.getConfigManager().getConfig().setShowTreeLoader(value);
                            WynnBuildClient.getConfigManager().saveConfig();
                        }));
        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 48, 100, 20, Text.literal("Atree code: "), textRenderer));

        var input = new TextFieldWidget(textRenderer, this.width / 2, this.height / 4 + 48, 100, 20, Text.literal(WynnBuildClient.getConfigManager().getConfig().getAtreeEncoding()));
        input.setText(WynnBuildClient.getConfigManager().getConfig().getAtreeEncoding());
        addDrawableChild(input);

        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 72, 100, 20, Text.literal("Powder level: "), textRenderer));

        addDrawableChild(CyclingButtonWidget.<Integer>builder(val -> Text.literal(String.valueOf(val)))
                .values(1, 2, 3, 4, 5, 6)
                .initially(WynnBuildClient.getConfigManager().getConfig().getDefaultPowderLevel())
                .omitKeyText()
                .build(this.width / 2, this.height / 4 + 72, 30, 20, Text.empty(),
                        (button, value) -> {
                            WynnBuildClient.getConfigManager().getConfig().setDefaultPowderLevel(value);
                            WynnBuildClient.getConfigManager().saveConfig();
                        }));

        addDrawableChild(CyclingButtonWidget.<Integer>builder(integer -> Text.literal(BuildScreen.PRECISION_OPTIONS.get(integer)).styled(style -> style.withColor(Formatting.AQUA)))
                .values(0, 1, 2, 3)
                .initially(WynnBuildClient.getConfigManager().getConfig().getPrecision())
                .build(this.width / 2 - 100, this.height / 4 + 96, 179, 20, Text.literal("Build Precision"),
                        (button, value) -> {
                            WynnBuildClient.getConfigManager().getConfig().setPrecision(value);
                            WynnBuildClient.getConfigManager().saveConfig();
                        }));

        addDrawableChild(ButtonWidget.builder(
                Text.literal("?"),
                button -> client.player.sendMessage(Text.literal("Precision options:\n").styled(style -> style.withColor(Formatting.GOLD)).append(BuildScreen.precisionTooltip))
        ).position(this.width / 2 + 80, this.height / 4 + 96).size(20, 20).build());

        addDrawableChild(new Button(this.width / 2 - 50, this.height / 4 + 122, 100, 20,
                Text.literal("Close"),
                button -> client.setScreen(parent)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 30, 0xffffff);
    }
}
