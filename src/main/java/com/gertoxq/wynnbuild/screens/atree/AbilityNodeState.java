package com.gertoxq.wynnbuild.screens.atree;

import static com.gertoxq.wynnbuild.util.Utils.between;

public enum AbilityNodeState {

    UNREACHABLE, UNLOCKABLE, UNLOCKED, BLOCKED;

    public static AbilityNodeState getType(int customModelData) {
        if (between(127, 186, customModelData)) {
            int remainder = (customModelData - 127) % 4;
            return switch (remainder) {
                case 0 -> UNREACHABLE;
                case 1 -> UNLOCKABLE;
                case 2 -> BLOCKED;
                case 3 -> UNLOCKED;
                default -> throw new IllegalStateException("Unexpected value: " + remainder);
            };
        }
        if (AbilityNodeType.abilityModelData.contains(customModelData)) {
            int number;
            if (between(73, 78, customModelData)) {
                number = customModelData - 73;
            } else if (between(83, 85, customModelData)) {
                number = customModelData - 83;
            } else if (between(94, 99, customModelData)) {
                number = customModelData - 94;
            } else {
                throw new RuntimeException("Cannot hit this block, just in case");
            }
            return switch (number % 3) {
                case 0 -> UNREACHABLE;
                case 1 -> UNLOCKABLE;
                case 2 -> UNLOCKED;
                default -> throw new IllegalStateException("Unexpected value: " + number);
            };
        }
        if (between(79, 107, customModelData)) {
            int number;
            if (between(79, 82, customModelData)) {
                number = customModelData - 79;
            } else if (between(86, 93, customModelData)) {
                number = customModelData - 86;
            } else if (between(100, 107, customModelData)) {
                number = customModelData - 100;
            } else {
                throw new RuntimeException("Cannot hit this block, just in case");
            }
            return switch (number % 4) {
                case 0 -> UNREACHABLE;
                case 1 -> UNLOCKABLE;
                case 2 -> BLOCKED;
                case 3 -> UNLOCKED;
                default -> throw new IllegalStateException("Unexpected value: " + number);
            };
        }

        throw new RuntimeException("Wynnbuild: Custom model data is invalid: "+ customModelData);
    }
}
