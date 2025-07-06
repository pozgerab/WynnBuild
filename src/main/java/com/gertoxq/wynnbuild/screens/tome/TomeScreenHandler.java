package com.gertoxq.wynnbuild.screens.tome;

import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.getConfigManager;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.tomeIds;
import static com.gertoxq.wynnbuild.util.Utils.removeFormat;

public class TomeScreenHandler extends ContainerScreenHandler {

    public static final List<Integer> EMPTY_IDS = List.of(61, 61, 62, 62, 62, 62, 63, 93);
    public static List<Integer> tomeSlots = List.of(11, 19, 22, 30, 31, 32, 4, 49);

    public TomeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 6);
    }

    public void saveTomeInfo() {
        Utils.catchNotLoaded(() -> {
            tomeIds = getIds();
            getConfigManager().getConfig().setTomeIds(tomeIds);
            getConfigManager().saveConfig();
        });
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        new Task(this::saveTomeInfo, 2);
    }

    public List<String> getTomeNames() {
        return tomeSlots.stream().map(index -> removeFormat(slots.get(index).getStack().getName().getString())).toList();
    }

    public List<Integer> getIds() {
        List<String> names = getTomeNames();
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (WynnData.getTomeMap().containsKey(name)) {
                ids.add(WynnData.getTomeMap().getOrDefault(name, EMPTY_IDS.get(i)));
            } else {
                //  idk why some tomes have a symbol at the end but substring them then
                ids.add(WynnData.getTomeMap().getOrDefault(name.substring(0, name.length() - 1), EMPTY_IDS.get(i)));
            }
        }
        return ids;
    }
}
