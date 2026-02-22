package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.client.WynnBuildClient;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.google.gson.*;
import com.wynntils.utils.FileUtils;
import com.wynntils.utils.mc.McUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gertoxq.wynnbuild.webquery.ApiDataProvider.fullApiAtree;

public abstract class DataProvider<T> {

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static final File cacheDir = new File(McUtils.mc().runDirectory, "cache/" + WynnBuild.MOD_ID);
    public static Set<String> loadedProviders = new HashSet<>();
    protected final File cacheFile;
    protected final Type dataType;
    protected final String name;
    protected T data;

    public DataProvider(String name, Type dataType) {
        this.cacheFile = new File(cacheDir, name + ".json");
        this.dataType = dataType;
        this.name = name;
    }

    private static void loaded(String name) {
        loadedProviders.add(name);
        if (DataProvider.loadedProviders.containsAll(List.of("archer", "warrior", "assassin", "mage", "shaman", "atree"))) {
            Ability.FULL_ABILITY_MAP = MergeTrees.merge(Providers.Atree.data(), fullApiAtree);
        }
    }

    public T data() {
        return data;
    }

    protected JsonElement toJson(T data) {
        return DataProvider.gson.toJsonTree(data);
    }

    protected abstract T transformData(JsonObject jsonObject);

    public void setData(T data) {
        this.data = data;
        loaded(name);
    }

    protected T fromJson(JsonElement dataElement) {
        return DataProvider.gson.fromJson(dataElement, this.dataType);
    }

    protected Optional<JsonObject> readCacheFile() {
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

    public Optional<T> getCacheData() {
        return readCacheFile()
                .map(cacheData -> cacheData.get("data"))
                .filter(element -> !element.isJsonNull() && !element.isJsonPrimitive())
                .map(this::fromJson);
    }

    public Optional<String> getCacheVersion() {
        return readCacheFile()
                .map(cacheData -> cacheData.get("version"))
                .filter(element -> !element.isJsonNull() && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
                .map(JsonElement::getAsString);
    }

    protected JsonObject createCacheObject(T dataMap, String version) {
        JsonObject cacheObject = new JsonObject();
        cacheObject.addProperty("version", version);
        cacheObject.add("data", toJson(dataMap));
        return cacheObject;
    }

    public void setCache(T dataMap, String version) {
        if (!DataProvider.cacheDir.exists()) {
            DataProvider.cacheDir.mkdirs();
        }
        if (!cacheFile.exists()) {
            FileUtils.createNewFile(cacheFile);
        }

        JsonObject cacheObject = createCacheObject(dataMap, version);

        try {
            Files.writeString(cacheFile.toPath(), DataProvider.gson.toJson(cacheObject));
        } catch (IOException e) {
            WynnBuild.warn("Failed to write cache. Error: {}", e.getMessage());
        }
    }

    public abstract String url();
}
