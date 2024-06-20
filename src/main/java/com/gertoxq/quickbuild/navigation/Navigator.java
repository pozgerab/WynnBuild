package com.gertoxq.quickbuild.navigation;

import com.gertoxq.quickbuild.screens.BuilderScreen;
import com.gertoxq.quickbuild.util.Task;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gertoxq.quickbuild.client.QuickBuildClient.client;

public class Navigator {

    private final NavPage from;
    private final NavPage to;
    private final List<NavPage> route = new ArrayList<>();
    private int navState = 0;

    public Navigator(NavPage from, NavPage to) {
        this.from = from;
        this.to = to;
        if (getNavMap().get(from).containsKey(to)) {
            route.add(to);
        } else {
            route.add(NavPage.CHARINFO);
            route.add(to);
        }
    }

    public static Map<NavPage, Map<NavPage, Integer>> getNavMap() {
        return Map.of(
                NavPage.CHARINFO, Map.of(NavPage.ATREE, 9, NavPage.TOMES, 8),
                NavPage.ATREE, Map.of(NavPage.CHARINFO, 63),
                NavPage.INVENTORY, Map.of(NavPage.CHARINFO, 6),
                NavPage.TOMES, Map.of(NavPage.CHARINFO, 27)
        );
    }

    public NavPage getFrom() {
        return from;
    }

    public NavPage getTo() {
        return to;
    }

    public void navigate() {
        route.forEach(navPage -> {
            new Task(() -> {
                int state = navState;
                var current = route.get(state);
                int slot = getNavMap().get(current).get(route.get(state + 1));
                new Task(() -> {
                    BuilderScreen currentScreen = new BuilderScreen((HandledScreen<?>) client.currentScreen);
                    currentScreen.getClicker().click(slot);
                }, navState * 4 + 1);
            }, navState * 4);
            navState++;
        });
    }
}
