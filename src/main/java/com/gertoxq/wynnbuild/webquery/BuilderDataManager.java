package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderDataManager {

    public static String LATEST_WYNNBUILDER_VERSION;
    public static int WYNN_VERSION_ID;
    private static final Pattern VERSION_STRING_PATTERN = Pattern.compile("'((?:\\d+\\.){3}\\d+)'");
    private static final String VERSION_FILE_URL = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/js/load_item.js";

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
                            processBuilderProvider(provider, ignoreCache || provider instanceof ApiDataProvider);
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
        if (!ignoreCache) {
            Optional<String> cacheVerOpt = provider.getCacheVersion();
            if (cacheVerOpt.isPresent() && cacheVerOpt.get().equals(BuilderDataManager.LATEST_WYNNBUILDER_VERSION)) {
                Optional<T> cacheDataOpt = provider.getCacheData();
                if (cacheDataOpt.isPresent()) {
                    provider.setData(cacheDataOpt.get());
                    WynnBuild.info("Loaded cached entries for provider {}", provider.getClass().getSimpleName());
                    return;
                }
            }
        }
        HttpHelper.get(provider.url())
                .thenApply(providerRes -> {
                    T dataMap = provider.transformData(JsonParser.parseString(providerRes.body()).getAsJsonObject());
                    provider.setCache(dataMap, BuilderDataManager.LATEST_WYNNBUILDER_VERSION);
                    return dataMap;
                })
                .thenAccept(stringTMap -> {
                    provider.setData(stringTMap);
                    WynnBuild.info("Loaded web entries for provider {}", provider.getClass().getSimpleName());
                }).exceptionally(throwable -> {
                    WynnBuild.error("Failed to load entries for provider {}. Err: {}", provider.getClass().getSimpleName(), throwable.getMessage());
                    return null;
                });
    }

}
