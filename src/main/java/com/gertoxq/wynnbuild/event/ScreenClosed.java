package com.gertoxq.wynnbuild.event;

import com.wynntils.mc.event.MenuEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ScreenClosed {

    public static void processContainerClose() {
    }

    @SubscribeEvent
    public void screenOpenedPre(MenuEvent.MenuOpenedEvent.Pre event) {

        processContainerClose();
    }
}
