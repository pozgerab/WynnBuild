package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.WynnBuild;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderDataManager {

    public static boolean queriedVersion = false;
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

                    queriedVersion = true;

                    WynnBuild.info("Wynnbuilder latest version: {}", BuilderDataManager.LATEST_WYNNBUILDER_VERSION);
                    WynnBuild.info("Wynnbuilder latest version id: {}", BuilderDataManager.WYNN_VERSION_ID);

                    Arrays.stream(Providers.class.getDeclaredFields()).forEach(field -> {

                        field.setAccessible(true);
                        try {
                            DataProvider<?> provider = (DataProvider<?>) field.get(null);
                            processProvider(provider, ignoreCache);

                        } catch (IllegalAccessException e) {
                            WynnBuild.error("Failed to access field. Err: {}", e.getMessage());
                        }
                    });

                });

    }

    private static <T> void processProvider(DataProvider<T> provider, boolean ignoreCache) {
        if (!ignoreCache) {
            Optional<String> cacheVerOpt = provider.getCacheVersion();
            if (cacheVerOpt.isPresent() && cacheVerOpt.get().equals(BuilderDataManager.LATEST_WYNNBUILDER_VERSION)) {
                Optional<Map<String, T>> cacheDataOpt = provider.getCacheData();
                if (cacheDataOpt.isPresent()) {
                    provider.setData(cacheDataOpt.get());
                    WynnBuild.info("Loaded cached entries for provider {}", provider.data());
                    return;
                }
            }
        }
        HttpHelper.get(provider.url(BuilderDataManager.LATEST_WYNNBUILDER_VERSION))
                .thenApply(providerRes -> {
                    JsonObject jsonObject = JsonParser.parseString(providerRes.body()).getAsJsonObject();
                    provider.setCache(jsonObject, BuilderDataManager.LATEST_WYNNBUILDER_VERSION);
                    return provider.transformData(jsonObject);
                })
                .thenAccept(stringTMap -> {
                    provider.setData(stringTMap);
                    WynnBuild.info("Loaded entries for provider {}", provider.data());
                });
    }

}
