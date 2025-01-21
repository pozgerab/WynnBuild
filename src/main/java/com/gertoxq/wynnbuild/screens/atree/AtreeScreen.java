package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.atreeimport.ImportAtree;
import com.gertoxq.wynnbuild.screens.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;
import static com.gertoxq.wynnbuild.screens.atree.AtreeScreenHandler.tempDupeMap;

public class AtreeScreen extends ContainerScreen<AtreeScreenHandler> {

    public static final Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfea\ue000");
    public static final ScreenManager.ScreenInstanceCreater<AtreeScreenHandler, AtreeScreen> CREATOR =
            (handler1, inventory, title1) -> new AtreeScreen(new AtreeScreenHandler(handler1.syncId, inventory, handler1.getInventory()), inventory, title1);
    public static AtreeScreen CURRENT_ATREE_SCREEN;

    private TextWidget messageField;
    private boolean isMessageVisible = false;
    private Button saveCurrBtn;

    public AtreeScreen(AtreeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        CURRENT_ATREE_SCREEN = this;
        tempDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
    }

    public void setMessage(Text message) {
        messageField.setMessage(message);
    }

    public void setMessageVisible(boolean messageVisible) {
        isMessageVisible = messageVisible;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (isMessageVisible) messageField.render(context, mouseX, mouseY, delta);
        if (getScreenHandler().readCurrent) saveCurrBtn.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        messageField = new TextWidget(width / 2 - 150, 40, 300, 20, Text.empty(), textRenderer).alignCenter();
        addSelectableChild(saveCurrBtn = new Button(width / 3 * 2, 70, 100, 20, Text.literal("Save Current"), button -> {
            if (getScreenHandler().readCurrent) {
                this.close();
                client.execute(() -> client.setScreen(new ImportAtreeScreen(null, null, atreeSuffix)));
            }
        }));
        addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.END,
                100, 20, 0, 0,
                Text.literal("Read"),
                button -> getScreenHandler().startAtreead(),
                () -> getConfigManager().getConfig().isShowButtons()));

        renderSaveButtons();
    }

    public void renderSaveButtons() {
        AtomicInteger i = new AtomicInteger();
        ImportAtree.getBuilds().stream().filter(save -> save.getCast() == cast)
                .forEach(build -> addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.START, 100, 20, 0, i.getAndAdd(20),
                        Text.literal("Load ").append(build.getName()),
                        button -> ImportAtree.applyBuild(build.getName(), this),
                        () -> getConfigManager().getConfig().isShowTreeLoader())));
    }

}
