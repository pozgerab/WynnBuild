package com.gertoxq.wynnbuild.screens.charinfo;

import com.gertoxq.wynnbuild.Cast;
import com.gertoxq.wynnbuild.SP;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Arrays;
import java.util.Map;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;
import static com.gertoxq.wynnbuild.util.Utils.getLore;
import static com.gertoxq.wynnbuild.util.Utils.removeFormat;

public class CharacterInfoScreenHandler extends ContainerScreenHandler {
    public CharacterInfoScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 3);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (SP.getStatContainerMap().containsKey(slotIndex)) {
            new Task(this::saveCharInfo, 2);
        }
    }

    public void saveCharInfo() {
        Utils.catchNotLoaded(() -> {
            wynnLevel = getLevel();
            stats = getStats();
            cast = getCast();
            configManager.getConfig().setCast(cast.name);
            configManager.saveConfig();
            currentDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
            castTreeObj = fullatree.get(cast.name).getAsJsonObject();
        });
    }

    public Map<SP, Integer> getStats() {
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
        var lore = getLore(slots.get(7).getStack());
        if (lore == null) {
            System.out.println("Couldn't find cast value");
            return null;
        }
        return Cast.find(removeFormat(lore.get(4).getString().replace("Class: ", "").strip()));
    }

    public int getLevel() {
        var lore = getLore(slots.get(7).getStack());
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
