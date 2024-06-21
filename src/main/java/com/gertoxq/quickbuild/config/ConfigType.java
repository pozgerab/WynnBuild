package com.gertoxq.quickbuild.config;

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
    private int defaultPowderLevel = 6;

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

    public int getDefaultPowderLevel() {
        return defaultPowderLevel;
    }

    public void setDefaultPowderLevel(int defaultPowderLevel) {
        this.defaultPowderLevel = defaultPowderLevel;
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
                && defaultPowderLevel == modConfig.defaultPowderLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, atreeEncoding, cast, savedAtrees, showTreeLoader, tomeIds, defaultPowderLevel);
    }
}
