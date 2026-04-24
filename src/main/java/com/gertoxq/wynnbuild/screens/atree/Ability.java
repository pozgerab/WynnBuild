package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.components.Models;

import java.util.*;

public record Ability(
        int id,
        String displayName,
        Set<Integer> parents,
        TreeSet<Integer> children,
        Set<Integer> dependencies,
        String archetype,
        int archetypeReq,
        int col,
        int page,
        int slot
) {

    public static Map<String, Map<Integer, Ability>> FULL_ABILITY_MAP;

    private static Map<Integer, Ability> ABILITY_MAP = new HashMap<>();

    private static final Map<Integer, Ability> ABILITY_MULTI_PAGE_LOOKUP = new HashMap<>();
    private static final Map<Integer, List<Ability>> ABILITY_PAGES = new HashMap<>();

    public static Map<Integer, Ability> getAbilityMap() {
        return ABILITY_MAP;
    }

    public static List<Ability> getPage(int page) {
        return ABILITY_PAGES.getOrDefault(page, Collections.emptyList());
    }

    public static Ability getById(int id) {
        return ABILITY_MAP.get(id);
    }

    public static Optional<Ability> getByNameSlot(String name, int slot) {
        return ABILITY_MAP.values().stream()
                .filter(ability -> ability.displayName().equals(name) && ability.slot == slot)
                .findFirst();
    }

    public static Optional<Ability> getByPageAndSlot(int page, int slot) {
        return Optional.ofNullable(ABILITY_MULTI_PAGE_LOOKUP.get(key(page, slot)));
    }

    private static int key(int page, int slot) {
        return page * 54 + slot;
    }

    private int key() {
        return key(this.page, this.slot);
    }

    public static void refreshTree() {
        WynnBuild.info("Refreshing atree, should only happen when changing class...");

        String classTypeKey = Models.Character.getClassType().getName();
        if (!FULL_ABILITY_MAP.containsKey(classTypeKey)) {
            WynnBuild.warn("Atree loading not finished");
            return;
        }

        ABILITY_MAP = FULL_ABILITY_MAP.get(classTypeKey);
        ABILITY_MULTI_PAGE_LOOKUP.clear();
        ABILITY_MAP.forEach((id, ability) -> ABILITY_MULTI_PAGE_LOOKUP.put(ability.key(), ability));
        ABILITY_PAGES.clear();
        ABILITY_MAP.forEach((integer, ability) ->
                ABILITY_PAGES.merge(
                        ability.page(),
                        new ArrayList<>(List.of(ability)),
                        (abilities, abilities2) -> {
                            abilities.addAll(abilities2);
                            return abilities;
                        }
                )
        );
        WynnBuild.debug(ABILITY_MAP.toString());
    }
}
