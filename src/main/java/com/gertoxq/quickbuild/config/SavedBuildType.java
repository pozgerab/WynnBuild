package com.gertoxq.quickbuild.config;

import com.gertoxq.quickbuild.Cast;

public class SavedBuildType {
    private String name;
    private String value;
    private Cast cast;

    public SavedBuildType(String name, String value, Cast cast) {
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

    public String getValue() {
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
