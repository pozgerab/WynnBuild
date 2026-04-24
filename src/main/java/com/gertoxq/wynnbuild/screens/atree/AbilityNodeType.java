package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;

import static com.gertoxq.wynnbuild.util.Utils.between;

public enum AbilityNodeType {

    WHITE(158),
    YELLOW(162),
    PURPLE(166),
    BLUE(170),
    RED(174),
    ULTIMATE(178); // archer: 178-180 assassin: 181-183, mage: 184-186, shaman: 187-189, warrior: 190-192,

    // warrior: bm: 230-232, cd: 238-240, pl: 246-248
    // mage:    lb: 242-244, rw: 250-252, ac: 226-228
    // shaman:  su: 266-268, ri: 254-256, ac: 218-220
    // assassin:sh: 258-260,              ac: 222-224
    // archer:  bo: 234-236, tr: 270-272, sh: 262-264

    private final int unreachableModelData;

    AbilityNodeType(int unreachableModelData) {
        this.unreachableModelData = unreachableModelData;
    }

    public static AbilityNodeType getType(int customModelData) {
        if (!between(158, 192, customModelData)) {
            WynnBuild.error("Invalid custom model data for ability node: " + customModelData);
            return WHITE;
        } else if (178 <= customModelData) return ULTIMATE;
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
