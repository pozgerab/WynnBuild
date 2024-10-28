package com.gertoxq.quickbuild.custom;

import com.gertoxq.quickbuild.Cast;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;

import static com.gertoxq.quickbuild.custom.ID.*;

@SuppressWarnings("unused")
public class AllIDs {
    public static final TypedID<Boolean> FIXID = new TypedID<>(PutOn.ALL, true, "fixID");
    public static final TypedID<String> NAME = new TypedID<>(PutOn.ALL, "Custom", "name");
    public static final TypedID<String> LORE = new TypedID<>(PutOn.ALL, "", "lore");
    public static final DoubleID<Tier, String> TIER = new DoubleID<>(PutOn.ALL, "tier", "Tier", Metric.TIER);
    public static final TypedID<String> SET = new TypedID<>(PutOn.ALL, "", "set");
    public static final TypedID<Integer> SLOTS = new TypedID<>(PutOn.ALL, 0, "slots");
    public static final DoubleID<ItemType, String> TYPE = new DoubleID<>(PutOn.ALL, "type", " Geartype", Metric.TYPE);
    public static final TypedID<String> MATERIAL = new TypedID<>(PutOn.ALL, "", "material");
    public static final TypedID<String> DROP = new TypedID<>(PutOn.ALL, "", "drop");
    public static final TypedID<String> QUEST = new TypedID<>(PutOn.ALL, "", "quest");
    public static final DoubleID<DoubleID.Range, String> NDAM = new DoubleID<>(PutOn.WEAPON, "nDam", "Neutral Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> FDAM = new DoubleID<>(PutOn.WEAPON, "fDam", "Fire Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> WDAM = new DoubleID<>(PutOn.WEAPON, "wDam", "Water Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> ADAM = new DoubleID<>(PutOn.WEAPON, "aDam", "Air Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> TDAM = new DoubleID<>(PutOn.WEAPON, "tDam", "Thunder Damage", Metric.RANGE);
    public static final DoubleID<DoubleID.Range, String> EDAM = new DoubleID<>(PutOn.WEAPON, "eDam", "Earth Damage", Metric.RANGE);
    public static final DoubleID<ATKSPDS, String> ATKSPD = new DoubleID<>(PutOn.WEAPON, "atkSpd", " Attack Speed", Metric.ATTACK_SPEED);
    public static final TypedID<Integer> HP = new TypedID<>(PutOn.ARMOR, 0, "hp", "Health", Metric.RAW);
    public static final TypedID<Integer> FDEF = new TypedID<>(PutOn.ARMOR, 0, "fDef", "Fire Defence", Metric.RAW);
    public static final TypedID<Integer> WDEF = new TypedID<>(PutOn.ARMOR, 0, "wDef", "Water Defence", Metric.RAW);
    public static final TypedID<Integer> ADEF = new TypedID<>(PutOn.ARMOR, 0, "aDef", "Air Defence", Metric.RAW);
    public static final TypedID<Integer> TDEF = new TypedID<>(PutOn.ARMOR, 0, "tDef", "Thunder Defence", Metric.RAW);
    public static final TypedID<Integer> EDEF = new TypedID<>(PutOn.ARMOR, 0, "eDef", "Earth Defence", Metric.RAW);
    public static final TypedID<Integer> LVL = new TypedID<>(PutOn.ALL, 0, "lvl", "Combat Lv. Min", Metric.RAW);
    public static final DoubleID<Cast, String> CLASS_REQ = new DoubleID<>(PutOn.ALL, "classReq", "Class Req", Metric.CAST);
    public static final TypedID<Integer> STR_REQ = new TypedID<>(PutOn.ALL, 0, "strReq", "Strength Min", Metric.RAW);
    public static final TypedID<Integer> DEX_REQ = new TypedID<>(PutOn.ALL, 0, "dexReq", "Dexterity Min", Metric.RAW);
    public static final TypedID<Integer> INT_REQ = new TypedID<>(PutOn.ALL, 0, "intReq", "Intelligence Min", Metric.RAW);
    public static final TypedID<Integer> DEF_REQ = new TypedID<>(PutOn.ALL, 0, "defReq", "Defence Min", Metric.RAW);
    public static final TypedID<Integer> AGI_REQ = new TypedID<>(PutOn.ALL, 0, "agiReq", "Agility Min", Metric.RAW);
    public static final TypedID<Integer> STR = new TypedID<>(PutOn.ALL, 0, "str", "Strength", Metric.RAW);
    public static final TypedID<Integer> DEX = new TypedID<>(PutOn.ALL, 0, "dex", "Dexterity", Metric.RAW);
    public static final TypedID<Integer> INT = new TypedID<>(PutOn.ALL, 0, "int", "Intelligence", Metric.RAW);
    public static final TypedID<Integer> AGI = new TypedID<>(PutOn.ALL, 0, "agi", "Agility", Metric.RAW);
    public static final TypedID<Integer> DEF = new TypedID<>(PutOn.ALL, 0, "def", "Defence", Metric.RAW);
    public static final TypedID<Integer> ID = new TypedID<>(PutOn.ALL, 0, "id");
    public static final TypedID<IntList> SKILLPOINTS = new TypedID<>(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), "skillpoints");
    public static final TypedID<IntList> REQS = new TypedID<>(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), "reqs");
    public static final TypedID<String> NDAM_ = new TypedID<>(PutOn.WEAPON, "", "nDam_");
    public static final TypedID<String> FDAM_ = new TypedID<>(PutOn.WEAPON, "", "fDam_");
    public static final TypedID<String> WDAM_ = new TypedID<>(PutOn.WEAPON, "", "wDam_");
    public static final TypedID<String> ADAM_ = new TypedID<>(PutOn.WEAPON, "", "aDam_");
    public static final TypedID<String> TDAM_ = new TypedID<>(PutOn.WEAPON, "", "tDam_");
    public static final TypedID<String> EDAM_ = new TypedID<>(PutOn.WEAPON, "", "eDam_");
    public static final TypedID<List<String>> MAJOR_IDS = new TypedID<>(PutOn.ALL, List.of(), "majorIds");
    public static final TypedID<Integer> HPR_PCT = new TypedID<>(PutOn.ALL, 0, "hprPct", "Health Regen", Metric.PERCENT);
    public static final TypedID<Integer> MR = new TypedID<>(PutOn.ALL, 0, "mr", "Mana Regen", Metric.PERXS);
    public static final TypedID<Integer> SD_PCT = new TypedID<>(PutOn.ALL, 0, "sdPct", "Spell Damage", Metric.PERCENT);
    public static final TypedID<Integer> MD_PCT = new TypedID<>(PutOn.ALL, 0, "mdPct", "Main Attack Damage", Metric.PERCENT);
    public static final TypedID<Integer> LS = new TypedID<>(PutOn.ALL, 0, "ls", "Life Steal", Metric.PERXS);
    public static final TypedID<Integer> MS = new TypedID<>(PutOn.ALL, 0, "ms", "Mana Steal", Metric.PERXS);
    public static final TypedID<Integer> XPB = new TypedID<>(PutOn.ALL, 0, "xpb", "XP Bonus", Metric.PERCENT);
    public static final TypedID<Integer> LB = new TypedID<>(PutOn.ALL, 0, "lb", "Loot Bonus", Metric.PERCENT);
    public static final TypedID<Integer> REF = new TypedID<>(PutOn.ALL, 0, "ref", "Reflection", Metric.PERCENT);
    public static final TypedID<Integer> THORNS = new TypedID<>(PutOn.ALL, 0, "thorns", "Thorns", Metric.PERCENT);
    public static final TypedID<Integer> EXPD = new TypedID<>(PutOn.ALL, 0, "expd", "Exploding", Metric.PERCENT);
    public static final TypedID<Integer> SPD = new TypedID<>(PutOn.ALL, 0, "spd", "Walk Speed", Metric.PERCENT);
    public static final TypedID<Integer> ATK_TIER = new TypedID<>(PutOn.ALL, 0, "atkTier", "tier Attack Speed", Metric.RAW);
    public static final TypedID<Integer> POISON = new TypedID<>(PutOn.ALL, 0, "poison", "Poison", Metric.PERXS);
    public static final TypedID<Integer> HP_BONUS = new TypedID<>(PutOn.ALL, 0, "hpBonus", "Health", Metric.RAW);
    public static final TypedID<Integer> SP_REGEN = new TypedID<>(PutOn.ALL, 0, "spRegen", "Soul Point Regen", Metric.PERCENT);
    public static final TypedID<Integer> E_STEAL = new TypedID<>(PutOn.ALL, 0, "eSteal", "Stealing", Metric.PERCENT);
    public static final TypedID<Integer> HPR_RAW = new TypedID<>(PutOn.ALL, 0, "hprRaw", "Health Regen", Metric.RAW);
    public static final TypedID<Integer> SD_RAW = new TypedID<>(PutOn.ALL, 0, "sdRaw", "Spell Damage", Metric.RAW);
    public static final TypedID<Integer> MD_RAW = new TypedID<>(PutOn.ALL, 0, "mdRaw", "Main Attack Damage", Metric.RAW);
    public static final TypedID<Integer> FDAM_PCT = new TypedID<>(PutOn.ALL, 0, "fDamPct", "Fire Damage", Metric.PERCENT);
    public static final TypedID<Integer> WDAM_PCT = new TypedID<>(PutOn.ALL, 0, "wDamPct", "Water Damage", Metric.PERCENT);
    public static final TypedID<Integer> ADAM_PCT = new TypedID<>(PutOn.ALL, 0, "aDamPct", "Air Damage", Metric.PERCENT);
    public static final TypedID<Integer> TDAM_PCT = new TypedID<>(PutOn.ALL, 0, "tDamPct", "Thunder Damage", Metric.PERCENT);
    public static final TypedID<Integer> EDAM_PCT = new TypedID<>(PutOn.ALL, 0, "eDamPct", "Earth Damage", Metric.PERCENT);
    public static final TypedID<Integer> FDEF_PCT = new TypedID<>(PutOn.ALL, 0, "fDefPct", "Fire Defence", Metric.PERCENT);
    public static final TypedID<Integer> WDEF_PCT = new TypedID<>(PutOn.ALL, 0, "wDefPct", "Water Defence", Metric.PERCENT);
    public static final TypedID<Integer> ADEF_PCT = new TypedID<>(PutOn.ALL, 0, "aDefPct", "Air Defence", Metric.PERCENT);
    public static final TypedID<Integer> TDEF_PCT = new TypedID<>(PutOn.ALL, 0, "tDefPct", "Thunder Defence", Metric.PERCENT);
    public static final TypedID<Integer> EDEF_PCT = new TypedID<>(PutOn.ALL, 0, "eDefPct", "Earth Defence", Metric.PERCENT);
    public static final TypedID<Integer> SP_PCT1 = new TypedID<>(PutOn.ALL, 0, "spPct1", "&1", Metric.PERCENT);
    public static final TypedID<Integer> SP_RAW1 = new TypedID<>(PutOn.ALL, 0, "spRaw1", "&1", Metric.RAW);
    public static final TypedID<Integer> SP_PCT2 = new TypedID<>(PutOn.ALL, 0, "spPct2", "&2", Metric.PERCENT);
    public static final TypedID<Integer> SP_RAW2 = new TypedID<>(PutOn.ALL, 0, "spRaw2", "&2", Metric.RAW);
    public static final TypedID<Integer> SP_PCT3 = new TypedID<>(PutOn.ALL, 0, "spPct3", "&3", Metric.PERCENT);
    public static final TypedID<Integer> SP_RAW3 = new TypedID<>(PutOn.ALL, 0, "spRaw3", "&3", Metric.RAW);
    public static final TypedID<Integer> SP_PCT4 = new TypedID<>(PutOn.ALL, 0, "spPct4", "&4", Metric.PERCENT);
    public static final TypedID<Integer> SP_RAW4 = new TypedID<>(PutOn.ALL, 0, "spRaw4", "&4", Metric.RAW);
    public static final TypedID<Integer> RAINBOW_RAW = new TypedID<>(PutOn.ALL, 0, "rainbowRaw");
    public static final TypedID<Integer> SPRINT = new TypedID<>(PutOn.ALL, 0, "sprint", "Sprint", Metric.PERCENT);
    public static final TypedID<Integer> SPRINT_REG = new TypedID<>(PutOn.ALL, 0, "sprintReg", "Sprint Regen", Metric.PERCENT);
    public static final TypedID<Integer> JH = new TypedID<>(PutOn.ALL, 0, "jh", "Jump Height", Metric.RAW);
    public static final TypedID<Integer> LQ = new TypedID<>(PutOn.ALL, 0, "lq", "Loot Quality", Metric.PERCENT);
    public static final TypedID<Integer> GXP = new TypedID<>(PutOn.ALL, 0, "gXp", "Gathering XP Bonus", Metric.PERCENT);
    public static final TypedID<Integer> GSPD = new TypedID<>(PutOn.ALL, 0, "gSpd", "Gathering Speed Bonus", Metric.PERCENT);
    public static final TypedID<String> DURABILITY = new TypedID<>(PutOn.CONSUMABLE, "", "durability", "Durability", Metric.OTHERSTR); //  int-int
    public static final TypedID<String> DURATION = new TypedID<>(PutOn.CONSUMABLE, "", "duration", "Duration", Metric.OTHERSTR); //    int-int
    public static final TypedID<Integer> CHARGES = new TypedID<>(PutOn.CONSUMABLE, 0, "charges", "Charges", Metric.OTHERINT);

    /**
     * Does absolutely nothing, but loads this class and the Typed ids
     */
    public static void load() {

    }
}
