package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.atree.AtreeNode;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.mc.event.ContainerSetSlotEvent;
import com.wynntils.mc.event.MenuEvent;
import net.minecraft.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DebugMenuOpen {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMenuOpen(MenuEvent.MenuOpenedEvent.Pre event) {
        WynnBuild.debugClient(event.getTitle().getString());
        WynnBuild.debugClient(Utils.escapeToUnicode(event.getTitle().getString()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMenuSet(ContainerSetContentEvent.Pre event) {

        List<String> names = new ArrayList<>();
        for (int i = 0; i < event.getItems().size(); i++) {
            ItemStack itemStack = event.getItems().get(i);
            if (!AtreeNode.isValidNode(itemStack, i)) continue;
            names.add(i + ": " + itemStack.getName().getString());
        }

        WynnBuild.debugClient(Arrays.toString(names.toArray()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onContainerSetSlot(ContainerSetSlotEvent.Pre e) {
        if (!e.getItemStack().isEmpty()) {
            WynnBuild.debugClient("SLOT SET " + e.getSlot() + " " + e.getItemStack().getName().getString());
        }
    }
}
