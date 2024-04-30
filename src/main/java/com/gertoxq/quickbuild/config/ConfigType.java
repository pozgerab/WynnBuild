package com.gertoxq.quickbuild.config;

import java.util.Objects;

public class ConfigType {
    private boolean showButtons = true;
    private String atreeEncoding = "0";
    private String cast = "Warrior";

    public boolean isShowButtons() {
        return showButtons;
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
        return showButtons == modConfig.showButtons && Objects.equals(atreeEncoding, modConfig.atreeEncoding) && Objects.equals(cast, modConfig.cast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(showButtons, atreeEncoding, cast);
    }
}
