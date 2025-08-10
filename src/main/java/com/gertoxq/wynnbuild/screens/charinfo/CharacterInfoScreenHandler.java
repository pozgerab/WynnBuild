package com.gertoxq.wynnbuild.screens.charinfo;

import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class CharacterInfoScreenHandler extends ContainerScreenHandler {
    public CharacterInfoScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 3);
    }
}
