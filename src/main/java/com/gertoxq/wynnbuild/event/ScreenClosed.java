package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.screens.tome.TomeQuery;
import com.google.common.collect.ImmutableList;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.mc.event.MenuEvent;
import com.wynntils.models.containers.containers.MasteryTomesContainer;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.neoforged.bus.api.SubscribeEvent;

public class ScreenClosed {

    public static void processContainerClose() {
        if (Models.Container.getCurrentContainer() instanceof MasteryTomesContainer) {
            onTomeContainerClose();
        }
    }

    private static void onTomeContainerClose() {
        ContainerContent content = new ContainerContent(ImmutableList.copyOf(McUtils.containerMenu().getStacks()),
                Text.empty(), ScreenHandlerType.GENERIC_9X6, McUtils.containerMenu().syncId);
        new TomeQuery().processTomes(content);
    }

    @SubscribeEvent
    public void screenOpenedPre(MenuEvent.MenuOpenedEvent.Pre event) {

        processContainerClose();
    }
}
