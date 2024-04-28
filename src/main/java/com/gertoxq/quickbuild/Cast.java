package com.gertoxq.quickbuild;

public enum Cast {
    WARRIOR("Warrior"),
    ARCHER("Archer"),
    ASSASSIN("Assassin"),
    MAGE("Mage"),
    SHAMAN("Shaman");
    public final String name;
    Cast(String name) {
        this.name = name;
    }
}
