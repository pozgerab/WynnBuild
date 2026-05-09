package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;

import java.util.Set;

import static com.gertoxq.wynnbuild.util.Utils.between;

public enum AbilityNodeType {

    ABILITY,
    /*                unreachable, unlockable, unlocked
        archer    -   73-75
        assassin  -   76-78
        mage      -   83-85
        shaman    -   94-96
        warrior   -   97-99
     */
                //  unreachable, unlockable, blocked, unlocked
    WHITE,     // 100-103
    YELLOW,    // 104-107
    PURPLE,    // 86-89
    BLUE,      // 79-82
    RED,       // 90-93
    ULTIMATE;
    // warrior: fallen: 147, battle-monk: 139, paladin: 155
    // mage:    light bender: 151, riftwalker: 159, arcanist: 135
    // shaman:  summoner: 175, ritualist: 163, acolyte: 127
    // assassin:shadestepper: 167, trickster: 183 acrobat: 131
    // archer:  boltslinger: 143, trapper: 179, sharpshooter: 171
    /* ABC      73-107 + 127-186
    73-75: archer
    76-78: assassin
    79-82: blue
    83-85: mage
    86-89: purple
    90-93: red
    94-96: shaman
    97-99: warrior
    100-103: white
    104-107: yellow

    ULTIMATES 127-186
    127-130: shaman acolyte
    131-134: assassin acrobat
    135-138: mage arcanist
    139-142: warrior battle-monk
    143-146: archer boltslinger
    147-150: warrior fallen
    151-154: mage light bender
    155-158: warrior paladin
    159-162: mage riftwalker
    163-166: shaman ritualist
    167-170: assassin shadestepper
    171-174: archer sharpshooter
    175-178: shaman summoner
    179-182: archer trapper
    183-186: assassin trickster
     */

    public static final Set<Integer> abilityModelData = Set.of(73, 74, 75, 76, 77, 78, 83, 84, 85, 94, 95, 96, 97, 98, 99);

    public static AbilityNodeType getType(int customModelData) {
        if (between(127, 186, customModelData)) return ULTIMATE;
        if (abilityModelData.contains(customModelData)) return ABILITY;
        if (between(100, 103, customModelData)) return WHITE;
        if (between(104, 107, customModelData)) return YELLOW;
        if (between(86, 89, customModelData)) return PURPLE;
        if (between(79, 82, customModelData)) return BLUE;
        if (between(90, 93, customModelData)) return RED;

        WynnBuild.error("Invalid custom model data for ability node: " + customModelData);
        return WHITE;
    }

}
