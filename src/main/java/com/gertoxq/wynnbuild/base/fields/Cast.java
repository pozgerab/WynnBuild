package com.gertoxq.wynnbuild.base.fields;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.models.character.type.ClassType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Cast {
    //  ORDER MATTERS
    Warrior("Knight", List.of("Bash", "Charge", "Uppercut", "War Scream"), ItemType.Spear),
    Assassin("Ninja", List.of("Spin Attack", "Dash", "Multi Hit", "Smoke Bomb"), ItemType.Dagger),
    Mage("Dark Wizard", List.of("Heal", "Teleport", "Meteor", "Ice Snake"), ItemType.Wand),
    Archer("Hunter", List.of("Arrow Storm", "Escape", "Arrow Bomb", "Arrow Shield"), ItemType.Bow),
    Shaman("Skyseer", List.of("Totem", "Haul", "Aura", "Uproot"), ItemType.Relik);
    public final List<String> aliases = new ArrayList<>();
    public final String alias;
    public final List<String> abilities;
    public final ItemType weapon;

    Cast(String alias, List<String> abilityNames, ItemType weapon) {
        this.abilities = abilityNames;
        this.alias = alias;
        this.weapon = weapon;
        this.aliases.add(name());
        this.aliases.add(alias);
    }

    public static Cast fromType(ClassType classType) {
        return valueOf(classType.getName());
    }

    public static ClassType findByWeapon(ItemType weapon) {
        if (!weapon.isWeapon()) {
            WynnBuild.warn("{} is not a weapon type", weapon.name());
            return null;
        }

        return ClassType.valueOf(Arrays.stream(Cast.values()).filter(cast1 -> cast1.weapon.equals(weapon)).findAny().orElseGet(() -> {
            WynnBuild.error("No cast found for weapon type: {}", weapon.name());
            throw new RuntimeException("No cast found for weapon type");
        }).name().toUpperCase());
    }
}
