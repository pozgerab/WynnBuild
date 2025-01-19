package com.gertoxq.wynnbuild.custom;

import com.gertoxq.wynnbuild.Cast;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;

import static com.gertoxq.wynnbuild.custom.ID.*;

@SuppressWarnings("unused")
public class AllIDs {
    public static final NonRolledID<Boolean> FIXID = new NonRolledID<>(PutOn.ALL, true, "fixID");
    public static final NonRolledID<String> NAME = new NonRolledID<>(PutOn.ALL, "Custom", "name");
    public static final NonRolledID<String> LORE = new NonRolledID<>(PutOn.ALL, "", "lore");
    public static final DoubleID<Tier, String> TIER = new DoubleID<>(PutOn.ALL, "tier", "Tier", Metric.TIER);
    public static final NonRolledID<String> SET = new NonRolledID<>(PutOn.ALL, "", "set");
    public static final NonRolledID<Integer> SLOTS = new NonRolledID<>(PutOn.ALL, 0, "slots");
    public static final DoubleID<ItemType, String> TYPE = new DoubleID<>(PutOn.ALL, "type", " Geartype", Metric.TYPE);
    public static final NonRolledID<String> MATERIAL = new NonRolledID<>(PutOn.ALL, "", "material");
    public static final NonRolledID<String> DROP = new NonRolledID<>(PutOn.ALL, "", "drop");
    public static final NonRolledID<String> QUEST = new NonRolledID<>(PutOn.ALL, "", "quest");
    public static final DoubleID<DoubleID.Range, String> NDAM = new DoubleID<>(PutOn.WEAPON, "nDam", "Neutral Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> FDAM = new DoubleID<>(PutOn.WEAPON, "fDam", "Fire Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> WDAM = new DoubleID<>(PutOn.WEAPON, "wDam", "Water Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> ADAM = new DoubleID<>(PutOn.WEAPON, "aDam", "Air Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> TDAM = new DoubleID<>(PutOn.WEAPON, "tDam", "Thunder Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> EDAM = new DoubleID<>(PutOn.WEAPON, "eDam", "Earth Damage", Metric.RANGE);
    public static final DoubleID<ATKSPDS, String> ATKSPD = new DoubleID<>(PutOn.WEAPON, "atkSpd", " Attack Speed", Metric.ATTACK_SPEED);
    public static final NonRolledID<Integer> HP = new NonRolledID<>(PutOn.ARMOR, 0, "hp", "Health", Metric.RAW);
    public static final NonRolledID<Integer> FDEF = new NonRolledID<>(PutOn.ARMOR, 0, "fDef", "Fire Defence", Metric.RAW);
    public static final NonRolledID<Integer> WDEF = new NonRolledID<>(PutOn.ARMOR, 0, "wDef", "Water Defence", Metric.RAW);
    public static final NonRolledID<Integer> ADEF = new NonRolledID<>(PutOn.ARMOR, 0, "aDef", "Air Defence", Metric.RAW);
    public static final NonRolledID<Integer> TDEF = new NonRolledID<>(PutOn.ARMOR, 0, "tDef", "Thunder Defence", Metric.RAW);
    public static final NonRolledID<Integer> EDEF = new NonRolledID<>(PutOn.ARMOR, 0, "eDef", "Earth Defence", Metric.RAW);
    public static final NonRolledID<Integer> LVL = new NonRolledID<>(PutOn.ALL, 1, "lvl", "Combat Lv. Min", Metric.RAW);
    public static final DoubleID<Cast, String> CLASS_REQ = new DoubleID<>(PutOn.ALL, "classReq", "Class Req", Metric.CAST);
    public static final NonRolledID<Integer> STR_REQ = new NonRolledID<>(PutOn.ALL, 0, "strReq", "Strength Min", Metric.RAW);
    public static final NonRolledID<Integer> DEX_REQ = new NonRolledID<>(PutOn.ALL, 0, "dexReq", "Dexterity Min", Metric.RAW);
    public static final NonRolledID<Integer> INT_REQ = new NonRolledID<>(PutOn.ALL, 0, "intReq", "Intelligence Min", Metric.RAW);
    public static final NonRolledID<Integer> DEF_REQ = new NonRolledID<>(PutOn.ALL, 0, "defReq", "Defence Min", Metric.RAW);
    public static final NonRolledID<Integer> AGI_REQ = new NonRolledID<>(PutOn.ALL, 0, "agiReq", "Agility Min", Metric.RAW);
    public static final NonRolledID<Integer> STR = new NonRolledID<>(PutOn.ALL, 0, "str", "Strength", Metric.RAW);
    public static final NonRolledID<Integer> DEX = new NonRolledID<>(PutOn.ALL, 0, "dex", "Dexterity", Metric.RAW);
    public static final NonRolledID<Integer> INT = new NonRolledID<>(PutOn.ALL, 0, "int", "Intelligence", Metric.RAW);
    public static final NonRolledID<Integer> AGI = new NonRolledID<>(PutOn.ALL, 0, "agi", "Agility", Metric.RAW);
    public static final NonRolledID<Integer> DEF = new NonRolledID<>(PutOn.ALL, 0, "def", "Defence", Metric.RAW);
    public static final NonRolledID<Integer> ID = new NonRolledID<>(PutOn.ALL, 0, "id");
    public static final NonRolledID<IntList> SKILLPOINTS = new NonRolledID<>(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), "skillpoints");
    public static final NonRolledID<IntList> REQS = new NonRolledID<>(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), "reqs");
    public static final NonRolledID<String> NDAM_ = new NonRolledID<>(PutOn.WEAPON, "", "nDam_");
    public static final NonRolledID<String> FDAM_ = new NonRolledID<>(PutOn.WEAPON, "", "fDam_");
    public static final NonRolledID<String> WDAM_ = new NonRolledID<>(PutOn.WEAPON, "", "wDam_");
    public static final NonRolledID<String> ADAM_ = new NonRolledID<>(PutOn.WEAPON, "", "aDam_");
    public static final NonRolledID<String> TDAM_ = new NonRolledID<>(PutOn.WEAPON, "", "tDam_");
    public static final NonRolledID<String> EDAM_ = new NonRolledID<>(PutOn.WEAPON, "", "eDam_");
    public static final NonRolledID<List<String>> MAJOR_IDS = new NonRolledID<>(PutOn.ALL, List.of(), "majorIds");

    public static final NonRolledID<String> DURABILITY = new NonRolledID<>(PutOn.CONSUMABLE, "", "durability", "Durability", Metric.OTHERSTR); //  int-int
    public static final NonRolledID<String> DURATION = new NonRolledID<>(PutOn.CONSUMABLE, "", "duration", "Duration", Metric.OTHERSTR); //    int-int
    public static final NonRolledID<Integer> CHARGES = new NonRolledID<>(PutOn.CONSUMABLE, 0, "charges", "Charges", Metric.OTHERINT);

    public static final RolledID HPR_PCT = new RolledID(PutOn.ALL, 0, "hprPct", "Health Regen", Metric.PERCENT);
    public static final RolledID MR = new RolledID(PutOn.ALL, 0, "mr", "Mana Regen", Metric.PERXS);
    public static final RolledID SD_PCT = new RolledID(PutOn.ALL, 0, "sdPct", "Spell Damage", Metric.PERCENT);
    public static final RolledID MD_PCT = new RolledID(PutOn.ALL, 0, "mdPct", "Main Attack Damage", Metric.PERCENT);
    public static final RolledID LS = new RolledID(PutOn.ALL, 0, "ls", "Life Steal", Metric.PERXS);
    public static final RolledID MS = new RolledID(PutOn.ALL, 0, "ms", "Mana Steal", Metric.PERXS);
    public static final RolledID XPB = new RolledID(PutOn.ALL, 0, "xpb", "XP Bonus", Metric.PERCENT);
    public static final RolledID LB = new RolledID(PutOn.ALL, 0, "lb", "Loot Bonus", Metric.PERCENT);
    public static final RolledID REF = new RolledID(PutOn.ALL, 0, "ref", "Reflection", Metric.PERCENT);
    public static final RolledID THORNS = new RolledID(PutOn.ALL, 0, "thorns", "Thorns", Metric.PERCENT);
    public static final RolledID EXPD = new RolledID(PutOn.ALL, 0, "expd", "Exploding", Metric.PERCENT);
    public static final RolledID SPD = new RolledID(PutOn.ALL, 0, "spd", "Walk Speed", Metric.PERCENT);
    public static final RolledID ATK_TIER = new RolledID(PutOn.ALL, 0, "atkTier", "tier Attack Speed", Metric.RAW);
    public static final RolledID POISON = new RolledID(PutOn.ALL, 0, "poison", "Poison", Metric.PERXS);
    public static final RolledID HP_BONUS = new RolledID(PutOn.ALL, 0, "hpBonus", "Health", Metric.RAW);
    public static final RolledID SP_REGEN = new RolledID(PutOn.ALL, 0, "spRegen", "Soul Point Regen", Metric.PERCENT);
    public static final RolledID E_STEAL = new RolledID(PutOn.ALL, 0, "eSteal", "Stealing", Metric.PERCENT);
    public static final RolledID HPR_RAW = new RolledID(PutOn.ALL, 0, "hprRaw", "Health Regen", Metric.RAW);
    public static final RolledID SD_RAW = new RolledID(PutOn.ALL, 0, "sdRaw", "Spell Damage", Metric.RAW);
    public static final RolledID MD_RAW = new RolledID(PutOn.ALL, 0, "mdRaw", "Main Attack Damage", Metric.RAW);
    public static final RolledID FDAM_PCT = new RolledID(PutOn.ALL, 0, "fDamPct", "Fire Damage", Metric.PERCENT);
    public static final RolledID WDAM_PCT = new RolledID(PutOn.ALL, 0, "wDamPct", "Water Damage", Metric.PERCENT);
    public static final RolledID ADAM_PCT = new RolledID(PutOn.ALL, 0, "aDamPct", "Air Damage", Metric.PERCENT);
    public static final RolledID TDAM_PCT = new RolledID(PutOn.ALL, 0, "tDamPct", "Thunder Damage", Metric.PERCENT);
    public static final RolledID EDAM_PCT = new RolledID(PutOn.ALL, 0, "eDamPct", "Earth Damage", Metric.PERCENT);
    public static final RolledID FDEF_PCT = new RolledID(PutOn.ALL, 0, "fDefPct", "Fire Defence", Metric.PERCENT);
    public static final RolledID WDEF_PCT = new RolledID(PutOn.ALL, 0, "wDefPct", "Water Defence", Metric.PERCENT);
    public static final RolledID ADEF_PCT = new RolledID(PutOn.ALL, 0, "aDefPct", "Air Defence", Metric.PERCENT);
    public static final RolledID TDEF_PCT = new RolledID(PutOn.ALL, 0, "tDefPct", "Thunder Defence", Metric.PERCENT);
    public static final RolledID EDEF_PCT = new RolledID(PutOn.ALL, 0, "eDefPct", "Earth Defence", Metric.PERCENT);

    public static final RolledID SP_PCT1 = new RolledID(PutOn.ALL, 0, "spPct1", "&1", Metric.PERCENT);
    public static final RolledID SP_RAW1 = new RolledID(PutOn.ALL, 0, "spRaw1", "&1", Metric.RAW);
    public static final RolledID SP_PCT2 = new RolledID(PutOn.ALL, 0, "spPct2", "&2", Metric.PERCENT);
    public static final RolledID SP_RAW2 = new RolledID(PutOn.ALL, 0, "spRaw2", "&2", Metric.RAW);
    public static final RolledID SP_PCT3 = new RolledID(PutOn.ALL, 0, "spPct3", "&3", Metric.PERCENT);
    public static final RolledID SP_RAW3 = new RolledID(PutOn.ALL, 0, "spRaw3", "&3", Metric.RAW);
    public static final RolledID SP_PCT4 = new RolledID(PutOn.ALL, 0, "spPct4", "&4", Metric.PERCENT);
    public static final RolledID SP_RAW4 = new RolledID(PutOn.ALL, 0, "spRaw4", "&4", Metric.RAW);

    public static final NonRolledID<Integer> RAINBOW_RAW = new NonRolledID<>(PutOn.ALL, 0, "rainbowRaw");

    public static final RolledID RSD_RAW = new RolledID(PutOn.ALL, 0, "rSdRaw", "Elemental Spell Damage", Metric.RAW);
    public static final RolledID SPRINT = new RolledID(PutOn.ALL, 0, "sprint", "Sprint", Metric.PERCENT);
    public static final RolledID SPRINT_REG = new RolledID(PutOn.ALL, 0, "sprintReg", "Sprint Regen", Metric.PERCENT);
    public static final RolledID JH = new RolledID(PutOn.ALL, 0, "jh", "Jump Height", Metric.RAW);
    public static final RolledID LQ = new RolledID(PutOn.ALL, 0, "lq", "Loot Quality", Metric.PERCENT);
    public static final RolledID GXP = new RolledID(PutOn.ALL, 0, "gXp", "Gathering XP Bonus", Metric.PERCENT);
    public static final RolledID GSPD = new RolledID(PutOn.ALL, 0, "gSpd", "Gathering Speed Bonus", Metric.PERCENT);

    public static final RolledID MAX_MANA = new RolledID(PutOn.ALL, 0, "maxMana", "Max Mana", Metric.RAW);
    public static final RolledID CRITDAM_PCT = new RolledID(PutOn.ALL, 0, "critDamPct", "Crit Damage Bonus", Metric.PERCENT);
    public static final RolledID NSD_RAW = new RolledID(PutOn.ALL, 0, "nSdRaw", "Neutral Spell Damage", Metric.RAW);
    public static final RolledID ESD_RAW = new RolledID(PutOn.ALL, 0, "eSdRaw", "Earth Spell Damage", Metric.RAW);
    public static final RolledID TSD_RAW = new RolledID(PutOn.ALL, 0, "tSdRaw", "Thunder Spell Damage", Metric.RAW);
    public static final RolledID WSD_RAW = new RolledID(PutOn.ALL, 0, "wSdRaw", "Water Spell Damage", Metric.RAW);
    public static final RolledID FSD_RAW = new RolledID(PutOn.ALL, 0, "fSdRaw", "Fire Spell Damage", Metric.RAW);
    public static final RolledID ASD_RAW = new RolledID(PutOn.ALL, 0, "aSdRaw", "Air Spell Damage", Metric.RAW);
    public static final RolledID RSD_PCT = new RolledID(PutOn.ALL, 0, "rSdPct", "Elemental Spell Damage", Metric.PERCENT);
    public static final RolledID NSD_PCT = new RolledID(PutOn.ALL, 0, "nSdPct", "Neutral Spell Damage", Metric.PERCENT);
    public static final RolledID ESD_PCT = new RolledID(PutOn.ALL, 0, "eSdPct", "Earth Spell Damage", Metric.PERCENT);
    public static final RolledID TSD_PCT = new RolledID(PutOn.ALL, 0, "tSdPct", "Thunder Spell Damage", Metric.PERCENT);
    public static final RolledID WSD_PCT = new RolledID(PutOn.ALL, 0, "wSdPct", "Water Spell Damage", Metric.PERCENT);
    public static final RolledID FSD_PCT = new RolledID(PutOn.ALL, 0, "fSdPct", "Fire Spell Damage", Metric.PERCENT);
    public static final RolledID ASD_PCT = new RolledID(PutOn.ALL, 0, "aSdPct", "Air Spell Damage", Metric.PERCENT);

    public static final RolledID RMD_RAW = new RolledID(PutOn.ALL, 0, "rMdRaw", "Elemental Main Attack Damage", Metric.RAW);
    public static final RolledID NMD_RAW = new RolledID(PutOn.ALL, 0, "nMdRaw", "Neutral Main Attack Damage", Metric.RAW);
    public static final RolledID EMD_RAW = new RolledID(PutOn.ALL, 0, "eMdRaw", "Earth Main Attack Damage", Metric.RAW);
    public static final RolledID TMD_RAW = new RolledID(PutOn.ALL, 0, "tMdRaw", "Thunder Main Attack Damage", Metric.RAW);
    public static final RolledID WMD_RAW = new RolledID(PutOn.ALL, 0, "wMdRaw", "Water Main Attack Damage", Metric.RAW);
    public static final RolledID FMD_RAW = new RolledID(PutOn.ALL, 0, "fMdRaw", "Fire Main Attack Damage", Metric.RAW);
    public static final RolledID AMD_RAW = new RolledID(PutOn.ALL, 0, "aMdRaw", "Air Main Attack Damage", Metric.RAW);

    public static final RolledID RMD_PCT = new RolledID(PutOn.ALL, 0, "rMdPct", "Elemental Main Attack Damage", Metric.PERCENT);
    public static final RolledID NMD_PCT = new RolledID(PutOn.ALL, 0, "nMdPct", "Neutral Main Attack Damage", Metric.PERCENT);
    public static final RolledID EMD_PCT = new RolledID(PutOn.ALL, 0, "eMdPct", "Earth Main Attack Damage", Metric.PERCENT);
    public static final RolledID TMD_PCT = new RolledID(PutOn.ALL, 0, "tMdPct", "Thunder Main Attack Damage", Metric.PERCENT);
    public static final RolledID WMD_PCT = new RolledID(PutOn.ALL, 0, "wMdPct", "Water Main Attack Damage", Metric.PERCENT);
    public static final RolledID FMD_PCT = new RolledID(PutOn.ALL, 0, "fMdPct", "Fire Main Attack Damage", Metric.PERCENT);
    public static final RolledID AMD_PCT = new RolledID(PutOn.ALL, 0, "aMdPct", "Air Main Attack Damage", Metric.PERCENT);

    public static final RolledID DAM_RAW = new RolledID(PutOn.ALL, 0, "damRaw", "Damage", Metric.RAW);

    public static final RolledID RDAM_RAW = new RolledID(PutOn.ALL, 0, "rDamRaw", "Elemental Damage", Metric.RAW);
    public static final RolledID NDAM_RAW = new RolledID(PutOn.ALL, 0, "nDamRaw", "Neutral Damage", Metric.RAW);
    public static final RolledID EDAM_RAW = new RolledID(PutOn.ALL, 0, "eDamRaw", "Earth Damage", Metric.RAW);
    public static final RolledID TDAM_RAW = new RolledID(PutOn.ALL, 0, "tDamRaw", "Thunder Damage", Metric.RAW);
    public static final RolledID WDAM_RAW = new RolledID(PutOn.ALL, 0, "wDamRaw", "Water Damage", Metric.RAW);
    public static final RolledID FDAM_RAW = new RolledID(PutOn.ALL, 0, "fDamRaw", "Fire Damage", Metric.RAW);
    public static final RolledID ADAM_RAW = new RolledID(PutOn.ALL, 0, "aDamRaw", "Air Damage", Metric.RAW);

    public static final RolledID DAM_PCT = new RolledID(PutOn.ALL, 0, "damPct", "Damage", Metric.PERCENT);
    public static final RolledID RDAM_PCT = new RolledID(PutOn.ALL, 0, "rDamPct", "Elemental Damage", Metric.PERCENT);
    public static final RolledID NDAM_PCT = new RolledID(PutOn.ALL, 0, "nDamPct", "Neutral Damage", Metric.PERCENT);

    public static final RolledID HEAL_PCT = new RolledID(PutOn.ALL, 0, "healPct", "Healing Efficiency", Metric.PERCENT);

    public static final RolledID KB = new RolledID(PutOn.ALL, 0, "kb", "Knockback", Metric.PERCENT);
    public static final RolledID WEAKEN_ENEMY = new RolledID(PutOn.ALL, 0, "weakenEnemy", "Weaken Enemy", Metric.PERCENT);
    public static final RolledID SLOW_ENEMY = new RolledID(PutOn.ALL, 0, "slowEnemy", "Slow Enemy", Metric.PERCENT);
    public static final RolledID RDEF_PCT = new RolledID(PutOn.ALL, 0, "rDefPct", "Elemental Defense", Metric.PERCENT);


    /**
     * Does absolutely nothing, but loads this class and the Typed ids
     */
    public static void load() {

    }
}
