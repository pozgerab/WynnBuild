package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.gertoxq.wynnbuild.screens.atree.Ability.fullatree;

public class WynnData {

    private static final Map<String, String> apiBuilderMap = new HashMap<>();

    public static Map<String, String> getApiBuilderMap() {
        return apiBuilderMap;
    }

    public static void loadAll() {
        loadAtree();
        loadApiBuilderMapping();
    }

    public static void loadAtree() {
        InputStream atreeStream = WynnBuild.class.getResourceAsStream("/" + "atree.json");
        assert atreeStream != null;
        fullatree = ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(atreeStream, StandardCharsets.UTF_8))).asMap();
    }

    public static void loadApiBuilderMapping() {
        InputStream mappingsStream = WynnBuild.class.getResourceAsStream("/" + "apibuildermap.json");
        assert mappingsStream != null;
        ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(mappingsStream, StandardCharsets.UTF_8))).asMap().forEach((api, builderEl) -> {
            String builder = builderEl.getAsString();
            apiBuilderMap.put(api, builder);
        });
    }


}
