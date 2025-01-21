package com.gertoxq.wynnbuild.screens.charinfo;

import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ContainerScreen;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import com.gertoxq.wynnbuild.util.Task;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.build;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.getConfigManager;

public class CharacterInfoScreen extends ContainerScreen<CharacterInfoScreenHandler> {

    public static final Pattern TITLE_PATTERN = Pattern.compile("\udaff\udfdc\ue003");
    public static final ScreenManager.ScreenInstanceCreater<CharacterInfoScreenHandler, CharacterInfoScreen> CREATOR =
            (handler1, inventory, title) -> new CharacterInfoScreen(new CharacterInfoScreenHandler(handler1.syncId, inventory, handler1.getInventory()), inventory, title);

    public CharacterInfoScreen(CharacterInfoScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        new Task(() -> getScreenHandler().saveCharInfo(), 2);

        addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, 0, -20,
                Text.literal("Read"),
                button -> getScreenHandler().saveCharInfo(),
                () -> getConfigManager().getConfig().isShowButtons()));
        addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, 0, 0,
                Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)),
                button -> build(),
                () -> getConfigManager().getConfig().isShowButtons()));
    }
}
