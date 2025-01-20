package com.gertoxq.wynnbuild.screens;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.client;

public class ContainerScreenHandler extends GenericContainerScreenHandler {
    public ContainerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, rows);
    }

    public ItemStack getStack(int index) {
        return slots.get(index).getStack();
    }

    public boolean isEmptySlot(int index) {
        return !slots.get(index).hasStack();
    }

    public void leftClickSlot(int index) {
        client.interactionManager.clickSlot(syncId, index, 0, SlotActionType.PICKUP, client.player);
    }

    public void rightClickSlot(int index) {
        client.interactionManager.clickSlot(syncId, index, 1, SlotActionType.PICKUP, client.player);
    }
}
