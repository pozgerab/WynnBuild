package com.gertoxq.wynnbuild.webquery.providers;

import com.gertoxq.wynnbuild.webquery.DataProvider;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class MidProvider extends DataProvider<String> {

    public MidProvider() {
        super("majid", new TypeToken<Map<String, String>>() {}.getType());
    }

    @Override
    protected Map<String, String> transformData(JsonObject jsonObject) {

        Map<String, String> majidMap = new HashMap<>();

        jsonObject.asMap().forEach((name, majidElement) -> {

            JsonObject majidObj = majidElement.getAsJsonObject();
            String majidDisplayName = majidObj.get("displayName").getAsString();

            majidMap.put(majidDisplayName, name);

        });

        return majidMap;
    }
}
