package com.gertoxq.quickbuild.screens;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

import java.util.ArrayList;
import java.util.List;

import static com.gertoxq.quickbuild.client.QuickBuildClient.removeFormat;
import static com.gertoxq.quickbuild.client.QuickBuildClient.tomeMap;

public class TomeScreen extends BuilderScreen {

    public static final List<Integer> EMPTY_IDS = List.of(61, 61, 62, 62, 62, 62, 63, 93);
    public static List<Integer> tomeSlots = List.of(11, 19, 22, 30, 31, 32, 4, 49);

    public TomeScreen(GenericContainerScreen screen) {
        super(screen);
    }

    public List<String> getTomeNames() {
        return tomeSlots.stream().map(index -> removeFormat(handler.slots.get(index).getStack().getName().getString())).toList();
    }

    public List<Integer> getIds() {
        List<String> names = getTomeNames();
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            ids.add(tomeMap.getOrDefault(name, EMPTY_IDS.get(i)));
        }
        return ids;
    }
}
