package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.webquery.providers.BuilderAbilitySchema;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MergeTrees {

    public static String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replace("'", "")
                .replace("’", "")
                .replaceAll("\\s+", " ")
                .trim()
                .replaceFirst("s\\b", "");
    }

    public static boolean isSame(String a, String b) {
        String na = normalize(a);
        String nb = normalize(b);

        if (na.equals(nb)) {
            return true;
        }
        int len = Math.min(na.length(), nb.length());
        if (len == 0) return false;

        int same = 0;
        for (int i = 0; i < len; i++) {
            if (na.charAt(i) == nb.charAt(i)) {
                same++;
            }
        }
        double ratio = same / (double) len;
        return ratio >= 0.8;
    }

    public static Map<String, Map<Integer, Ability>> merge(Map<String, Map<Integer, BuilderAbilitySchema>> builderTree, Map<String, List<ApiAbilitySchema>> apiTree) {

        Map<String, Map<Integer, Ability>> mergedTree = new HashMap<>();

        for (Map.Entry<String, Map<Integer, BuilderAbilitySchema>> builderEntry : builderTree.entrySet()) {

            Map<Integer, Ability> mergedClassTree = new HashMap<>();

            String className = builderEntry.getKey();
            String classNameKey = className.toLowerCase();
            Map<Integer, BuilderAbilitySchema> abilities = builderEntry.getValue();

            if (apiTree.containsKey(classNameKey)) {

                Set<Integer> usedAbilities = new HashSet<>();

                for (Map.Entry<Integer, BuilderAbilitySchema> abilityEntry : abilities.entrySet()) {

                    BuilderAbilitySchema builderAbility = abilityEntry.getValue();

                    for (ApiAbilitySchema apiAbility : apiTree.get(classNameKey)) {

                        if (usedAbilities.contains(apiAbility.id())) continue;

                        if (apiAbility.name().equals(builderAbility.display_name())
                                || isSame(apiAbility.name(), builderAbility.display_name())) {

                            Ability mergedAbility = new Ability(
                                    builderAbility.id(),
                                    builderAbility.display_name(),
                                    builderAbility.parents(),
                                    builderAbility.children(),
                                    apiAbility.pageNumber(),
                                    apiAbility.slot(),
                                    builderAbility.dependencies()
                            );

                            usedAbilities.add(apiAbility.id());

                            mergedClassTree.put(builderAbility.id(), mergedAbility);
                            break;
                        }
                    }
                }

                Set<Integer> unused = IntStream.range(0, abilities.size()).boxed().collect(Collectors.toSet());
                unused.removeAll(usedAbilities);

                if (!unused.isEmpty()) {
                    WynnBuild.warn("Could not construct full ability tree for class {}. Missing abilities: {}", className, unused);
                }

            }
            mergedTree.put(className, mergedClassTree);
        }
        WynnBuild.warn("{}", mergedTree);
        return mergedTree;
    }

}
