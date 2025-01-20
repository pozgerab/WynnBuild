package com.gertoxq.wynnbuild.config;

import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigType {
    private boolean showButtons = true;
    private String atreeEncoding = "0";
    private boolean showTreeLoader = true;
    private String cast = "Warrior";
    private List<SavedBuildType> savedAtrees = new ArrayList<>();
    private List<Integer> tomeIds = new ArrayList<>();
    private @Range(from = 1, to = 6) int defaultPowderLevel = 6;
    private List<SavedItemType> savedItems = new ArrayList<>();
    private int atreeIdleTime = 10;
    private @Range(from = 0, to = 3) int precision = 1;

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

    public int getAtreeIdleTime() {
        return atreeIdleTime;
    }

    public void setAtreeIdleTime(int atreeIdleTime) {
        this.atreeIdleTime = atreeIdleTime;
    }

    public List<SavedBuildType> getSavedAtrees() {
        return savedAtrees;
    }

    public void setSavedAtrees(List<SavedBuildType> savedAtrees) {
        this.savedAtrees = savedAtrees;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getAtreeEncoding() {
        return atreeEncoding;
    }

    public void setAtreeEncoding(String atreeEncoding) {
        this.atreeEncoding = atreeEncoding;
    }

    public List<Integer> getTomeIds() {
        return tomeIds;
    }

    public void setTomeIds(List<Integer> tomeIds) {
        this.tomeIds = tomeIds;
    }

    public List<SavedItemType> getSavedItems() {
        return savedItems;
    }

    public void setSavedItems(List<SavedItemType> savedItems) {
        this.savedItems = savedItems;
    }

    public int getDefaultPowderLevel() {
        return defaultPowderLevel;
    }

    public void setDefaultPowderLevel(int defaultPowderLevel) {
        this.defaultPowderLevel = defaultPowderLevel;
    }

    @Range(from = 0, to = 3)
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
        return savedAtrees == modConfig.savedAtrees
                && showButtons == modConfig.showButtons
                && Objects.equals(atreeEncoding, modConfig.atreeEncoding)
                && Objects.equals(cast, modConfig.cast)
                && showTreeLoader == modConfig.showTreeLoader
                && tomeIds == modConfig.tomeIds
                && defaultPowderLevel == modConfig.defaultPowderLevel
                && savedItems == modConfig.savedItems
                && atreeIdleTime == modConfig.atreeIdleTime
                && precision == modConfig.precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, atreeEncoding,
                cast, savedAtrees, showTreeLoader,
                tomeIds, defaultPowderLevel, savedItems, atreeIdleTime, precision);
    }
}
