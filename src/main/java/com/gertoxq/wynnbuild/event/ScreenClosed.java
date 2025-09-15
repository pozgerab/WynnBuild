package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.screens.aspect.AspectInfo;
import com.gertoxq.wynnbuild.screens.tome.TomeQuery;
import com.google.common.collect.ImmutableList;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.mc.event.MenuEvent;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.models.containers.containers.AspectsContainer;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.neoforged.bus.api.SubscribeEvent;

public class ScreenClosed {

    @SubscribeEvent
    public void screenOpenedPre(MenuEvent.MenuOpenedEvent.Pre event) {

        if (Models.Container.getCurrentContainer() instanceof AspectsContainer aspectsContainer) {
            ContainerContent content = new ContainerContent(ImmutableList.copyOf(McUtils.containerMenu().getStacks()),
                    Text.empty(), ScreenHandlerType.GENERIC_9X6, aspectsContainer.getContainerId());
            if (new AspectInfo().verifyValidAspectContainer(content)) {
                new AspectInfo().processAspects(content);
            }
        } else if (McUtils.mc().currentScreen != null
                && ContainerModel.MASTERY_TOMES_NAME.matches(McUtils.mc().currentScreen.getTitle().getString())) {
            ContainerContent content = new ContainerContent(ImmutableList.copyOf(McUtils.containerMenu().getStacks()),
                    Text.empty(), ScreenHandlerType.GENERIC_9X6, McUtils.containerMenu().syncId);
            new TomeQuery().processTomes(content);
        }
    }
}
