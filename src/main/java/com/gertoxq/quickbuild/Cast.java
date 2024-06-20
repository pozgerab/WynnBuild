package com.gertoxq.quickbuild;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Cast {
    WARRIOR("Warrior", "Knight"),
    ARCHER("Archer", "Hunter"),
    ASSASSIN("Assassin", "Ninja"),
    MAGE("Mage", "Dark Wizard"),
    SHAMAN("Shaman", "Skyseer");
    public final String name;
    public final List<String> aliases = new ArrayList<>();

    Cast(String name, String... aliases) {
        this.name = name;
        this.aliases.add(name);
        this.aliases.addAll(List.of(aliases));
    }

    @Nullable
    public static Cast find(String alias) {
        return Arrays.stream(Cast.values()).filter(cast -> cast.aliases.stream().map(String::toLowerCase).toList().contains(alias.toLowerCase())).findFirst().orElse(null);
    }
}
