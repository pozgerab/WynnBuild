package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metrics;

@SuppressWarnings("unused")
public interface RolledIDs {
    RolledID HPR_PCT = new RolledID("hprPct", "Health Regen", Metrics.PERCENT);
    RolledID MR = new RolledID("mr", "Mana Regen", Metrics.PER5S);
    RolledID SD_PCT = new RolledID("sdPct", "Spell Damage", Metrics.PERCENT);
    RolledID MD_PCT = new RolledID("mdPct", "Main Attack Damage", Metrics.PERCENT);
    RolledID LS = new RolledID("ls", "Life Steal", Metrics.PER3S);
    RolledID MS = new RolledID("ms", "Mana Steal", Metrics.PER3S);
    RolledID XPB = new RolledID("xpb", "XP Bonus", Metrics.PERCENT);
    RolledID LB = new RolledID("lb", "Loot Bonus", Metrics.PERCENT);
    RolledID REF = new RolledID("ref", "Reflection", Metrics.PERCENT);
    RolledID THORNS = new RolledID("thorns", "Thorns", Metrics.PERCENT);
    RolledID EXPD = new RolledID("expd", "Exploding", Metrics.PERCENT);
    RolledID SPD = new RolledID("spd", "Walk Speed", Metrics.PERCENT);
    RolledID ATK_TIER = new RolledID("atkTier", "tier Attack Speed", Metrics.RAW_BONUS);
    RolledID POISON = new RolledID("poison", "Poison", Metrics.PER3S);
    RolledID HP_BONUS = new RolledID("hpBonus", "Health", Metrics.RAW_BONUS);
    RolledID SP_REGEN = new RolledID("spRegen", "Soul Point Regen", Metrics.PERCENT);
    RolledID E_STEAL = new RolledID("eSteal", "Stealing", Metrics.PERCENT);
    RolledID HPR_RAW = new RolledID("hprRaw", "Health Regen", Metrics.RAW_BONUS);
    RolledID SD_RAW = new RolledID("sdRaw", "Spell Damage", Metrics.RAW_BONUS);
    RolledID MD_RAW = new RolledID("mdRaw", "Main Attack Damage", Metrics.RAW_BONUS);
    RolledID FDAM_PCT = new RolledID("fDamPct", "Fire Damage", Metrics.PERCENT);
    RolledID WDAM_PCT = new RolledID("wDamPct", "Water Damage", Metrics.PERCENT);
    RolledID ADAM_PCT = new RolledID("aDamPct", "Air Damage", Metrics.PERCENT);
    RolledID TDAM_PCT = new RolledID("tDamPct", "Thunder Damage", Metrics.PERCENT);
    RolledID EDAM_PCT = new RolledID("eDamPct", "Earth Damage", Metrics.PERCENT);
    RolledID FDEF_PCT = new RolledID("fDefPct", "Fire Defence", Metrics.PERCENT);
    RolledID WDEF_PCT = new RolledID("wDefPct", "Water Defence", Metrics.PERCENT);
    RolledID ADEF_PCT = new RolledID("aDefPct", "Air Defence", Metrics.PERCENT);
    RolledID TDEF_PCT = new RolledID("tDefPct", "Thunder Defence", Metrics.PERCENT);
    RolledID EDEF_PCT = new RolledID("eDefPct", "Earth Defence", Metrics.PERCENT);

    RolledID SP_PCT1 = new RolledID("spPct1", "1st Spell Cost", Metrics.PERCENT);
    RolledID SP_RAW1 = new RolledID("spRaw1", "1st Spell Cost", Metrics.RAW_BONUS);
    RolledID SP_PCT2 = new RolledID("spPct2", "2nd Spell Cost", Metrics.PERCENT);
    RolledID SP_RAW2 = new RolledID("spRaw2", "2nd Spell Cost", Metrics.RAW_BONUS);
    RolledID SP_PCT3 = new RolledID("spPct3", "3rd Spell Cost", Metrics.PERCENT);
    RolledID SP_RAW3 = new RolledID("spRaw3", "3rd Spell Cost", Metrics.RAW_BONUS);
    RolledID SP_PCT4 = new RolledID("spPct4", "4th Spell Cost", Metrics.PERCENT);
    RolledID SP_RAW4 = new RolledID("spRaw4", "4th Spell Cost", Metrics.RAW_BONUS);

    RolledID RSD_RAW = new RolledID("rSdRaw", "Elemental Spell Damage", Metrics.RAW_BONUS);
    RolledID SPRINT = new RolledID("sprint", "Sprint", Metrics.PERCENT);
    RolledID SPRINT_REG = new RolledID("sprintReg", "Sprint Regen", Metrics.PERCENT);
    RolledID JH = new RolledID("jh", "Jump Height", Metrics.RAW_BONUS);
    RolledID LQ = new RolledID("lq", "Loot Quality", Metrics.PERCENT);
    RolledID GXP = new RolledID("gXp", "Gather XP Bonus", Metrics.PERCENT);
    RolledID GSPD = new RolledID("gSpd", "Gather Speed", Metrics.PERCENT);

    RolledID MAX_MANA = new RolledID("maxMana", "Max Mana", Metrics.RAW_BONUS);
    RolledID CRITDAM_PCT = new RolledID("critDamPct", "Crit Damage Bonus", Metrics.PERCENT);
    RolledID NSD_RAW = new RolledID("nSdRaw", "Neutral Spell Damage", Metrics.RAW_BONUS);
    RolledID ESD_RAW = new RolledID("eSdRaw", "Earth Spell Damage", Metrics.RAW_BONUS);
    RolledID TSD_RAW = new RolledID("tSdRaw", "Thunder Spell Damage", Metrics.RAW_BONUS);
    RolledID WSD_RAW = new RolledID("wSdRaw", "Water Spell Damage", Metrics.RAW_BONUS);
    RolledID FSD_RAW = new RolledID("fSdRaw", "Fire Spell Damage", Metrics.RAW_BONUS);
    RolledID ASD_RAW = new RolledID("aSdRaw", "Air Spell Damage", Metrics.RAW_BONUS);
    RolledID RSD_PCT = new RolledID("rSdPct", "Elemental Spell Damage", Metrics.PERCENT);
    RolledID NSD_PCT = new RolledID("nSdPct", "Neutral Spell Damage", Metrics.PERCENT);
    RolledID ESD_PCT = new RolledID("eSdPct", "Earth Spell Damage", Metrics.PERCENT);
    RolledID TSD_PCT = new RolledID("tSdPct", "Thunder Spell Damage", Metrics.PERCENT);
    RolledID WSD_PCT = new RolledID("wSdPct", "Water Spell Damage", Metrics.PERCENT);
    RolledID FSD_PCT = new RolledID("fSdPct", "Fire Spell Damage", Metrics.PERCENT);
    RolledID ASD_PCT = new RolledID("aSdPct", "Air Spell Damage", Metrics.PERCENT);

    RolledID RMD_RAW = new RolledID("rMdRaw", "Elemental Main Attack Damage", Metrics.RAW_BONUS);
    RolledID NMD_RAW = new RolledID("nMdRaw", "Neutral Main Attack Damage", Metrics.RAW_BONUS);
    RolledID EMD_RAW = new RolledID("eMdRaw", "Earth Main Attack Damage", Metrics.RAW_BONUS);
    RolledID TMD_RAW = new RolledID("tMdRaw", "Thunder Main Attack Damage", Metrics.RAW_BONUS);
    RolledID WMD_RAW = new RolledID("wMdRaw", "Water Main Attack Damage", Metrics.RAW_BONUS);
    RolledID FMD_RAW = new RolledID("fMdRaw", "Fire Main Attack Damage", Metrics.RAW_BONUS);
    RolledID AMD_RAW = new RolledID("aMdRaw", "Air Main Attack Damage", Metrics.RAW_BONUS);

    RolledID RMD_PCT = new RolledID("rMdPct", "Elemental Main Attack Damage", Metrics.PERCENT);
    RolledID NMD_PCT = new RolledID("nMdPct", "Neutral Main Attack Damage", Metrics.PERCENT);
    RolledID EMD_PCT = new RolledID("eMdPct", "Earth Main Attack Damage", Metrics.PERCENT);
    RolledID TMD_PCT = new RolledID("tMdPct", "Thunder Main Attack Damage", Metrics.PERCENT);
    RolledID WMD_PCT = new RolledID("wMdPct", "Water Main Attack Damage", Metrics.PERCENT);
    RolledID FMD_PCT = new RolledID("fMdPct", "Fire Main Attack Damage", Metrics.PERCENT);
    RolledID AMD_PCT = new RolledID("aMdPct", "Air Main Attack Damage", Metrics.PERCENT);

    RolledID DAM_RAW = new RolledID("damRaw", "Damage", Metrics.RAW_BONUS);

    RolledID RDAM_RAW = new RolledID("rDamRaw", "Elemental Damage", Metrics.RAW_BONUS);
    RolledID NDAM_RAW = new RolledID("nDamRaw", "Neutral Damage", Metrics.RAW_BONUS);
    RolledID EDAM_RAW = new RolledID("eDamRaw", "Earth Damage", Metrics.RAW_BONUS);
    RolledID TDAM_RAW = new RolledID("tDamRaw", "Thunder Damage", Metrics.RAW_BONUS);
    RolledID WDAM_RAW = new RolledID("wDamRaw", "Water Damage", Metrics.RAW_BONUS);
    RolledID FDAM_RAW = new RolledID("fDamRaw", "Fire Damage", Metrics.RAW_BONUS);
    RolledID ADAM_RAW = new RolledID("aDamRaw", "Air Damage", Metrics.RAW_BONUS);

    RolledID DAM_PCT = new RolledID("damPct", "Damage", Metrics.PERCENT);
    RolledID RDAM_PCT = new RolledID("rDamPct", "Elemental Damage", Metrics.PERCENT);
    RolledID NDAM_PCT = new RolledID("nDamPct", "Neutral Damage", Metrics.PERCENT);

    RolledID HEAL_PCT = new RolledID("healPct", "Healing Efficiency", Metrics.PERCENT);
    RolledID MAIN_ATTACK_RANGE = new RolledID("mainAttackRange", "Main Attack Range", Metrics.PERCENT);

    RolledID KB = new RolledID("kb", "Knockback", Metrics.PERCENT);
    RolledID WEAKEN_ENEMY = new RolledID("weakenEnemy", "Weaken Enemy", Metrics.PERCENT);
    RolledID SLOW_ENEMY = new RolledID("slowEnemy", "Slow Enemy", Metrics.PERCENT);
    RolledID RDEF_PCT = new RolledID("rDefPct", "Elemental Defense", Metrics.PERCENT);
}
