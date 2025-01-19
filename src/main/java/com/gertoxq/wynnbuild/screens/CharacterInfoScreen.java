package com.gertoxq.wynnbuild.screens;

import com.gertoxq.wynnbuild.Cast;
import com.gertoxq.wynnbuild.SP;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.Arrays;
import java.util.Map;

import static com.gertoxq.wynnbuild.util.Utils.getLore;
import static com.gertoxq.wynnbuild.util.Utils.removeFormat;

public class CharacterInfoScreen extends BScreen {
    public CharacterInfoScreen(GenericContainerScreen screen) {
        super(screen);
    }

    public Map<SP, Integer> getStats() {
        var slots = handler.slots;
        Map<SP, Integer> stats = SP.createStatMap();
        SP.getStatContainerMap().forEach((slot, id) -> {
            if (slots.get(slot).getStack() == null) return;
            var idVal = getLore(slots.get(slot).getStack());
            if (idVal == null) return;
            //System.out.println(Arrays.toString(idVal.toArray()));
            int point = 0;
            try {                                       //Bc lore is longer on intel
                point = Integer.parseInt(removeFormat(idVal.get(id == SP.INTELLIGENCE ? 4 : 3).getSiblings().get(1).getString().replace(" points", "")));
            } catch (Exception e) {
                System.out.println("ERR while parsing stat point");
                e.printStackTrace();
            }
            stats.put(id, point);
        });
        return stats;
    }

    public Cast getCast() {
        var lore = getLore(handler.slots.get(7).getStack());
        if (lore == null) {
            System.out.println("Couldn't find cast value");
            return null;
        }
        return Cast.find(removeFormat(lore.get(4).getString().replace("Class: ", "").strip()));
    }

    public int getLevel() {
        var lore = getLore(handler.slots.get(7).getStack());
        if (lore == null) {
            System.out.println("Couldn't find level");
            return 1;
        }
        try {
            return Integer.parseInt(Arrays.stream(removeFormat(lore.get(3).getString().strip()).split(": ")).toList().getLast().strip());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Couldn't parse level");
        }
        return 1;
    }
}
