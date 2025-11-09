package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wynntils.utils.FileUtils;
import com.wynntils.utils.mc.McUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

public abstract class DataProvider<V> {

    private static final File cacheDir = new File(McUtils.mc().runDirectory, "cache/" + "wynnbuild");
    private static final String dataFolderUrl = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/data/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String name;
    private Map<String, V> data;
    private final File cacheFile;

    public DataProvider(String name) {
        this.name = name;
        this.cacheFile = new File(cacheDir, name + ".json");
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

    public void setCache(JsonObject dataObject, String version) {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (!cacheFile.exists()) {
            FileUtils.createNewFile(cacheFile);
        }

        JsonObject cacheObject = new JsonObject();
        cacheObject.addProperty("version", version);
        cacheObject.add("data", dataObject);

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
            JsonObject cacheObject = gson.fromJson(reader, JsonObject.class);
            return Optional.of(cacheObject);
        } catch (IOException e) {
            WynnBuild.error("Failed to read cache. Error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getCacheVersion() {
        Optional<JsonObject> cacheDataOpt = readCacheFile();
        if (cacheDataOpt.isEmpty()) return Optional.empty();
        JsonObject cacheData = cacheDataOpt.get();
        return Optional.ofNullable(cacheData.get("version").getAsString());
    }

    public Optional<Map<String, V>> getCacheData() {
        Optional<JsonObject> cacheDataOpt = readCacheFile();
        if (cacheDataOpt.isEmpty()) return Optional.empty();
        JsonObject cacheData = cacheDataOpt.get();

        Type type = new TypeToken<Map<String, V>>() {}.getType();
        return Optional.ofNullable(gson.fromJson(cacheData.get("data"), type));
    }
}
