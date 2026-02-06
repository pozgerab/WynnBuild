package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.BuilderDataProvider;
import com.gertoxq.wynnbuild.webquery.DataProvider;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AtreeProvider extends BuilderDataProvider<Map<Integer, BuilderAbilitySchema>> {

    public AtreeProvider() {
        super("atree", new TypeToken<Map<String, Map<Integer, BuilderAbilitySchema>>>(){}.getType());
    }

    @Override
    public void setData(Map<String, Map<Integer, BuilderAbilitySchema>> data) {
        super.setData(data);
    }

    @Override
    public Map<String, Map<Integer, BuilderAbilitySchema>> transformData(JsonObject atreeObj) {

        Map<String, List<BuilderAbilitySchema>> originalSerialized = DataProvider.gson.fromJson(
                atreeObj, new TypeToken<Map<String, List<BuilderAbilitySchema>>>(){}.getType()
        );

        Map<String, Map<Integer, BuilderAbilitySchema>> atreeMap = new HashMap<>();

        for (Map.Entry<String, List<BuilderAbilitySchema>> entry : originalSerialized.entrySet()) {
            String cast = entry.getKey();
            atreeMap.put(cast, getMappedAbilities(entry.getValue()));
        }

        for (Map<Integer, BuilderAbilitySchema> abilities : atreeMap.values()) {

            for (BuilderAbilitySchema abil : abilities.values()) {

                for (Integer parentId : abil.parents()) {

                    BuilderAbilitySchema parent = abilities.get(parentId);
                    List<Integer> children = parent.children();
                    if (!children.contains(abil.id())) {
                        children.add(abil.id());
                        Collections.sort(children);
                    }
                }
            }
        }

        return atreeMap;
    }

    private static @NotNull Map<Integer, BuilderAbilitySchema> getMappedAbilities(List<BuilderAbilitySchema> list) {
        Map<Integer, BuilderAbilitySchema> mapped = new HashMap<>();

        for (BuilderAbilitySchema abil : list) {
            BuilderAbilitySchema temp = new BuilderAbilitySchema(
                    abil.id(),
                    abil.display_name()
                            .replace("1", "I")
                            .replace("2", "II")
                            .replace("3", "III"),
                    new ArrayList<>(abil.parents()),
                    new ArrayList<>(abil.dependencies()),
                    new ArrayList<>()
            );
            mapped.put(temp.id(), temp);
        }
        return mapped;
    }

}
