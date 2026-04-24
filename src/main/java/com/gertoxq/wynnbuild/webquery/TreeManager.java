package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.webquery.providers.BuilderAbilitySchema;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeManager {

    public static Map<String, Map<Integer, Ability>> matchTrees(Map<String, List<BuilderAbilitySchema>> builderTree, Map<String, List<ApiAbilitySchema>> apiTree) {

        Map<String, Map<Integer, Ability>> matchedTrees = new HashMap<>();

        for (String lowerCaseKey : apiTree.keySet()) {

            String key = Utils.capitalizeFirst(lowerCaseKey);

            List<BuilderAbilitySchema> builderClassTree = builderTree.get(key); // not lowercase
            List<ApiAbilitySchema> apiClassTree = apiTree.get(lowerCaseKey); // api is in lowercase

            matchedTrees.put(key, matchClassTrees(builderClassTree, apiClassTree)); // not lowercase
        }

        return matchedTrees;
    }

    private static Ability mergeAbility(BuilderAbilitySchema builderAbility, ApiAbilitySchema apiAbility) {
        return new Ability(
                builderAbility.id(),
                apiAbility.name(),
                builderAbility.parents(),
                builderAbility.children(),
                builderAbility.dependencies(),
                builderAbility.archetype(),
                builderAbility.archetype_req(),
                builderAbility.col(),
                apiAbility.pageNumber(),
                apiAbility.slot()
        );
    }

    private static Map<Integer, Ability> matchClassTrees(List<BuilderAbilitySchema> builderClassTree, List<ApiAbilitySchema> apiClassTree) {

        // columns for both trees
        List<List<ApiAbilitySchema>> columnApis = IntStream.range(0, 9)
                .mapToObj(i -> new ArrayList<ApiAbilitySchema>()).collect(Collectors.toList());
        List<List<BuilderAbilitySchema>> columnBuilder = IntStream.range(0, 9)
                .mapToObj(i -> new ArrayList<BuilderAbilitySchema>()).collect(Collectors.toList());

        // assign them to the grid
        apiClassTree.stream().sorted(Comparator.comparing(ab -> ab.pageNumber() * 54 + ab.slot()))
                .forEach(abil -> columnApis.get(abil.slot() % 9).add(abil));

        builderClassTree.stream().sorted(Comparator.comparingInt(ab -> ab.row() * 9 + ab.col()))
                .forEach(abil -> columnBuilder.get(abil.col()).add(abil));

        Map<Integer, Ability> matchedAbilities = new HashMap<>();
        // match the trees by the grid
        for (int col = 0; col < 9; col++) {

            List<ApiAbilitySchema> apiAbilities = columnApis.get(col);
            List<BuilderAbilitySchema> builderAbilities = columnBuilder.get(col);

            for (int index = 0; index < apiAbilities.size(); index++) {

                ApiAbilitySchema apiAbility = apiAbilities.get(index);
                BuilderAbilitySchema builderAbility = builderAbilities.get(index);

                Ability merged = mergeAbility(builderAbility, apiAbility);
                matchedAbilities.put(builderAbility.id(), merged);

            }
        }

        return matchedAbilities;
    }

}
