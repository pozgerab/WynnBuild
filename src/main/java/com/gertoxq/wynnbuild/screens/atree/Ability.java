package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.components.Models;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record Ability(
        int id,
        String displayName,
        List<Integer> parents,
        List<Integer> children,
        @Nullable Integer pageNumber,
        @Nullable Integer slot,
        List<Integer> dependencies
) {

    public static Map<String, Map<Integer, Ability>> FULL_ABILITY_MAP;

    final static Map<String, List<Integer>> nameToId = new HashMap<>();
    private static Map<Integer, Ability> ABILITY_MAP = new HashMap<>();

    public static Map<Integer, Ability> getAbilityMap() {
        return ABILITY_MAP;
    }

    public static Ability getById(int id) {
        return ABILITY_MAP.get(id);
    }

    public static Optional<Integer> getIdByNameSlotPage(String name, int slot, int page) {
        List<Integer> possibleIds = nameToId.getOrDefault(name, List.of());
        Integer foundId = null;
        for (Integer possibleId : possibleIds) {
            if (Ability.getById(possibleId).slot() == null || Objects.equals(Ability.getById(possibleId).slot(), slot) && Objects.equals(Ability.getById(possibleId).pageNumber(), page)) {
                foundId = possibleId;
            }
        }
        return Optional.ofNullable(foundId);
    }

    public static void refreshTree() {
        WynnBuild.info("Refreshing atree, should only happen when changing cast...");

        String classTypeKey = Models.Character.getClassType().getName();
        if (!FULL_ABILITY_MAP.containsKey(classTypeKey)) {
            WynnBuild.warn("Atree loading not finished");
        } else {
            ABILITY_MAP = FULL_ABILITY_MAP.get(classTypeKey);
        }
    }
}
