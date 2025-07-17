package com.gertoxq.wynnbuild.atreeimport;

import com.gertoxq.wynnbuild.AtreeCoder;
import com.gertoxq.wynnbuild.config.SavedBuild;
import com.gertoxq.wynnbuild.screens.atree.AtreeNode;
import com.gertoxq.wynnbuild.screens.atree.AtreeScreen;
import com.gertoxq.wynnbuild.screens.atree.AtreeScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;

public class ImportAtree {

    static AtomicBoolean allowClick = new AtomicBoolean(true);

    public static void addBuild(String name, String code) {

        getConfigManager().getConfig().getSavedAtrees().add(new SavedBuild(name, code, cast));
        getConfigManager().saveConfig();
    }

    public static List<SavedBuild> getBuilds() {
        return getConfigManager().getConfig().getSavedAtrees();
    }

    private static Runnable traverse(AtreeScreen screen, Set<Integer> applyIds, AtomicInteger counter, int max) {
        if (counter.getAndIncrement() >= max) return () -> allowClick.set(true);
        return () -> {
            Map<Integer, Integer> idSlots = new HashMap<>();
            screen.getScreenHandler().getSlots().forEach(atreeNode -> idSlots.put(atreeNode.id, atreeNode.getSlot().getIndex()));
            AtomicInteger j = new AtomicInteger(0);
            var unsorted = applyIds.stream().filter(idSlots::containsKey).toList();
            var sortedAbils = new Atrouter(new HashSet<>(unsorted), applyIds).findRoute();
            sortedAbils.forEach(id -> {
                new Task(() -> screen.getScreenHandler().leftClickSlot(idSlots.get(id)), j.get() * 15 + 2);
                j.addAndGet(1);
            });
            new Task(() -> screen.getScreenHandler().scrollAtree(1), sortedAbils.size() * 15 + 10)
                    .then(traverse(screen, applyIds, counter, max), 5);
        };
    }

    public static void applyBuild(String name, AtreeScreen screen) {
        SavedBuild build = getBuilds().stream().filter(savedBuildType -> cast == savedBuildType.getCast() && Objects.equals(name, savedBuildType.getName())).findFirst().orElse(null);
        if (build == null) {
            displayErr("Build not found, something went wrong");
            return;
        }
        Set<Integer> applyIds = AtreeCoder.decode_atree(build.getCode());
        allowClick.set(false);
        ScreenMouseEvents.allowMouseClick(screen).register((screen1, mouseX, mouseY, button) -> allowClick.get());
        ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, key, scancode, modifiers) -> allowClick.get());
        screen.getScreenHandler().scrollAtree(-7); // Wait for scroll finish
        AtreeScreenHandler.resetReader();
        new Task(() -> {
            AtreeNode firstNode = screen.getScreenHandler().getSlots().getFirst();
            if (firstNode.isUnlockedOrUnreachable()) {
                displayErr("Atree is not empty, reset it first");
                allowClick.set(true);
                return;
            }
            traverse(screen, applyIds, new AtomicInteger(0), 7).run();
        }, 7 * 4);
    }

}
