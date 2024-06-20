package com.gertoxq.quickbuild.screens;

import com.gertoxq.quickbuild.Cast;
import com.gertoxq.quickbuild.IDS;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.Map;

import static com.gertoxq.quickbuild.client.QuickBuildClient.getLoreFromItemStack;
import static com.gertoxq.quickbuild.client.QuickBuildClient.removeFormat;

public class CharacterInfoScreen extends BuilderScreen {
    public CharacterInfoScreen(GenericContainerScreen screen) {
        super(screen);
    }

    public Map<IDS, Integer> getStats() {
        var slots = handler.slots;
        Map<IDS, Integer> stats = IDS.createStatMap();
        IDS.getStatContainerMap().forEach((slot, id) -> {
            if (slots.get(slot).getStack() == null) return;
            var idVal = getLoreFromItemStack(slots.get(slot).getStack());
            if (idVal == null) return;
            //System.out.println(Arrays.toString(idVal.toArray()));
            int point = 0;
            try {                                       //Bc lore is longer on intel
                point = Integer.parseInt(removeFormat(idVal.get(id == IDS.INTELLIGENCE ? 4 : 3).getSiblings().get(1).getString().replace(" points", "")));
            } catch (Exception ignored) {
                //System.out.println("ERR while parsing stat point");
            }
            stats.put(id, point);
        });
        return stats;
    }

    public Cast getCast() {
        var lore = getLoreFromItemStack(handler.slots.get(7).getStack());
        if (lore == null) {
            System.out.println("Couldn't find cast value");
            return null;
        }
        return Cast.find(lore.get(4).getSiblings().get(1).getString().toUpperCase());
    }
}
