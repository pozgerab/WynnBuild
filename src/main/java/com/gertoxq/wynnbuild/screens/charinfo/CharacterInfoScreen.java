package com.gertoxq.wynnbuild.screens.charinfo;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ContainerScreen;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.WynnBuild.getConfigManager;

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
        addDrawableChild(createButton(Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, 0, 0,
                Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)),
                button -> {
                    this.close();
                    client.execute(WynnBuild::build);
                },
                () -> getConfigManager().getConfig().isShowButtons()));
    }
}
