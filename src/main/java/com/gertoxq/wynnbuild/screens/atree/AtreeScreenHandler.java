package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AtreeScreenHandler extends ContainerScreenHandler {
    static final Set<Integer> readCache = new HashSet<>();

    public boolean readCurrent = false;

    public AtreeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 6);
    }

    public static void resetReader() {
        readCache.clear();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

    }

    public List<AtreeNode> getSlots() {
        return slots.stream().map(AtreeNode::new).filter(node -> node.getId().map(integer -> {
            readCache.add(integer);
            return true;
        }).orElse(false)).toList();
    }

    public void scrollAtree(int amount) {
        for (int i = 0; i < Math.abs(amount); i++) {
            new Task(() -> WynnBuild.client.execute(() -> this.leftClickSlot(amount > 0 ? AbilityTreeQuery.NEXT_PAGE_SLOT : AbilityTreeQuery.PREVIOUS_PAGE_SLOT)), i * 4);
        }
    }
}
