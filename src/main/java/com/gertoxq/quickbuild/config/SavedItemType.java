package com.gertoxq.quickbuild.config;

import com.gertoxq.quickbuild.custom.ID;

import java.util.Date;

public class SavedItemType {

    private final Date createdAt;
    private final ID.ItemType type;
    private final String hash;
    private final int baseItemId;
    private String name;

    public SavedItemType(String name, ID.ItemType type, String hash, int baseItemId) {
        this.baseItemId = baseItemId;
        this.createdAt = new Date();
        this.name = name;
        this.type = type;
        this.hash = hash;
    }

    public int getBaseItemId() {
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

    public ID.ItemType getType() {
        return type;
    }

    public String getHash() {
        return hash;
    }
}
