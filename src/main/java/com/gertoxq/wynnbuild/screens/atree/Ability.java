package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.webquery.ApiDataProvider;
import com.gertoxq.wynnbuild.webquery.Providers;
import com.gertoxq.wynnbuild.webquery.TreeManager;
import com.wynntils.models.abilitytree.type.AbilityTreeSkillNode;
import com.wynntils.models.character.type.ClassType;

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
        int slot,
        int cost
) {

    private static Map<String, Map<Integer, Ability>> FULL_ABILITY_MAP;

    public static Map<String, Map<Integer, Ability>> getFullAbilityMap() {
        if (FULL_ABILITY_MAP == null) {
            throw new IllegalStateException("WynnBuild: Full ability map is not loaded yet.");
        }
        return FULL_ABILITY_MAP;
    }

    private static final Map<ClassType, Map<Integer, Ability>> ABILITY_MULTI_PAGE_LOOKUP = new HashMap<>();

    public static Map<ClassType, Map<Integer, Ability>> getAbilityMultiPageLookup() {
        if (ABILITY_MULTI_PAGE_LOOKUP.isEmpty()) {
            throw new IllegalStateException("WynnBuild: Ability Multi Page Lookup is not loaded yet.");
        }
        return ABILITY_MULTI_PAGE_LOOKUP;
    }

    public static Ability getById(int id, ClassType classType) {
        return getFullAbilityMap().get(classType.getName()).get(id);
    }

    public static int idFromNode(AbilityTreeSkillNode node, ClassType classType) {
        int slot = node.location().row() * 9 + node.location().col();
        Optional<Ability> ability = Ability.getByPageAndSlot(node.location().page(), slot, classType);
        if (ability.isEmpty()) {
            throw new RuntimeException("Couldn't find ability on page " + node.location().page() + " and slot " + slot);
        }
        return ability.get().id();
    }

    public static Optional<Ability> getByPageAndSlot(int page, int slot, ClassType classType) {
        return Optional.ofNullable(getAbilityMultiPageLookup().get(classType).get(key(page, slot)));
    }

    public static int key(int page, int slot) {
        return page * 54 + slot;
    }

    public int key() {
        return key(this.page, this.slot);
    }

    public static void init() {
        FULL_ABILITY_MAP = TreeManager.matchTrees(Providers.Atree.data(), ApiDataProvider.fullApiAtree);
        FULL_ABILITY_MAP.forEach((classTypeStr, abilityMap) -> {
            Map<Integer, Ability> classAbilityMap = new HashMap<>();
            abilityMap.forEach((id, ability) -> {
                classAbilityMap.put(ability.key(), ability);
            });
            ABILITY_MULTI_PAGE_LOOKUP.put(ClassType.fromName(classTypeStr), classAbilityMap);
        });
    }
}
