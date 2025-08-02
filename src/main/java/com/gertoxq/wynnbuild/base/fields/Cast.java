package com.gertoxq.wynnbuild.base.fields;

import com.gertoxq.wynnbuild.WynnBuild;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum Cast {
    //  ORDER MATTERS
    Warrior("Warrior", "Knight", List.of("Bash", "Charge", "Uppercut", "War Scream"), ItemType.Spear),
    Assassin("Assassin", "Ninja", List.of("Spin Attack", "Dash", "Multi Hit", "Smoke Bomb"), ItemType.Dagger),
    Mage("Mage", "Dark Wizard", List.of("Heal", "Teleport", "Meteor", "null"), ItemType.Wand),
    Archer("Archer", "Hunter", List.of("Arrow Storm", "Escape", "Arrow Bomb", "Arrow Shield"), ItemType.Bow),
    Shaman("Shaman", "Skyseer", List.of("Totem", "Haul", "Aura", "Uproot"), ItemType.Relik);
    public final String name;
    public final List<String> aliases = new ArrayList<>();
    public final String alias;
    public final List<String> abilities;
    public final ItemType weapon;

    Cast(String name, String alias, List<String> abilityNames, ItemType weapon) {
        this.name = name;
        this.abilities = abilityNames;
        this.alias = alias;
        this.weapon = weapon;
        this.aliases.add(name);
        this.aliases.add(alias);
    }

    public static Cast findByWeapon(ItemType weapon) {
        if (!weapon.isWeapon()) {
            WynnBuild.warn("{} is not a weapon type", weapon.name());
            return null;
        }
        return Arrays.stream(Cast.values()).filter(cast1 -> cast1.weapon.equals(weapon)).findAny().orElseGet(() -> {
            WynnBuild.error("No cast found for weapon type: {}", weapon.name());
            throw new RuntimeException("No cast found for weapon type");
        });
    }

    public static Optional<@Nullable Cast> find(String alias) {
        return Arrays.stream(Cast.values()).filter(cast -> cast.aliases.stream().map(String::toLowerCase).toList().contains(alias.toLowerCase())).findFirst();
    }
}
