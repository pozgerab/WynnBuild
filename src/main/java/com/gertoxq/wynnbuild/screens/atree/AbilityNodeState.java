package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;

import static com.gertoxq.wynnbuild.util.Utils.between;

public enum AbilityNodeState {

    UNLOCKED, UNLOCKABLE, LOCKED, BLOCKED;

    public static AbilityNodeState getType(int customModelData) {
        if (!between(158, 192, customModelData)) {
            WynnBuild.error("Invalid custom model data for ability node: " + customModelData);
            return LOCKED;
        } else if (178 <= customModelData) {
            return switch (customModelData % 3) {
                case 0 -> UNLOCKED;
                case 2 -> UNLOCKABLE;
                default -> LOCKED; // case 1
            };
        } else {
            return switch (customModelData % 4) {
                case 3 -> UNLOCKABLE;
                case 0 -> UNLOCKED;
                case 1 -> BLOCKED;
                default -> LOCKED; // case 2
            };
        }
    }
}
