package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderDataManager {

    private static final Pattern VERSION_STRING_PATTERN = Pattern.compile("'((?:\\d+\\.){3}\\d+)'");
    private static final String VERSION_FILE_URL = BuilderDataProvider.masterUrl + "js/load_item.js";
    public static String LATEST_WYNNBUILDER_VERSION;
    public static int WYNN_VERSION_ID;

    public static void initBuilderData() {
        reloadBuilderData(false);
    }

    public static void reloadBuilderData(boolean ignoreCache) {
        HttpHelper.get(VERSION_FILE_URL)
                .thenAccept(res -> {

                    Matcher matcher = VERSION_STRING_PATTERN.matcher(res.body());
                    int count = 0;
                    String last = null;

                    while (matcher.find()) {
                        last = matcher.group(1);
                        count++;
                    }

                    BuilderDataManager.WYNN_VERSION_ID = count - 1;
                    BuilderDataManager.LATEST_WYNNBUILDER_VERSION = last;

                    WynnBuild.info("Wynnbuilder version: {} (id: {})", BuilderDataManager.LATEST_WYNNBUILDER_VERSION, BuilderDataManager.WYNN_VERSION_ID);

                    Arrays.stream(Providers.class.getDeclaredFields()).forEach(field -> {
                        try {
                            WynnBuild.info("Trying to load provider from field: {}", field.getName());

                            field.setAccessible(true);

                            DataProvider<?> provider = (DataProvider<?>) field.get(null);
                            processBuilderProvider(provider, ignoreCache);
                        } catch (Exception e) {
                            WynnBuild.error("Failed to load provider from field {}. Error: {}", field.getName(), e.getMessage());
                        }
                    });

                }).exceptionally(throwable -> {
                    WynnBuild.error("Failed to get latest version file. Error: {}", throwable.getMessage());
                    return null;
                });

    }

    private static <T> void processBuilderProvider(DataProvider<T> provider, boolean ignoreCache) {
        if (ignoreCache) {
            WynnBuild.info("Ignoring cache for provider {}:{}", provider.getClass().getSimpleName(), provider.name);
            fetchData(provider);
            return;
        }

        Optional<String> cacheVerOpt = provider.getCacheVersion();
        if (cacheVerOpt.isEmpty() || !cacheVerOpt.get().equals(BuilderDataManager.LATEST_WYNNBUILDER_VERSION)) {
            WynnBuild.info("Cache version mismatch or not found for provider {}:{}. Expected: {}, Found: {}",
                    provider.getClass().getSimpleName(),
                    provider.name,
                    BuilderDataManager.LATEST_WYNNBUILDER_VERSION,
                    cacheVerOpt.orElse("null"));
            fetchData(provider);
            return;
        }

        Optional<T> cacheDataOpt = provider.getCacheData();
        if (cacheDataOpt.isEmpty()) {
            WynnBuild.info("Cache data not found for provider {}:{}", provider.getClass().getSimpleName(), provider.name);
            fetchData(provider);
            return;
        }

        if (!(provider instanceof BuilderDataProvider<?> builderDataProvider) || !builderDataProvider.hasDb()) {
            provider.setData(cacheDataOpt.get());
            WynnBuild.info("Loaded cached entries for provider {}:{}", provider.getClass().getSimpleName(), provider.name);
            return;
        }

        WynnBuild.info("Checking DB version for provider {}:{}", provider.getClass().getSimpleName(), provider.name);
        HttpHelper.get(builderDataProvider.getDbUrl())
                .thenAccept(res -> {

                    Matcher matcher = builderDataProvider.getDbVersionPattern().matcher(res.body());
                    int version;

                    if (!matcher.find()) {
                        WynnBuild.warn("Failed to find matching pattern on {}", builderDataProvider.getDbUrl());
                        fetchData(provider);
                        return;
                    }

                    try {
                        version = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        WynnBuild.warn("Failed to parse \"{}\"", matcher.group(1));
                        fetchData(provider);
                        return;
                    }

                    if (!Objects.equals(builderDataProvider.getCacheDbVersion().orElse(null), version)) {
                        WynnBuild.info("DB version mismatch for provider {}:{}. Expected: {}, Found: {}",
                                provider.getClass().getSimpleName(),
                                provider.name,
                                version,
                                builderDataProvider.getCacheDbVersion().orElse(null));
                        builderDataProvider.setDbVersion(version);
                        fetchData(provider);
                        return;
                    }

                    provider.setData(cacheDataOpt.get());
                    WynnBuild.info("Loaded cached entries for provider {}:{}", provider.getClass().getSimpleName(), provider.name);
                });

    }

    private static <T> void fetchData(DataProvider<T> provider) {
        HttpHelper.get(provider.url())
                .thenApply(providerRes -> {
                    T dataMap = provider.transformData(JsonParser.parseString(providerRes.body()).getAsJsonObject());
                    provider.setCache(dataMap, BuilderDataManager.LATEST_WYNNBUILDER_VERSION);
                    return dataMap;
                })
                .thenAccept(stringTMap -> {
                    provider.setData(stringTMap);
                    WynnBuild.info("Loaded web entries for provider {}:{}", provider.getClass().getSimpleName(), provider.name);
                }).exceptionally(throwable -> {
                    WynnBuild.error("Failed to load entries for provider {}:{}. Err: {}", provider.getClass().getSimpleName(), provider.name, throwable.getMessage());
                    return null;
                });
    }

}
