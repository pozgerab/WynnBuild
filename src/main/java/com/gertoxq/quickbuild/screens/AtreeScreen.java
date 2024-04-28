package com.gertoxq.quickbuild.screens;

import com.gertoxq.quickbuild.Cast;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.Slot;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.gertoxq.quickbuild.client.QuickBuildClient.*;

public class AtreeScreen extends BuilderScreen {
    public AtreeScreen(GenericContainerScreen screen) {
        super(screen);
    }
    public List<String> getNames() {
        return handler.slots.stream().map(slot -> {
            var name = removeFormat(slot.getStack().getName().getString());
            name = name.replace("Unlock ", "");
            name = name.replace(" ability", "");
            name = removeNum(name);
            return name;
        }).toList();
    }
    public List<Slot> getUnlocked() {
        AtomicBoolean stop = new AtomicBoolean(false);
        return handler.slots.stream().filter(slot -> {
            if (stop.get()) return false;
            var lore = getLoreFromItemStack(slot.getStack());
            if (lore == null) return false;
            if (slot.getIndex() == 53) {
                stop.set(true);
            }
            return removeFormat(lore.get(lore.size() - 1).getString()).equals("You already unlocked this ability");
        }).toList();
    }

    public List<String> getUnlockedNames() {
        return getUnlocked().stream().map(slot -> removeNum(removeFormat(slot.getStack().getName().getString()))).toList();
    }
    public Set<Integer> getUnlockedIds() {
        return getUnlockedNames().stream().map(name -> {
            try {
                return Integer.parseInt(castTreeObj.entrySet().stream().filter(entry -> Objects.equals(name, removeNum(entry.getValue().getAsJsonObject().get("display_name").getAsString()))).findAny().orElse(null).getKey());
            } catch (NullPointerException e) {
                return 0;
            }
        }).collect(Collectors.toSet());
    }
    public Set<Integer> getIds() {
        return getNames().stream().map(name -> {
            try {
                return Integer.parseInt(castTreeObj.entrySet().stream().filter(entry -> Objects.equals(name, removeNum(entry.getValue().getAsJsonObject().get("display_name").getAsString()))).findAny().orElse(null).getKey());
            } catch (NullPointerException e) {
                //System.out.println("ITEM NOT FOUND");
                return 0;
            }
        }).collect(Collectors.toSet());
    }

    public Set<Integer> getUpgradedIds() {
        Set<Integer> unlockedIds = new HashSet<>(getUnlockedIds());
        for (int i = 0; i < 3; i++) { // So tier 2 abilities have a chance to upgrade to tier 3
            Map<Integer, Integer> toReplace = new HashMap<>();
            getUnlockedIds().forEach(id -> {
                if (currentDupeMap.containsKey(id.toString()) && getIds().contains(currentDupeMap.get(id.toString()).getAsJsonArray().get(0).getAsInt())) {
                    toReplace.put(id, currentDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt());
                }
            });
            toReplace.forEach((integer, integer2) -> {
                unlockedIds.remove(integer);
                unlockedIds.add(integer2);
            });
        }
        return unlockedIds;
    }

    public static Set<Integer> fixIds(Cast cast, Set<Integer> unlockedIds, Set<Integer> ids) {
        var tempDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
        for (int i = 0; i < 3; i++) { // So tier 2 abilities have a chance to upgrade to tier 3
            Map<Integer, Integer> toReplace = new HashMap<>();
            unlockedIds.forEach(id -> {
                if (tempDupeMap.containsKey(id.toString()) && ids.contains(tempDupeMap.get(id.toString()).getAsJsonArray().get(0).getAsInt())) {
                    toReplace.put(id, tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt());
                }
            });
            toReplace.forEach((integer, integer2) -> {
                unlockedIds.remove(integer);
                unlockedIds.add(integer2);
            });
        }
        return unlockedIds;
    }
}
