package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.screens.tome.TomeQuery;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.HashSet;

public class WorldChangeTreeRefresh {

    @SubscribeEvent
    public void onWorldChange(WorldStateEvent event) {
        if (event.getNewState() == WorldState.WORLD) {
            Ability.refreshTree();
            WynnBuild.atreeState = new HashSet<>();
            WynnBuild.tomeIds = TomeQuery.EMPTY_IDS;
            new TomeQuery().queryTomeInfo();
        }
    }
}
