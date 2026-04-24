package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.BuilderDataProvider;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class AtreeProvider extends BuilderDataProvider<List<BuilderAbilitySchema>> {

    public AtreeProvider() {
        super("atree", new TypeToken<Map<String, List<BuilderAbilitySchema>>>() {
        }.getType());
    }

    private static @NotNull Map<Integer, BuilderAbilitySchema> getMappedAbilities(List<BuilderAbilitySchema> list) {
        Map<Integer, BuilderAbilitySchema> mapped = new HashMap<>();

        for (BuilderAbilitySchema ability : list) {
            mapped.put(ability.id(), ability);
        }
        return mapped;
    }

    @Override
    public Map<String, List<BuilderAbilitySchema>> transformData(JsonObject atreeObj) {

        Map<String, List<BuilderAbilitySchema>> originalSerialized = gson.fromJson(
                atreeObj, new TypeToken<Map<String, List<BuilderAbilitySchema>>>() {
                }.getType()
        );

        Map<String, Map<Integer, BuilderAbilitySchema>> atreeMap = new HashMap<>();

        for (Map.Entry<String, List<BuilderAbilitySchema>> entry : originalSerialized.entrySet()) {
            String cast = entry.getKey();
            atreeMap.put(cast, getMappedAbilities(entry.getValue()));
        }

        // appending children-parents relationships

        for (Map<Integer, BuilderAbilitySchema> abilities : atreeMap.values()) {

            for (BuilderAbilitySchema abil : abilities.values()) {

                for (Integer parentId : abil.parents()) {

                    BuilderAbilitySchema parent = abilities.get(parentId);
                    TreeSet<Integer> children = parent.children();
                    children.add(abil.id());
                }
            }
        }

        Map<String, List<BuilderAbilitySchema>> abilities = new HashMap<>();

        for (String key : atreeMap.keySet()) {

            abilities.put(key, atreeMap.get(key).values().stream().toList());
        }

        return abilities;
    }

}
