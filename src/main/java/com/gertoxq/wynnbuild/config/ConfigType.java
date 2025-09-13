package com.gertoxq.wynnbuild.config;

import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigType {
    private final List<SavedBuild> savedAtrees = new ArrayList<>();
    private boolean showButtons = true;
    private boolean showTreeLoader = true;
    private List<Integer> tomeIds = new ArrayList<>();
    private @Range(from = 1, to = 6) int defaultPowderLevel = 6;
    private @Range(from = 0, to = 1) int precision = 0;

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

    public List<Integer> getTomeIds() {
        return tomeIds;
    }

    public void setTomeIds(List<Integer> tomeIds) {
        this.tomeIds = tomeIds;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ConfigType modConfig = (ConfigType) object;
        return Objects.equals(savedAtrees, modConfig.savedAtrees)
                && showButtons == modConfig.showButtons
                && showTreeLoader == modConfig.showTreeLoader
                && tomeIds == modConfig.tomeIds
                && defaultPowderLevel == modConfig.defaultPowderLevel
                && precision == modConfig.precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, savedAtrees, showTreeLoader,
                tomeIds, defaultPowderLevel, precision);
    }
}
