package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.models.character.type.ClassType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.gertoxq.wynnbuild.screens.atree.Ability.fullatree;

public class WynnData {

    private static final Map<String, Integer> nameToId = new HashMap<>();
    private static final Map<String, Integer> tomeMap = new HashMap<>();
    private static final Map<String, String> apiBuilderMap = new HashMap<>();
    private static final Map<String, String> majorIdMap = new HashMap<>();
    private static final Map<ClassType, Map<String, Integer>> aspectIdsMaps = new HashMap<>();

    public static Map<String, Integer> getIdMap() {
        return nameToId;
    }

    public static Map<String, Integer> getTomeMap() {
        return tomeMap;
    }

    public static Map<String, String> getApiBuilderMap() {
        return apiBuilderMap;
    }

    public static Map<String, String> getMajorIdMap() {
        return majorIdMap;
    }

    public static Map<String, Integer> getAspectMap(ClassType classType) {
        return aspectIdsMaps.get(classType);
    }

    public static void loadAll() {
        loadItems();
        loadAtree();
        loadApiBuilderMapping();
        loadMajorIds();
        loadAspects();
    }

    public static void loadItems() {
        InputStream dataStream = WynnBuild.class.getResourceAsStream("/" + "dataMap.json");
        InputStream tomeStream = WynnBuild.class.getResourceAsStream("/" + "tomeIdMap.json");
        try {
            assert dataStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dataStream, StandardCharsets.UTF_8))).asMap().forEach((name, idEl) -> {
                int id = idEl.getAsInt();

                nameToId.put(name, id);
            });
            assert tomeStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(tomeStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> tomeMap.put(s, jsonElement.getAsInt()));

        } catch (Exception e) {
            WynnBuild.error("didn't finish init, something went wrong with wynnbuild: {}", e);
        }
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

    public static void loadMajorIds() {
        InputStream majorIdsStream = WynnBuild.class.getResourceAsStream("/" + "major_ids.json");
        assert majorIdsStream != null;
        ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(majorIdsStream, StandardCharsets.UTF_8))).asMap().forEach((displayName, codeNameEl) ->
                majorIdMap.put(displayName, codeNameEl.getAsString()));
    }

    public static void loadAspects() {
        InputStream aspectStream = WynnBuild.class.getResourceAsStream("/" + "aspects.json");
        assert aspectStream != null;
        ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(aspectStream, StandardCharsets.UTF_8))).asMap().forEach((castKey, jsonElement) -> {
            ClassType cast = ClassType.fromName(castKey);
            JsonObject aspectObj = jsonElement.getAsJsonObject();
            Map<String, Integer> castIds = new HashMap<>();
            aspectObj.asMap().forEach((intKey, aspectName) -> {
                int id = Integer.parseInt(intKey);
                String aspectNameStr = aspectName.getAsString();
                castIds.put(aspectNameStr, id);
            });
            aspectIdsMaps.put(cast, castIds);
        });
    }


}
