package com.gertoxq.wynnbuild.config;

import com.wynntils.models.character.type.ClassType;

public class SavedBuild {
    private String name;
    private String value;
    private ClassType cast;

    public SavedBuild(String name, String value, ClassType cast) {
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

    public ClassType getCast() {
        return cast;
    }

    public void setCast(ClassType cast) {
        this.cast = cast;
    }
}
