package com.gertoxq.quickbuild.config;

import com.gertoxq.quickbuild.custom.IDS;

import java.util.Date;

public class SavedItemType {

    private final Date createdAt;
    private final String name;
    private final IDS.ItemType type;
    private final String hash;
    private final int baseItemId;

    public SavedItemType(String name, IDS.ItemType type, String hash, int baseItemId) {
        this.baseItemId = baseItemId;
        this.createdAt = new Date();
        this.name = name;
        this.type = type;
        this.hash = hash;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public IDS.ItemType getType() {
        return type;
    }

    public String getHash() {
        return hash;
    }
}
