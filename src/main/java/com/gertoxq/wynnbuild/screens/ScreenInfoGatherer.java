package com.gertoxq.wynnbuild.screens;

import com.gertoxq.wynnbuild.config.ConfigType;
import com.gertoxq.wynnbuild.screens.aspect.AspectInfo;
import com.gertoxq.wynnbuild.screens.tome.TomeScreen;
import com.gertoxq.wynnbuild.screens.tome.TomeScreenHandler;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.mc.event.ScreenOpenedEvent;
import com.wynntils.models.containers.containers.AspectsContainer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.neoforged.bus.api.SubscribeEvent;

import static com.gertoxq.wynnbuild.WynnBuild.*;
import static com.gertoxq.wynnbuild.WynnBuild.getConfigManager;

public class ScreenInfoGatherer {

    @SubscribeEvent
    public void onScreenOpen(ScreenOpenedEvent.Post event) {
        if (Models.Container.getCurrentContainer() instanceof AspectsContainer) {
            Utils.catchNotLoaded(() -> {
                aspects = AspectInfo.getAspects(((GenericContainerScreen) event.getScreen()).getScreenHandler().getStacks());
                getConfigManager().getConfig().setAspects(aspects.stream().map(ConfigType.SavedAspect::fromAspect).toList());
                getConfigManager().saveConfig();
            });
        } else if (TomeScreen.TITLE_PATTERN.matcher(event.getScreen().getTitle().getString()).matches()) {
            Utils.catchNotLoaded(() -> {
                tomeIds = TomeScreenHandler.getTomeIds(((GenericContainerScreen) event.getScreen()).getScreenHandler().getStacks());
                getConfigManager().getConfig().setTomeIds(tomeIds);
                getConfigManager().saveConfig();
            });
        }
    }

    @SubscribeEvent
    public void onScreenClick(ContainerClickEvent event) {
        if (Models.Container.getCurrentContainer() instanceof AspectsContainer) {
            Utils.catchNotLoaded(() -> {
                aspects = AspectInfo.getAspects((event.getContainerMenu().getStacks()));
                getConfigManager().getConfig().setAspects(aspects.stream().map(ConfigType.SavedAspect::fromAspect).toList());
                getConfigManager().saveConfig();
            });
        } else if (TomeScreen.TITLE_PATTERN.matcher(client.currentScreen.getTitle().getString()).matches()) {
            Utils.catchNotLoaded(() -> {
                tomeIds = TomeScreenHandler.getTomeIds((event.getContainerMenu().getStacks()));
                getConfigManager().getConfig().setTomeIds(tomeIds);
                getConfigManager().saveConfig();
            });
        }
    }
}
