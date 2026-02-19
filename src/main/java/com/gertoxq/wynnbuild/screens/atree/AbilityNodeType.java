package com.gertoxq.wynnbuild.screens.atree;

import static com.gertoxq.wynnbuild.util.Utils.between;

public enum AbilityNodeType {

    WHITE(158),
    YELLOW(162),
    PURPLE(166),
    BLUE(170),
    RED(174),
    ABILITY(184);

    private final int unreachableModelData;

    AbilityNodeType(int unreachableModelData) {
        this.unreachableModelData = unreachableModelData;
    }

    public static AbilityNodeType getType(int customModelData) {
        if (between(WHITE.unreachable(), WHITE.blocked(), customModelData)) return WHITE;
        else if (between(YELLOW.unreachable(), YELLOW.blocked(), customModelData)) return YELLOW;
        else if (between(PURPLE.unreachable(), PURPLE.blocked(), customModelData)) return PURPLE;
        else if (between(BLUE.unreachable(), BLUE.blocked(), customModelData)) return BLUE;
        else if (between(RED.unreachable(), RED.blocked(), customModelData)) return RED;
        else if (between(ABILITY.unreachable(), ABILITY.blocked(), customModelData)) return ABILITY;
        return null;
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
