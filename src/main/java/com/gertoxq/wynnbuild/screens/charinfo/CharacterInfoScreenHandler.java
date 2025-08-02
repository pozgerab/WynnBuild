package com.gertoxq.wynnbuild.screens.charinfo;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.base.sp.SkillpointList;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Arrays;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.castTreeObj;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.fullatree;
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
            WynnBuild.wynnLevel = getLevel();
            WynnBuild.stats = getStats();
            Cast prevCast = WynnBuild.cast;
            WynnBuild.cast = getCast();
            WynnBuild.getConfigManager().getConfig().setCast(WynnBuild.cast.name);
            WynnBuild.getConfigManager().saveConfig();
            if (!WynnBuild.cast.equals(prevCast)) {
                castTreeObj = fullatree.get(WynnBuild.cast.name).getAsJsonObject();
                if (WynnBuild.cast.equals(prevCast)) {
                    Ability.refreshTree();
                }
            }
        });
    }

    public SkillpointList getStats() {
        SkillpointList stats = SkillpointList.empty();
        SP.getStatContainerMap().forEach((slot, id) -> {
            if (slots.get(slot).getStack() == null) return;
            var idVal = getLore(slots.get(slot).getStack());
            if (idVal == null) return;
            int point = 0;
            try {                                       //Bc lore is longer on intel
                point = Integer.parseInt(removeFormat(idVal.get(id == SP.INTELLIGENCE ? 4 : 3).getSiblings().get(1).getString().replace(" points", "")));
            } catch (Exception e) {
                WynnBuild.error("ERR while parsing stat point: {}", e);
            }
            stats.set(id.ordinal(), point);
        });
        return stats;
    }

    public Cast getCast() {
        var lore = getLore(slots.get(7).getStack());
        if (lore == null) {
            WynnBuild.error("Couldn't find cast value and lore was null");
            return null;
        }
        return Cast.find(removeFormat(lore.get(4).getString().replace("Class: ", "").strip())).get();
    }

    public int getLevel() {
        var lore = getLore(slots.get(7).getStack());
        if (lore == null) {
            WynnBuild.error("Couldn't find level and lore was null");
            return 1;
        }
        try {
            return Integer.parseInt(Arrays.stream(removeFormat(lore.get(3).getString().strip()).split(": ")).toList().getLast().strip());
        } catch (NumberFormatException e) {
            WynnBuild.error("Couldn't parse level: {}", e);
        }
        return 1;
    }
}
