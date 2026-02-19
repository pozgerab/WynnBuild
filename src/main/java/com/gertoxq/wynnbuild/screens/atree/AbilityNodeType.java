package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;

import static com.gertoxq.wynnbuild.util.Utils.between;

public enum AbilityNodeType {

    WHITE(158),
    YELLOW(162),
    PURPLE(166),
    BLUE(170),
    RED(174),
    ABILITY(178); // archer: 178-180 assassin: 181-183, mage: 184-186, shaman: 187-189, warrior: 190-192,

    private final int unreachableModelData;

    AbilityNodeType(int unreachableModelData) {
        this.unreachableModelData = unreachableModelData;
    }

    public static AbilityNodeType getType(int customModelData) {
        if (!between(158, 192, customModelData)) {
            WynnBuild.error("Invalid custom model data for ability node: " + customModelData);
            return WHITE;
        }
        else if (178 <= customModelData) return ABILITY;
        else if (174 <= customModelData) return RED;
        else if (170 <= customModelData) return BLUE;
        else if (166 <= customModelData) return PURPLE;
        else if (162 <= customModelData) return YELLOW;
        else return WHITE;
    }

    public int unreachable() {
        return unreachableModelData;
    }

    public int unlockable() {
        return unreachableModelData + 1;
    }

    public int unlocked() {
        return unreachableModelData + 2;
    }

    public int blocked() {
        return unreachableModelData + 3;
    }
}
