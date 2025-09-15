package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.screens.Button;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.gertoxq.wynnbuild.WynnBuild.getConfigManager;

public class ConfigScreen extends Screen {
    private static final SimpleOption.TooltipFactory<Integer> helpFactory = value -> Tooltip.of(Text.literal(Build.PRECISION_OPTIONS.get(value)).styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true))
            .append(Text.literal(" - ").styled(style -> style.withColor(Formatting.DARK_GRAY)).append(Build.PRECISION_TOOLTIPS.get(value)).styled(style -> style.withColor(Formatting.GOLD))));
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Wynnbuild Config"));
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();

        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)), Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED)))
                .initially(getConfigManager().getConfig().isShowButtons())
                .build(this.width / 2 - 100, this.height / 4, 200, 20, Text.literal("Buttons"), (button, value) -> {
                    getConfigManager().getConfig().setShowButtons(value);
                    getConfigManager().saveConfig();
                }));
        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("Shown").styled(style -> style.withColor(Formatting.GREEN)), Text.literal("Hidden").styled(style -> style.withColor(Formatting.RED)))
                .initially(getConfigManager().getConfig().isShowTreeLoader())
                .build(this.width / 2 - 100, this.height / 4 + 24, 200, 20, Text.literal("Atree Presets"),
                        (button, value) -> {
                            getConfigManager().getConfig().setShowTreeLoader(value);
                            getConfigManager().saveConfig();
                        }));
        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 48, 100, 20, Text.literal("Atree code: "), textRenderer));

        var input = new TextFieldWidget(textRenderer, this.width / 2, this.height / 4 + 48, 100, 20, Text.literal(WynnBuild.getAtreeSuffix()));
        input.setText(WynnBuild.getAtreeSuffix());
        input.setEditable(false);
        addDrawableChild(input);

        addDrawableChild(new TextWidget(this.width / 2 - 100, this.height / 4 + 72, 100, 20, Text.literal("Powder level: "), textRenderer));

        addDrawableChild(CyclingButtonWidget.<Integer>builder(val -> Text.literal(String.valueOf(val)))
                .values(1, 2, 3, 4, 5, 6)
                .initially(getConfigManager().getConfig().getDefaultPowderLevel())
                .omitKeyText()
                .build(this.width / 2, this.height / 4 + 72, 30, 20, Text.empty(),
                        (button, value) -> {
                            getConfigManager().getConfig().setDefaultPowderLevel(value);
                            getConfigManager().saveConfig();
                        }));

        var help = ButtonWidget.builder(
                        Text.literal("?"),
                        button -> {
                            if (client.player == null) return;
                            WynnBuild.message(Text.literal("Precision options:\n").styled(style -> style.withColor(Formatting.GOLD)).append(Build.precisionTooltip));
                        })
                .position(this.width / 2 + 80, this.height / 4 + 96)
                .tooltip(helpFactory.apply(getConfigManager().getConfig().getPrecision()))
                .size(20, 20).build();

        addDrawableChild(CyclingButtonWidget.onOffBuilder()
                .values(false, true)
                .initially(getConfigManager().getConfig().getPrecision() == 1)
                .build(this.width / 2 - 100, this.height / 4 + 96, 179, 20, Text.literal("Build Precision"),
                        (button, value) -> {
                            help.setTooltip(helpFactory.apply(value ? 1 : 0));
                            getConfigManager().getConfig().setPrecision(value ? 1 : 0);
                            getConfigManager().saveConfig();
                        }));

        addDrawableChild(help);

        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("ON").styled(style -> style.withColor(Formatting.GREEN)),
                        Text.literal("OFF").styled(style -> style.withColor(Formatting.RED))).initially(getConfigManager().getConfig().isIncludeTomes())
                .build(this.width / 2 - 100, this.height / 4 + 120, 200, 20, Text.literal("Include Tomes"),
                        (button, value) -> {
                            getConfigManager().getConfig().setIncludeTomes(value);
                            getConfigManager().saveConfig();
                        }));

        addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("ON").styled(style -> style.withColor(Formatting.GREEN)),
                        Text.literal("OFF").styled(style -> style.withColor(Formatting.RED))).initially(getConfigManager().getConfig().isIncludeAspects())
                .build(this.width / 2 - 100, this.height / 4 + 144, 200, 20, Text.literal("Include Aspects"),
                        (button, value) -> {
                            getConfigManager().getConfig().setIncludeAspects(value);
                            getConfigManager().saveConfig();
                        }));

        addDrawableChild(new Button(this.width / 2 - 50, this.height / 4 + 172, 100, 20,
                Text.literal("Close"),
                button -> client.setScreen(parent)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 4 - 30, 0xffffff);
    }
}
