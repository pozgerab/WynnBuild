package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.*;
import com.wynntils.utils.FileUtils;
import com.wynntils.utils.mc.McUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonParseException;

public abstract class DataProvider<V> {

    private static final File cacheDir = new File(McUtils.mc().runDirectory, "cache/" + "wynnbuild");
    private static final String dataFolderUrl = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/data/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String name;
    private Map<String, V> data;
    private final File cacheFile;
    private final Type dataType;

    public DataProvider(String name, Type dataType) {
        this.name = name;
        this.cacheFile = new File(cacheDir, name + ".json");
        this.dataType = dataType;
    }

    public Map<String, V> data() {
        return data;
    }

    public String url(String version) {
        return dataFolderUrl + version + "/" + name + ".json";
    }

    protected abstract Map<String, V> transformData(JsonObject jsonObject);

    public void setData(Map<String, V> data) {
        this.data = data;
    }

    public void setCache(Map<String, V> dataMap, String version) {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (!cacheFile.exists()) {
            FileUtils.createNewFile(cacheFile);
        }

        JsonObject cacheObject = new JsonObject();
        cacheObject.addProperty("version", version);
        cacheObject.add("data", gson.toJsonTree(dataMap));

        try {
            Files.writeString(cacheFile.toPath(), gson.toJson(cacheObject));
        } catch (IOException e) {
            WynnBuild.warn("Failed to write cache. Error: {}", e.getMessage());
        }
    }

    private Optional<JsonObject> readCacheFile() {
        if (!cacheFile.exists()) {
            return Optional.empty();
        }
        try (FileReader reader = new FileReader(cacheFile)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element == null || !element.isJsonObject()) {
                WynnBuild.warn("Cache file {} is not a valid JSON object.", cacheFile.getName());
                return Optional.empty();
            }
            return Optional.of(element.getAsJsonObject());
        } catch (IOException | JsonParseException e) {
            WynnBuild.error("Failed to read cache file {}. Error: {}", cacheFile.getName(), e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getCacheVersion() {
        return readCacheFile()
                .map(cacheData -> cacheData.get("version"))
                .filter(element -> !element.isJsonNull() && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
                .map(JsonElement::getAsString);
    }

    public Optional<Map<String, V>> getCacheData() {
        Optional<JsonObject> cacheDataOpt = readCacheFile();
        if (cacheDataOpt.isEmpty()) return Optional.empty();
        JsonObject cacheData = cacheDataOpt.get();

        return Optional.ofNullable(gson.fromJson(cacheData.get("data"), this.dataType));
    }
}
