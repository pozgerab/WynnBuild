package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.Tier;

import java.util.Arrays;
import java.util.List;

public final class Data {
    public static final List<String> ci_save_order = List.of("name", "lore", "tier", "set", "slots", "type",
            "material", "drop", "quest",
            "nDam", "fDam", "wDam", "aDam", "tDam", "eDam",
            "atkSpd", "hp",
            "fDef", "wDef", "aDef", "tDef", "eDef",
            "lvl", "classReq",
            "strReq", "dexReq", "intReq", "defReq", "agiReq",
            "str", "dex", "int", "agi", "def", "id",
            "skillpoints", "reqs",
// NOTE: THESE ARE UNUSED.
            "nDam_", "fDam_", "wDam_", "aDam_", "tDam_", "eDam_",
            "majorIds", "hprPct", "mr",
            "sdPct", "mdPct",
            "ls", "ms", "xpb", "lb",
            "ref", "thorns", "expd", "spd", "atkTier", "poison", "hpBonus", "spRegen", "eSteal", "hprRaw",
            "sdRaw", "mdRaw",
            "fDamPct", "wDamPct", "aDamPct", "tDamPct", "eDamPct",
            "fDefPct", "wDefPct", "aDefPct", "tDefPct", "eDefPct",
            "spPct1", "spRaw1", "spPct2", "spRaw2", "spPct3", "spRaw3", "spPct4", "spRaw4",
            "rSdRaw",
            "sprint", "sprintReg", "jh", "lq", "gXp", "gSpd", "durability", "duration", "charges", "maxMana", "critDamPct",
            /*"sdRaw", "rSdRaw",*/ "nSdRaw", "eSdRaw", "tSdRaw", "wSdRaw", "fSdRaw", "aSdRaw",
            /*"sdPct",*/ "rSdPct", "nSdPct", "eSdPct", "tSdPct", "wSdPct", "fSdPct", "aSdPct",
            /*"mdRaw",*/ "rMdRaw", "nMdRaw", "eMdRaw", "tMdRaw", "wMdRaw", "fMdRaw", "aMdRaw",
            /*"mdPct",*/ "rMdPct", "nMdPct", "eMdPct", "tMdPct", "wMdPct", "fMdPct", "aMdPct",
            "damRaw", "rDamRaw", "nDamRaw", "eDamRaw", "tDamRaw", "wDamRaw", "fDamRaw", "aDamRaw",
            "damPct", "rDamPct", "nDamPct", /*"eDamPct", "tDamPct", "wDamPct", "fDamPct", "aDamPct",*/
            "healPct",
            "mainAttackRange", "kb", "weakenEnemy", "slowEnemy",
            "rDefPct"
    );
    public static final List<ID> ci_save_order_ids = ci_save_order.stream().map(string -> {
        ID id = ID.getByName(string);
        if (id == null && !string.endsWith("_")) {
            WynnBuild.error("Invalid id string: {}", string);
        }
        return id;
    }).toList();
    public static final List<String> all_types = List.of("Helmet", "Chestplate", "Leggings", "Boots",
            "Ring", "Bracelet", "Necklace", "Wand", "Spear", "Bow", "Dagger", "Relik", "Potion", "Scroll", "Food",
            "WeaponTome", "ArmorTome", "GuildTome", "LootrunTome", "GatherXpTome", "DungeonXpTome", "MobXpTome");
    public static final List<String> attackSpeeds = Arrays.stream(AtkSpd.values()).map(Enum::name).toList();
    public static final List<String> damages = List.of("nDam", "eDam", "tDam", "wDam", "fDam", "aDam");
    public static final List<String> tiers = Arrays.stream(Tier.values()).map(Enum::name).toList();
    public static final List<String> classes = Arrays.stream(Cast.values()).map(Enum::name).toList();

    private Data() {

    }
}