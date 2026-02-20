package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.components.Models;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Range;

import java.util.*;

public class ConfigType {
    private String latestVersion = FabricLoader.getInstance().getModContainer(WynnBuild.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    private final List<SavedBuild> savedAtrees = new ArrayList<>();
    private boolean showButtons = true;
    private boolean showTreeLoader = true;
    private @Range(from = 1, to = 6) int defaultPowderLevel = 6;
    private @Range(from = 0, to = 1) int precision = 0;
    private boolean includeTomes = false;
    private boolean includeAspects = false;
    private Map<String, String> profileIdAtreeCache = new HashMap<>();

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isShowTreeLoader() {
        return showTreeLoader;
    }

    public void setShowTreeLoader(boolean showTreeLoader) {
        this.showTreeLoader = showTreeLoader;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public List<SavedBuild> getSavedAtrees() {
        return savedAtrees;
    }

    public int getDefaultPowderLevel() {
        return defaultPowderLevel;
    }

    public void setDefaultPowderLevel(int defaultPowderLevel) {
        this.defaultPowderLevel = defaultPowderLevel;
    }

    @Range(from = 0, to = 1)
    public int getPrecision() {
        return precision;
    }

    public void setPrecision(@Range(from = 0, to = 3) int precision) {
        this.precision = precision;
    }

    public boolean isIncludeTomes() {
        return includeTomes;
    }

    public void setIncludeTomes(boolean includeTomes) {
        this.includeTomes = includeTomes;
    }

    public boolean isIncludeAspects() {
        return includeAspects;
    }

    public void setIncludeAspects(boolean includeAspects) {
        this.includeAspects = includeAspects;
    }

    public Map<String, String> getProfileIdAtreeCache() {
        return profileIdAtreeCache;
    }

    public void setProfileIdAtreeCache(Map<String, String> profileIdAtreeCache) {
        this.profileIdAtreeCache = profileIdAtreeCache;
    }

    public void addTreeCache(String atree) {
        profileIdAtreeCache.put(Models.Character.getId(), atree);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ConfigType modConfig = (ConfigType) object;
        return Objects.equals(savedAtrees, modConfig.savedAtrees)
                && Objects.equals(latestVersion, modConfig.latestVersion)
                && showButtons == modConfig.showButtons
                && showTreeLoader == modConfig.showTreeLoader
                && defaultPowderLevel == modConfig.defaultPowderLevel
                && precision == modConfig.precision
                && includeTomes == modConfig.includeTomes
                && includeAspects == modConfig.includeAspects;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latestVersion, showButtons, savedAtrees, showTreeLoader, defaultPowderLevel, precision, includeTomes, includeAspects);
    }
}
