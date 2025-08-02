package com.gertoxq.wynnbuild.identifications;

@SuppressWarnings("unused")
public interface RolledIDs {
    RolledID HPR_PCT = new RolledID("hprPct", "Health Regen", Metric.PERCENT);
    RolledID MR = new RolledID("mr", "Mana Regen", Metric.PERXS);
    RolledID SD_PCT = new RolledID("sdPct", "Spell Damage", Metric.PERCENT);
    RolledID MD_PCT = new RolledID("mdPct", "Main Attack Damage", Metric.PERCENT);
    RolledID LS = new RolledID("ls", "Life Steal", Metric.PERXS);
    RolledID MS = new RolledID("ms", "Mana Steal", Metric.PERXS);
    RolledID XPB = new RolledID("xpb", "XP Bonus", Metric.PERCENT);
    RolledID LB = new RolledID("lb", "Loot Bonus", Metric.PERCENT);
    RolledID REF = new RolledID("ref", "Reflection", Metric.PERCENT);
    RolledID THORNS = new RolledID("thorns", "Thorns", Metric.PERCENT);
    RolledID EXPD = new RolledID("expd", "Exploding", Metric.PERCENT);
    RolledID SPD = new RolledID("spd", "Walk Speed", Metric.PERCENT);
    RolledID ATK_TIER = new RolledID("atkTier", "tier Attack Speed", Metric.RAW);
    RolledID POISON = new RolledID("poison", "Poison", Metric.PERXS);
    RolledID HP_BONUS = new RolledID("hpBonus", "Health", Metric.RAW);
    RolledID SP_REGEN = new RolledID("spRegen", "Soul Point Regen", Metric.PERCENT);
    RolledID E_STEAL = new RolledID("eSteal", "Stealing", Metric.PERCENT);
    RolledID HPR_RAW = new RolledID("hprRaw", "Health Regen", Metric.RAW);
    RolledID SD_RAW = new RolledID("sdRaw", "Spell Damage", Metric.RAW);
    RolledID MD_RAW = new RolledID("mdRaw", "Main Attack Damage", Metric.RAW);
    RolledID FDAM_PCT = new RolledID("fDamPct", "Fire Damage", Metric.PERCENT);
    RolledID WDAM_PCT = new RolledID("wDamPct", "Water Damage", Metric.PERCENT);
    RolledID ADAM_PCT = new RolledID("aDamPct", "Air Damage", Metric.PERCENT);
    RolledID TDAM_PCT = new RolledID("tDamPct", "Thunder Damage", Metric.PERCENT);
    RolledID EDAM_PCT = new RolledID("eDamPct", "Earth Damage", Metric.PERCENT);
    RolledID FDEF_PCT = new RolledID("fDefPct", "Fire Defence", Metric.PERCENT);
    RolledID WDEF_PCT = new RolledID("wDefPct", "Water Defence", Metric.PERCENT);
    RolledID ADEF_PCT = new RolledID("aDefPct", "Air Defence", Metric.PERCENT);
    RolledID TDEF_PCT = new RolledID("tDefPct", "Thunder Defence", Metric.PERCENT);
    RolledID EDEF_PCT = new RolledID("eDefPct", "Earth Defence", Metric.PERCENT);

    RolledID SP_PCT1 = new RolledID("spPct1", "1st Spell Cost", Metric.PERCENT);
    RolledID SP_RAW1 = new RolledID("spRaw1", "1st Spell Cost", Metric.RAW);
    RolledID SP_PCT2 = new RolledID("spPct2", "2nd Spell Cost", Metric.PERCENT);
    RolledID SP_RAW2 = new RolledID("spRaw2", "2nd Spell Cost", Metric.RAW);
    RolledID SP_PCT3 = new RolledID("spPct3", "3rd Spell Cost", Metric.PERCENT);
    RolledID SP_RAW3 = new RolledID("spRaw3", "3rd Spell Cost", Metric.RAW);
    RolledID SP_PCT4 = new RolledID("spPct4", "4th Spell Cost", Metric.PERCENT);
    RolledID SP_RAW4 = new RolledID("spRaw4", "4th Spell Cost", Metric.RAW);

    RolledID RSD_RAW = new RolledID("rSdRaw", "Elemental Spell Damage", Metric.RAW);
    RolledID SPRINT = new RolledID("sprint", "Sprint", Metric.PERCENT);
    RolledID SPRINT_REG = new RolledID("sprintReg", "Sprint Regen", Metric.PERCENT);
    RolledID JH = new RolledID("jh", "Jump Height", Metric.RAW);
    RolledID LQ = new RolledID("lq", "Loot Quality", Metric.PERCENT);
    RolledID GXP = new RolledID("gXp", "Gathering XP Bonus", Metric.PERCENT);
    RolledID GSPD = new RolledID("gSpd", "Gathering Speed Bonus", Metric.PERCENT);

    RolledID MAX_MANA = new RolledID("maxMana", "Max Mana", Metric.RAW);
    RolledID CRITDAM_PCT = new RolledID("critDamPct", "Crit Damage Bonus", Metric.PERCENT);
    RolledID NSD_RAW = new RolledID("nSdRaw", "Neutral Spell Damage", Metric.RAW);
    RolledID ESD_RAW = new RolledID("eSdRaw", "Earth Spell Damage", Metric.RAW);
    RolledID TSD_RAW = new RolledID("tSdRaw", "Thunder Spell Damage", Metric.RAW);
    RolledID WSD_RAW = new RolledID("wSdRaw", "Water Spell Damage", Metric.RAW);
    RolledID FSD_RAW = new RolledID("fSdRaw", "Fire Spell Damage", Metric.RAW);
    RolledID ASD_RAW = new RolledID("aSdRaw", "Air Spell Damage", Metric.RAW);
    RolledID RSD_PCT = new RolledID("rSdPct", "Elemental Spell Damage", Metric.PERCENT);
    RolledID NSD_PCT = new RolledID("nSdPct", "Neutral Spell Damage", Metric.PERCENT);
    RolledID ESD_PCT = new RolledID("eSdPct", "Earth Spell Damage", Metric.PERCENT);
    RolledID TSD_PCT = new RolledID("tSdPct", "Thunder Spell Damage", Metric.PERCENT);
    RolledID WSD_PCT = new RolledID("wSdPct", "Water Spell Damage", Metric.PERCENT);
    RolledID FSD_PCT = new RolledID("fSdPct", "Fire Spell Damage", Metric.PERCENT);
    RolledID ASD_PCT = new RolledID("aSdPct", "Air Spell Damage", Metric.PERCENT);

    RolledID RMD_RAW = new RolledID("rMdRaw", "Elemental Main Attack Damage", Metric.RAW);
    RolledID NMD_RAW = new RolledID("nMdRaw", "Neutral Main Attack Damage", Metric.RAW);
    RolledID EMD_RAW = new RolledID("eMdRaw", "Earth Main Attack Damage", Metric.RAW);
    RolledID TMD_RAW = new RolledID("tMdRaw", "Thunder Main Attack Damage", Metric.RAW);
    RolledID WMD_RAW = new RolledID("wMdRaw", "Water Main Attack Damage", Metric.RAW);
    RolledID FMD_RAW = new RolledID("fMdRaw", "Fire Main Attack Damage", Metric.RAW);
    RolledID AMD_RAW = new RolledID("aMdRaw", "Air Main Attack Damage", Metric.RAW);

    RolledID RMD_PCT = new RolledID("rMdPct", "Elemental Main Attack Damage", Metric.PERCENT);
    RolledID NMD_PCT = new RolledID("nMdPct", "Neutral Main Attack Damage", Metric.PERCENT);
    RolledID EMD_PCT = new RolledID("eMdPct", "Earth Main Attack Damage", Metric.PERCENT);
    RolledID TMD_PCT = new RolledID("tMdPct", "Thunder Main Attack Damage", Metric.PERCENT);
    RolledID WMD_PCT = new RolledID("wMdPct", "Water Main Attack Damage", Metric.PERCENT);
    RolledID FMD_PCT = new RolledID("fMdPct", "Fire Main Attack Damage", Metric.PERCENT);
    RolledID AMD_PCT = new RolledID("aMdPct", "Air Main Attack Damage", Metric.PERCENT);

    RolledID DAM_RAW = new RolledID("damRaw", "Damage", Metric.RAW);

    RolledID RDAM_RAW = new RolledID("rDamRaw", "Elemental Damage", Metric.RAW);
    RolledID NDAM_RAW = new RolledID("nDamRaw", "Neutral Damage", Metric.RAW);
    RolledID EDAM_RAW = new RolledID("eDamRaw", "Earth Damage", Metric.RAW);
    RolledID TDAM_RAW = new RolledID("tDamRaw", "Thunder Damage", Metric.RAW);
    RolledID WDAM_RAW = new RolledID("wDamRaw", "Water Damage", Metric.RAW);
    RolledID FDAM_RAW = new RolledID("fDamRaw", "Fire Damage", Metric.RAW);
    RolledID ADAM_RAW = new RolledID("aDamRaw", "Air Damage", Metric.RAW);

    RolledID DAM_PCT = new RolledID("damPct", "Damage", Metric.PERCENT);
    RolledID RDAM_PCT = new RolledID("rDamPct", "Elemental Damage", Metric.PERCENT);
    RolledID NDAM_PCT = new RolledID("nDamPct", "Neutral Damage", Metric.PERCENT);

    RolledID HEAL_PCT = new RolledID("healPct", "Healing Efficiency", Metric.PERCENT);
    RolledID MAIN_ATTACK_RANGE = new RolledID("mainAttackRange", "Main Attack Range", Metric.PERCENT);

    RolledID KB = new RolledID("kb", "Knockback", Metric.PERCENT);
    RolledID WEAKEN_ENEMY = new RolledID("weakenEnemy", "Weaken Enemy", Metric.PERCENT);
    RolledID SLOW_ENEMY = new RolledID("slowEnemy", "Slow Enemy", Metric.PERCENT);
    RolledID RDEF_PCT = new RolledID("rDefPct", "Elemental Defense", Metric.PERCENT);
}
