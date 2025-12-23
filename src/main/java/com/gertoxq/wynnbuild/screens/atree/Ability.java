package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.components.Models;

import java.util.*;

public record Ability(
        int id,
        String displayName,
        List<Integer> parents,
        List<Integer> children,
        Integer pageNumber,
        Integer slot,
        List<Integer> dependencies
) {

    public static Map<String, Map<Integer, Ability>> FULL_ABILITY_MAP;

    private static Map<Integer, Ability> ABILITY_MAP = new HashMap<>();
    private static Map<Integer, Ability> MULTI_PAGE_ABILITY_MAP = new HashMap<>();

    public static Map<Integer, Ability> getAbilityMap() {
        return ABILITY_MAP;
    }

    public static Ability getById(int id) {
        return ABILITY_MAP.get(id);
    }

    public static Optional<Ability> getAbilityByPageSlot(int page, int slot) {
        return Optional.ofNullable(MULTI_PAGE_ABILITY_MAP.get(page * 55 + slot));
    }

    public static void refreshTree() {
        WynnBuild.info("Refreshing atree, should only happen when changing cast...");

        String classTypeKey = Models.Character.getClassType().getName();
        if (!FULL_ABILITY_MAP.containsKey(classTypeKey)) {
            WynnBuild.warn("Atree loading not finished");
        } else {
            ABILITY_MAP = FULL_ABILITY_MAP.get(classTypeKey);
            MULTI_PAGE_ABILITY_MAP = new HashMap<>();
            ABILITY_MAP.forEach((id, ability) -> MULTI_PAGE_ABILITY_MAP.put(ability.pageNumber() * 55 + ability.slot(), ability));
        }
    }
}
