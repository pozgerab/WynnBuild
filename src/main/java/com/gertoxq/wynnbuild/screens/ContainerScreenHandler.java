package com.gertoxq.wynnbuild.screens;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;

import static com.gertoxq.wynnbuild.WynnBuild.client;

public class ContainerScreenHandler extends GenericContainerScreenHandler {
    public ContainerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, rows);
    }

    public void leftClickSlot(short index) {
        client.getNetworkHandler().sendPacket(
                new ClickSlotC2SPacket(syncId, 0, index, (byte) 0, SlotActionType.PICKUP, new Int2ObjectArrayMap<>(), ItemStackHash.EMPTY)
        );
    }

}
