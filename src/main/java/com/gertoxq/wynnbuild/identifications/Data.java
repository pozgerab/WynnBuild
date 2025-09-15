package com.gertoxq.wynnbuild.identifications;

import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.gear.type.GearType;

import java.util.List;
import java.util.Set;

public final class Data {
    public static final List<String> ci_save_order = List.of(
            "name", "lore", "tier", "set", "slots", "type",
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

    public static final Set<String> rolledIDs = Set.of("hprPct", "mr",
            "sdPct", "mdPct", "ls", "ms", "xpb", "lb", "ref", "thorns", "expd", "spd", "atkTier", "poison", "hpBonus", "spRegen", "eSteal",
            "hprRaw", "sdRaw", "mdRaw", "fDamPct", "wDamPct", "aDamPct", "tDamPct", "eDamPct", "fDefPct", "wDefPct", "aDefPct",
            "tDefPct", "eDefPct", "spPct1", "spRaw1", "spPct2", "spRaw2", "spPct3", "spRaw3", "spPct4", "spRaw4", "rSdRaw", "sprint",
            "sprintReg", "jh", "lq", "gXp", "gSpd", "eMdPct", "eMdRaw", "eSdPct", "eSdRaw", "eDamRaw", "eDamAddMin", "eDamAddMax", "tMdPct",
            "tMdRaw", "tSdPct", "tSdRaw", "tDamRaw", "tDamAddMin", "tDamAddMax", "wMdPct", "wMdRaw", "wSdPct", "wSdRaw", "wDamRaw",
            "wDamAddMin", "wDamAddMax", "fMdPct", "fMdRaw", "fSdPct", "fSdRaw", "fDamRaw", "fDamAddMin", "fDamAddMax", "aMdPct",
            "aMdRaw", "aSdPct", "aSdRaw", "aDamRaw", "aDamAddMin", "aDamAddMax", "nMdPct", "nMdRaw", "nSdPct", "nSdRaw", "nDamPct",
            "nDamRaw", "nDamAddMin", "nDamAddMax", "damPct", "damRaw", "damAddMin", "damAddMax", "rMdPct", "rMdRaw",
            "rSdPct", "rDamPct", "rDamRaw", "rDamAddMin", "rDamAddMax", "critDamPct", "spPct1Final", "spPct2Final",
            "spPct3Final", "spPct4Final", "healPct", "kb", "weakenEnemy", "slowEnemy", "rDefPct", "maxMana",
            "mainAttackRange");

    public static final List<GearTier> gearTiers = List.of(GearTier.NORMAL, GearTier.UNIQUE, GearTier.RARE, GearTier.LEGENDARY, GearTier.FABLED, GearTier.MYTHIC, GearTier.CRAFTED);

    public static final List<GearType> gearTypes = List.of(GearType.HELMET, GearType.CHESTPLATE, GearType.LEGGINGS, GearType.BOOTS,
            GearType.RING, GearType.BRACELET, GearType.NECKLACE, GearType.WAND, GearType.SPEAR, GearType.BOW, GearType.DAGGER, GearType.RELIK);

    public static final List<ClassType> classTypes = List.of(ClassType.WARRIOR, ClassType.ASSASSIN, ClassType.MAGE, ClassType.ARCHER, ClassType.SHAMAN);

    public static final List<String> damages = List.of("nDam", "eDam", "tDam", "wDam", "fDam", "aDam");

    private Data() {

    }
}