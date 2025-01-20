package com.gertoxq.wynnbuild.screens;

import com.gertoxq.wynnbuild.AtreeCoder;
import com.gertoxq.wynnbuild.atreeimport.ImportAtree;
import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.config.Manager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.cast;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.getConfigManager;

public class ImportAtreeScreen extends Screen {
    private static final Manager configManager = WynnBuildClient.getConfigManager();
    private final Screen parent;

    public ImportAtreeScreen(Screen parent) {
        super(Text.literal("Import atree"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        getConfigManager().loadConfig();
        var nameInput = new TextFieldWidget(textRenderer, width / 2 - 100, height / 4 + 24, 58, 20, Text.empty());
        nameInput.setPlaceholder(Text.literal("Name"));
        addDrawableChild(nameInput);
        var codeInput = new TextFieldWidget(textRenderer, width / 2 - 39, height / 4 + 24, 138, 20, Text.empty());
        codeInput.setPlaceholder(Text.literal("Wynnbuilder atree code"));
        addDrawableChild(codeInput);
        addDrawableChild(new Button(width / 2 - 100, height / 4 + 44, 200, 20, Text.literal("Save Build"), button -> {
            var code = codeInput.getText();
            if (code.isEmpty()) return;
            if (nameInput.getText().isEmpty()) return;
            try {
                code = AtreeCoder.encode_atree(AtreeCoder.decode_atree(code)).toB64();
                ImportAtree.addBuild(nameInput.getText(), code);
            } catch (Exception e) {
                client.player.sendMessage(Text.literal("Invalid code").styled(style -> style.withColor(Formatting.RED)));
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
                e.printStackTrace();
            }
            init();
        }));
        AtomicInteger i = new AtomicInteger();
        ImportAtree.getBuilds().stream().filter(save -> save.getCast() == cast).forEach(build -> {
            addDrawableChild(new TextWidget(0, i.get() * 35, 80, 20, Text.literal(build.getName()).append(":").append(Text.literal(build.getValue())), textRenderer).alignLeft());
            addDrawableChild(new TextWidget(0, i.get() * 35 + 10, 80, 20, Text.literal("Cast: ").append(Text.literal(build.getCast().name).styled(style -> style.withColor(build.getCast() == cast ? Formatting.GREEN : Formatting.WHITE))), textRenderer).alignLeft());
            addDrawableChild(new Button(80, i.getAndIncrement() * 35 + 5, 50, 15, Text.literal("DEL").styled(style -> style.withColor(Formatting.RED).withBold(true)), button -> {
                getConfigManager().getConfig().getSavedAtrees().remove(build);
                getConfigManager().saveConfig();
                init();
            }));
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Import Ability Tree"), width / 2, height / 4, 0xffffff);
        super.render(context, mouseX, mouseY, delta);
    }
}
