package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.DataProvider;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class TomeProvider extends DataProvider<Integer> {

    public TomeProvider() {
        super("tomes");
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
