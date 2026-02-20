package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.screen.slot.SlotActionType;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.regex.Pattern;

public class AtreeReset {

    Pattern ATREE_RESET_PATTERN = Pattern.compile("\uDAFF\uDFEA\uE001");
    int resetSlot = 22;

    @SubscribeEvent
    public void onClick(ContainerClickEvent event) {
        if (McUtils.mc().currentScreen != null
                && ATREE_RESET_PATTERN.matcher(McUtils.mc().currentScreen.getTitle().getString()).matches()
                && event.getSlotNum() == resetSlot
                && event.getMouseButton() == 0 // Left click
                && event.getClickType() == SlotActionType.PICKUP) {

            WynnBuild.atreeState.clear();
            WynnBuild.saveAtreeCache();
            WynnBuild.debug("Atree state reset");
        }

    }
}
