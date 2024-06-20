package com.gertoxq.quickbuild.util;

import com.gertoxq.quickbuild.GuiSlot;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import static com.gertoxq.quickbuild.client.QuickBuildClient.client;

public class ScreenClicker {
    private final HandledScreen<?> screen;

    public ScreenClicker(HandledScreen<?> screen) {
        this.screen = screen;
    }

    public void click(int slot) {
        try {
            assert client.player != null;
            client.player.networkHandler.sendPacket(
                    new ClickSlotC2SPacket(this.screen.getScreenHandler().syncId, this.screen.getScreenHandler().getRevision(), slot, 0, SlotActionType.PICKUP, new ItemStack(Items.AIR), new Int2ObjectArrayMap<>()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollAtree(int amount) {
        for (int i = 0; i < Math.abs(amount); i++) {
            new Task(() -> client.execute(() -> this.click(amount > 0 ? GuiSlot.ATREE_UP.slot : GuiSlot.ATREE_DOWN.slot)), i * 2);
        }
    }
}
