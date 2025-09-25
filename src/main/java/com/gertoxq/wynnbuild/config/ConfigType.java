package com.gertoxq.wynnbuild.config;

import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigType {
    private final List<SavedBuild> savedAtrees = new ArrayList<>();
    private boolean showButtons = true;
    private boolean showTreeLoader = true;
    private @Range(from = 1, to = 6) int defaultPowderLevel = 6;
    private @Range(from = 0, to = 1) int precision = 0;
    private boolean includeTomes = false;
    private boolean includeAspects = false;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ConfigType modConfig = (ConfigType) object;
        return Objects.equals(savedAtrees, modConfig.savedAtrees)
                && showButtons == modConfig.showButtons
                && showTreeLoader == modConfig.showTreeLoader
                && defaultPowderLevel == modConfig.defaultPowderLevel
                && precision == modConfig.precision
                && includeTomes == modConfig.includeTomes
                && includeAspects == modConfig.includeAspects;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, savedAtrees, showTreeLoader, defaultPowderLevel, precision, includeTomes, includeAspects);
    }
}
