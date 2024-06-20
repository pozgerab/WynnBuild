package com.gertoxq.quickbuild.screens;

import com.gertoxq.quickbuild.AtreeCoder;
import com.gertoxq.quickbuild.atreeimport.ImportAtree;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.quickbuild.client.QuickBuildClient.cast;
import static com.gertoxq.quickbuild.client.QuickBuildClient.getConfigManager;

public class ImportAtreeScreen extends Screen {
    private final Screen parent;

    public ImportAtreeScreen(Screen parent) {
        super(Text.literal("Import atree"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
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
                client.execute(this::init);
            }));
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Import Ability Tree"), width / 2, height / 4, 0xffffff);
        super.render(context, mouseX, mouseY, delta);
    }
}
