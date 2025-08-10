package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.base.fields.Cast;

public class SavedBuild {
    private String name;
    private String value;
    private Cast cast;

    public SavedBuild(String name, String value, Cast cast) {
        this.cast = cast;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Cast getCast() {
        return cast;
    }

    public void setCast(Cast cast) {
        this.cast = cast;
    }
}
