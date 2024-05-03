package com.gertoxq.quickbuild.atreeimport;

import com.gertoxq.quickbuild.AtreeCoder;
import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.gertoxq.quickbuild.config.ConfigType;
import com.gertoxq.quickbuild.config.Manager;
import com.gertoxq.quickbuild.config.SavedBuildType;
import com.gertoxq.quickbuild.screens.AtreeScreen;
import com.gertoxq.quickbuild.util.Task;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.quickbuild.client.QuickBuildClient.*;

public class ImportAtree {

    private static final Manager configManager = QuickBuildClient.getConfigManager();
    private static final ConfigType config = configManager.getConfig();

    public static void addBuild(String name, String code) {

        config.getSavedAtrees().add(new SavedBuildType(name, code, cast));
        configManager.saveConfig();
    }
    public static List<SavedBuildType> getBuilds() {
        return config.getSavedAtrees();
    }

    private static Runnable traverse(AtreeScreen screen, Set<Integer> applyIds, AtomicInteger counter, int max) {
        if (counter.getAndIncrement() >= max) return () -> allowClick.set(true);
        return () -> {
            var idSlots = screen.getAllUpgradedIdsWithSlots();
            System.out.println(Arrays.toString(idSlots.entrySet().toArray()));
            AtomicInteger j = new AtomicInteger(0);
            var unsorted = applyIds.stream().filter(idSlots::containsKey).toList();
            var sortedAbils = new Atrouter(new HashSet<>(unsorted)).findRoute();
            System.out.println(Arrays.toString(sortedAbils.toArray()));
            sortedAbils.forEach(id -> {
                new Task(() -> screen.getClicker().click(idSlots.get(id)),j.get() * 15 + 2);
                j.addAndGet(1);
            });
            new Task(() -> screen.getClicker().scrollAtree(1), sortedAbils.size()*15+10)
                    .then(traverse(screen, applyIds, counter, max),5);
        };
    }

    static AtomicBoolean allowClick = new AtomicBoolean(true);

    public static void applyBuild(String name, AtreeScreen screen) {
        SavedBuildType build = getBuilds().stream().filter(savedBuildType -> cast == savedBuildType.getCast() && Objects.equals(name, savedBuildType.getName())).findFirst().orElse(null);
        if (build == null) {
            client.player.sendMessage(Text.literal("Build not found, something went wrong"));
            return;
        }
        Set<Integer> applyIds = AtreeCoder.decode_atree(build.getValue());
        System.out.println(Arrays.toString(applyIds.toArray()));
        allowClick.set(false);
        ScreenMouseEvents.allowMouseClick(screen.getScreen()).register((screen1, mouseX, mouseY, button) -> allowClick.get());
        screen.getClicker().scrollAtree(-7); // Wait for scroll finish
        new Task(traverse(screen, applyIds, new AtomicInteger(0), 7), 16);
    }

}
