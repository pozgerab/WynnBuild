package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.BuilderDataProvider;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class TomeProvider extends BuilderDataProvider<Integer> {

    public TomeProvider() {
        super("tomes", new TypeToken<Map<String, Integer>>() {}.getType());
    }

    @Override
    protected Map<String, Integer> transformData(JsonObject jsonObject) {

        Map<String, Integer> tomeMap = new HashMap<>();

        jsonObject.getAsJsonArray("tomes").asList().forEach(tomeElement -> {

            JsonObject tomeObj = tomeElement.getAsJsonObject();
            String tomeName = tomeObj.get("name").getAsString();
            Integer tomeId = tomeObj.get("id").getAsInt();

            tomeMap.put(tomeName, tomeId);

        });

        return tomeMap;

    }
}
