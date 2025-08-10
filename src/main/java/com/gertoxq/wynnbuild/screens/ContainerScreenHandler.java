package com.gertoxq.wynnbuild.screens;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

import static com.gertoxq.wynnbuild.WynnBuild.client;

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
        client.getNetworkHandler().sendPacket(
                new ClickSlotC2SPacket(syncId, 0, index, 0, SlotActionType.PICKUP, Items.AIR.getDefaultStack(), new Int2ObjectArrayMap<>())
        );
    }

    public void rightClickSlot(int index) {
        client.getNetworkHandler().sendPacket(
                new ClickSlotC2SPacket(syncId, 0, index, 1, SlotActionType.PICKUP, Items.AIR.getDefaultStack(), new Int2ObjectArrayMap<>())
        );
    }
}
