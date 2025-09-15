package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.atreeimport.ImportAtree;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ContainerScreen;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import com.wynntils.core.components.Models;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class AtreeScreen extends ContainerScreen<AtreeScreenHandler> {

    public static final Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfea\ue000");
    public static final ScreenManager.ScreenInstanceCreater<AtreeScreenHandler, AtreeScreen> CREATOR =
            (handler1, inventory, title1) -> new AtreeScreen(new AtreeScreenHandler(handler1.syncId, inventory, handler1.getInventory()), inventory, title1);

    public AtreeScreen(AtreeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        renderSaveButtons();
    }

    public void renderSaveButtons() {
        AtomicInteger i = new AtomicInteger();
        ImportAtree.getBuilds().stream().filter(save -> save.getCast() == Models.Character.getClassType())
                .forEach(build -> addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.START, 100, 20, 0, i.getAndAdd(20),
                        Text.literal("Load ").append(build.getName())
                                .styled(style -> style.withStrikethrough(true))
                        ,
                        button -> {
                            this.close();
                            WynnBuild.message(Text.literal("Latest wynncraft update is not yet supported by this feature"));
                            //ImportAtree.applyBuild(build.getName(), this);
                        },
                        () -> WynnBuild.getConfigManager().getConfig().isShowTreeLoader())));
    }

}
