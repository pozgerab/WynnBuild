package com.gertoxq.wynnbuild.atreeimport;

import com.gertoxq.wynnbuild.AtreeCoder;
import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.config.SavedBuildType;
import com.gertoxq.wynnbuild.screens.AtreeScreen;
import com.gertoxq.wynnbuild.util.Task;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;

public class ImportAtree {

    private static final Manager configManager = WynnBuildClient.getConfigManager();
    static AtomicBoolean allowClick = new AtomicBoolean(true);

    public static void addBuild(String name, String code) {

        configManager.getConfig().getSavedAtrees().add(new SavedBuildType(name, code, cast));
        configManager.saveConfig();
    }

    public static List<SavedBuildType> getBuilds() {
        return configManager.getConfig().getSavedAtrees();
    }

    private static Runnable traverse(AtreeScreen screen, Set<Integer> applyIds, AtomicInteger counter, int max) {
        if (counter.getAndIncrement() >= max) return () -> allowClick.set(true);
        return () -> {
            Map<Integer, Integer> idSlots = new HashMap<>();
            screen.getSlots().forEach(abilSlot -> idSlots.put(abilSlot.id(), abilSlot.slot().getIndex()));
            AtomicInteger j = new AtomicInteger(0);
            var unsorted = applyIds.stream().filter(idSlots::containsKey).toList();
            var sortedAbils = new Atrouter(new HashSet<>(unsorted), castTreeObj).findRoute();
            sortedAbils.forEach(id -> {
                new Task(() -> screen.getClicker().click(idSlots.get(id)), j.get() * 15 + 2);
                j.addAndGet(1);
            });
            new Task(() -> screen.getClicker().scrollAtree(1), sortedAbils.size() * 15 + 10)
                    .then(traverse(screen, applyIds, counter, max), 5);
        };
    }

    public static void applyBuild(String name, AtreeScreen screen) {
        SavedBuildType build = getBuilds().stream().filter(savedBuildType -> cast == savedBuildType.getCast() && Objects.equals(name, savedBuildType.getName())).findFirst().orElse(null);
        if (build == null) {
            client.player.sendMessage(Text.literal("Build not found, something went wrong"));
            return;
        }
        Set<Integer> applyIds = AtreeCoder.decode_atree(build.getValue());
        allowClick.set(false);
        ScreenMouseEvents.allowMouseClick(screen.getScreen()).register((screen1, mouseX, mouseY, button) -> allowClick.get());
        screen.getClicker().scrollAtree(-7); // Wait for scroll finish
        AtreeScreen.resetReader();
        new Task(traverse(screen, applyIds, new AtomicInteger(0), 7), 7 * 4);
    }

}
