package com.gertoxq.wynnbuild.webquery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.webquery.BuilderDataManager.LATEST_WYNNBUILDER_VERSION;

public abstract class BuilderDataProvider<V> extends DataProvider<Map<String, V>> {

    private Integer dbVersion;
    private final String dbName;

    public BuilderDataProvider(String name, String dbName, Type dataType) {
        super(name, dataType);
        this.dbName = dbName;
    }

    public BuilderDataProvider(String name, Type dataType) {
        this(name, null, dataType);
    }

    public boolean hasDb() {
        return dbName != null;
    }

    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getDbUrl() {
        String jsFolderUrl = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/js/";
        return jsFolderUrl + "load_" + dbName.toLowerCase() + ".js";
    }

    public Pattern getDbVersionPattern() {
        return Pattern.compile("(?:const|let|var) \\Q" + dbName.toUpperCase() + "_DB_VERSION\\E\\s*=\\s*(\\d*);");
    }

    @Override
    protected JsonObject createCacheObject(Map<String, V> dataMap, String version) {
        JsonObject cacheObject = super.createCacheObject(dataMap, version);
        if (hasDb()) cacheObject.addProperty("dbVersion", dbVersion);
        return cacheObject;
    }

    public Optional<Integer> getCacheDbVersion() {
        return readCacheFile()
                .map(cacheData -> cacheData.get("dbVersion"))
                .filter(element -> !element.isJsonNull() && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())
                .map(JsonElement::getAsInt);
    }

    @Override
    public String url() {
        String dataFolderUrl = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/data/";
        return dataFolderUrl + LATEST_WYNNBUILDER_VERSION + "/" + name + ".json";
    }
}
