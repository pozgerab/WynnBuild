package com.gertoxq.wynnbuild.webquery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wynntils.models.character.type.ClassType;

import java.util.*;

public class ApiDataProvider extends DataProvider<List<ApiAbilitySchema>> {

    public static Map<String, List<ApiAbilitySchema>> fullApiAtree = new HashMap<>();

    public ApiDataProvider(ClassType classType) {
        super(classType.getName().toLowerCase(), new TypeToken<List<ApiAbilitySchema>>() {}.getType());
    }

    @Override
    public void setData(List<ApiAbilitySchema> data) {
        super.setData(data);
        fullApiAtree.put(name, data);
    }

    @Override
    protected List<ApiAbilitySchema> transformData(JsonObject classTreeObj) {

        List<ApiAbilitySchema> apiTree = new ArrayList<>();

        JsonObject pages = classTreeObj.getAsJsonObject("pages");
        for (Map.Entry<String, JsonElement> pageEntry : pages.entrySet()) {

            JsonObject pageObj = pageEntry.getValue().getAsJsonObject();

            for (Map.Entry<String, JsonElement> abilityEntry : pageObj.entrySet()) {

                JsonObject abilityObj = abilityEntry.getValue().getAsJsonObject();

                assert abilityObj.has("page") : "missing page";
                assert abilityObj.has("slot") : "missing slot";

                ApiAbilitySchema apiAbility = new ApiAbilitySchema(
                        -1,
                        abilityObj.get("page").getAsInt(),
                        abilityObj.get("name").getAsString().replaceAll("<[^>]+>", ""),
                        abilityObj.get("slot").getAsInt()
                );

                apiTree.add(apiAbility);
            }
        }

        apiTree = new ArrayList<>(apiTree.stream().sorted(Comparator.comparingInt(apiAbility -> apiAbility.pageNumber() * 55 + apiAbility.slot())).toList());

        for (int i = 0; i < apiTree.size(); i++) {
            ApiAbilitySchema original = apiTree.get(i);
            ApiAbilitySchema idAnnotated = new ApiAbilitySchema(
                    i, original.pageNumber(), original.name(), original.slot()
            );
            apiTree.set(i, idAnnotated);
        }

        return apiTree;
    }

    public String url() {
        return "https://api.wynncraft.com/v3/ability/tree/" + name;
    }
}
