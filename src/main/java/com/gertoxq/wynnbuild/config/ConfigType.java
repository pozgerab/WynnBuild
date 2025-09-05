package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.build.Aspect;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigType {
    private boolean showButtons = true;
    private String atreeEncoding = "0";
    private boolean showTreeLoader = true;
    private String cast = "Warrior";
    private List<SavedBuild> savedAtrees = new ArrayList<>();
    private List<Integer> tomeIds = new ArrayList<>();
    private @Range(from = 1, to = 6) int defaultPowderLevel = 6;
    private List<SavedItem> savedItems = new ArrayList<>();
    private @Range(from = 0, to = 1) int precision = 0;
    private List<SavedAspect> aspects = new ArrayList<>();

    public List<SavedAspect> getAspects() {
        return aspects;
    }

    public void setAspects(List<SavedAspect> aspects) {
        this.aspects = aspects;
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

    public void setSavedAtrees(List<SavedBuild> savedAtrees) {
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

    public List<SavedItem> getSavedItems() {
        return savedItems;
    }

    public void setSavedItems(List<SavedItem> savedItems) {
        this.savedItems = savedItems;
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
        return savedAtrees == modConfig.savedAtrees
                && showButtons == modConfig.showButtons
                && Objects.equals(atreeEncoding, modConfig.atreeEncoding)
                && Objects.equals(cast, modConfig.cast)
                && showTreeLoader == modConfig.showTreeLoader
                && tomeIds == modConfig.tomeIds
                && defaultPowderLevel == modConfig.defaultPowderLevel
                && savedItems == modConfig.savedItems
                && precision == modConfig.precision
                && aspects == modConfig.aspects;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, atreeEncoding,
                cast, savedAtrees, showTreeLoader,
                tomeIds, defaultPowderLevel, savedItems, precision, aspects);
    }

    public record SavedAspect(int id, int tier) {
        public static SavedAspect fromAspect(Aspect aspect) {
            return new SavedAspect(aspect.id, aspect.tier);
        }
    }
}
