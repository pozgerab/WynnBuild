package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.DataProvider;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ItemProvider extends DataProvider<Integer> {

    public ItemProvider() {
        super("items");
    }

    @Override
    protected Map<String, Integer> transformData(JsonObject jsonObject) {

        Map<String, Integer> idMap = new HashMap<>();

        jsonObject.getAsJsonArray("items").asList().forEach(jsonElement -> {

            String name = jsonElement.getAsJsonObject().get("name").getAsString();
            Integer id = jsonElement.getAsJsonObject().get("id").getAsInt();

            idMap.put(name, id);
        });

        return idMap;
    }
}
