package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.base.fields.ItemType;

import java.util.Date;

public class SavedItem {

    private final Date createdAt;
    private final ItemType type;
    private final String hash;
    private final Integer baseItemId;
    private String name;

    public SavedItem(String name, ItemType type, String hash, Integer baseItemId) {
        this.baseItemId = baseItemId;
        this.createdAt = new Date();
        this.name = name;
        this.type = type;
        this.hash = hash;
    }

    public Integer getBaseItemId() {
        return baseItemId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getType() {
        return type;
    }

    public String getHash() {
        return hash;
    }
}
