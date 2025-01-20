package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.atreeimport.ImportAtree;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;
import static com.gertoxq.wynnbuild.screens.atree.AtreeScreenHandler.tempDupeMap;

public class AtreeScreen extends ContainerScreen<AtreeScreenHandler> {

    public static final Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfea\ue000");
    public static AtreeScreen CURRENT_ATREE_SCREEN;

    public AtreeScreen(AtreeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        CURRENT_ATREE_SCREEN = this;
        renderSaveButtons();
        tempDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, 0, 0,
                Text.literal("Read"),
                button -> getScreenHandler().startAtreead(),
                () -> configManager.getConfig().isShowButtons()));
    }

    public void renderSaveButtons() {
        AtomicInteger i = new AtomicInteger();
        ImportAtree.getBuilds().stream().filter(save -> save.getCast() == cast)
                .forEach(build -> addDrawable(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.START, 100, 20, 0, i.getAndAdd(20),
                        Text.literal("Load ").append(build.getName()),
                        button -> ImportAtree.applyBuild(build.getName(), this),
                        () -> configManager.getConfig().isShowTreeLoader())));
    }

}
