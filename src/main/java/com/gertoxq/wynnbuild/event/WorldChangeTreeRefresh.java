package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.aspect.AspectInfo;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.webquery.Providers;
import com.wynntils.core.components.Models;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.HashSet;

public class WorldChangeTreeRefresh {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldChange(WorldStateEvent event) {
        if (event.getNewState() == WorldState.WORLD) {
            Ability.refreshTree();
            AspectInfo.aspectMap = Providers.Aspects.getClassAspects(Models.Character.getClassType());
            WynnBuild.atreeState = WynnBuild.getCachedAtree().map(treeCode -> WynnBuild.getAtreeCoder().decode_atree(treeCode)).orElse(new HashSet<>());
            WynnBuild.tomeIds = null;
            WynnBuild.aspects = null;
        }
    }
}
