package com.gertoxq.wynnbuild.screens.tome;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.game.TomeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gertoxq.wynnbuild.WynnBuild.*;

public class TomeScreenHandler extends ContainerScreenHandler {

    public static final int EMPTY_ID = -1;
    public static final List<Integer> EMPTY_IDS = Collections.nCopies(14, EMPTY_ID);
    public static final List<Integer> tomeSlots = List.of(11, 19, 22, 30, 31, 32, 4, 49, 15, 25, 28, 38, 34, 42);

    public TomeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 6);
    }

    public void saveTomeInfo() {
        Utils.catchNotLoaded(() -> {
            getConfigManager().getConfig().setTomeIds(tomeIds);
            getConfigManager().saveConfig();
        });
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        client.execute(this::saveTomeInfo);
        WynnBuild.message(Text.literal("Saved Tome Info")); // TODO: REMOVE
    }

    public static List<Integer> getTomeIds(List<ItemStack> items) {
        List<Integer> ids = new ArrayList<>();
        tomeSlots.forEach(index -> {
            Optional<TomeItem> optionalTome = Models.Item.asWynnItem(items.get(index), TomeItem.class);
            if (optionalTome.isEmpty()) {
                ids.add(EMPTY_ID);
                return;
            }
            if (WynnData.getTomeMap().containsKey(optionalTome.get().getName())) {
                ids.add(WynnData.getTomeMap().get(optionalTome.get().getName()));
            } else {
                WynnBuild.warn("Tome not found: " + optionalTome.get().getName());
            }
        });
        return ids;
    }
}
