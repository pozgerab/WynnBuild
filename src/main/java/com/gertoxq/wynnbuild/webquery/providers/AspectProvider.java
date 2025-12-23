package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.BuilderDataProvider;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wynntils.models.character.type.ClassType;

import java.util.HashMap;
import java.util.Map;

public class AspectProvider extends BuilderDataProvider<Map<String, Integer>> {
    public AspectProvider() {
        super("aspects", new TypeToken<Map<String, Map<String, Integer>>>() {}.getType());
    }

    public Map<String, Integer> getClassAspects(ClassType classType) {
        return this.data().get(classType.getName());
    }

    @Override
    protected Map<String, Map<String, Integer>> transformData(JsonObject jsonObject) {

        Map<String, Map<String, Integer>> aspectMap = new HashMap<>();

        jsonObject.asMap().forEach((className, aspectList) -> {

            Map<String, Integer> classAspectMap = new HashMap<>();
            aspectList.getAsJsonArray().asList().forEach(aspectElement -> {

                JsonObject aspectObj = aspectElement.getAsJsonObject();

                String aspectName = aspectObj.get("displayName").getAsString();
                Integer aspectId = aspectObj.get("id").getAsInt();

                classAspectMap.put(aspectName, aspectId);
            });

            aspectMap.put(className, classAspectMap);
        });

        return aspectMap;
    }
}
