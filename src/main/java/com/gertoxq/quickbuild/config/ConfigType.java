package com.gertoxq.quickbuild.config;

import java.util.*;

public class ConfigType {
    private boolean showButtons = true;
    private String atreeEncoding = "0";
    private boolean showTreeLoader = true;
    private String cast = "Warrior";
    private List<SavedBuildType> savedAtrees = new ArrayList<>();

    public boolean isShowTreeLoader() {
        return showTreeLoader;
    }

    public void setShowTreeLoader(boolean showTreeLoader) {
        this.showTreeLoader = showTreeLoader;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public List<SavedBuildType> getSavedAtrees() {
        return savedAtrees;
    }

    public void setSavedAtrees(List<SavedBuildType> savedAtrees) {
        this.savedAtrees = savedAtrees;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ConfigType modConfig = (ConfigType) object;
        return savedAtrees == modConfig.savedAtrees && showButtons == modConfig.showButtons && Objects.equals(atreeEncoding, modConfig.atreeEncoding) && Objects.equals(cast, modConfig.cast) && showTreeLoader == modConfig.showTreeLoader;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, atreeEncoding, cast, savedAtrees, showTreeLoader);
    }
}
