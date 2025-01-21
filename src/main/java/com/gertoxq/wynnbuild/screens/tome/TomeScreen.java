package com.gertoxq.wynnbuild.screens.tome;

import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ContainerScreen;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import com.gertoxq.wynnbuild.util.Task;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.getConfigManager;

public class TomeScreen extends ContainerScreen<TomeScreenHandler> {

    public static final Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfdb\ue005");
    public static final ScreenManager.ScreenInstanceCreater<TomeScreenHandler, TomeScreen> CREATOR =
            (handler1, inventory, title1) -> new TomeScreen(new TomeScreenHandler(handler1.syncId, inventory, handler1.getInventory()), inventory, title1);

    public TomeScreen(TomeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        new Task(() -> getScreenHandler().saveTomeInfo(), 2);
        addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, 0, 0,
                Text.literal("Read"),
                button -> getScreenHandler().saveTomeInfo(),
                () -> getConfigManager().getConfig().isShowButtons()));
    }
}
