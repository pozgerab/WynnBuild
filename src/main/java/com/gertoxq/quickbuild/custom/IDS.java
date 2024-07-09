package com.gertoxq.quickbuild.custom;

import com.gertoxq.quickbuild.Cast;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public enum IDS {
    FIXID(PutOn.ALL, true, Boolean.class, "fixID"),
    NAME(PutOn.ALL, "Custom", String.class, "name"),
    LORE(PutOn.ALL, "", String.class, "lore"),
    TIER(PutOn.ALL, "Normal", String.class, "tier"),
    SET(PutOn.ALL, "", String.class, "set"),
    SLOTS(PutOn.ALL, 0, Integer.class, "slots"),
    TYPE(PutOn.ALL, "Helmet", String.class, "type"),
    MATERIAL(PutOn.ALL, "", String.class, "material"),
    DROP(PutOn.ALL, "", String.class, "drop"),
    QUEST(PutOn.ALL, "", String.class, "quest"),
    NDAM(PutOn.WEAPON, "0-0", String.class, "nDam", "Neutral Damage", Metric.INT_INT),
    FDAM(PutOn.WEAPON, "0-0", String.class, "fDam", "Fire Damage", Metric.INT_INT),
    WDAM(PutOn.WEAPON, "0-0", String.class, "wDam", "Water Damage", Metric.INT_INT),
    ADAM(PutOn.WEAPON, "0-0", String.class, "aDam", "Air Damage", Metric.INT_INT),
    TDAM(PutOn.WEAPON, "0-0", String.class, "tDam", "Thunder Damage", Metric.INT_INT),
    EDAM(PutOn.WEAPON, "0-0", String.class, "eDam", "Earth Damage", Metric.INT_INT),
    ATKSPD(PutOn.WEAPON, "", String.class, "atkSpd"),
    HP(PutOn.ARMOR, 0, Integer.class, "hp", "Health", Metric.RAW),
    FDEF(PutOn.ARMOR, 0, Integer.class, "fDef", "Fire Defence", Metric.RAW),
    WDEF(PutOn.ARMOR, 0, Integer.class, "wDef", "Water Defence", Metric.RAW),
    ADEF(PutOn.ARMOR, 0, Integer.class, "aDef", "Air Defence", Metric.RAW),
    TDEF(PutOn.ARMOR, 0, Integer.class, "tDef", "Thunder Defence", Metric.RAW),
    EDEF(PutOn.ARMOR, 0, Integer.class, "eDef", "Earth Defence", Metric.RAW),
    LVL(PutOn.ALL, 0, Integer.class, "lvl", "Combat Lv. Min", Metric.RAW),
    CLASS_REQ(PutOn.ALL, "", String.class, "classReq", "Class Req", Metric.REQ),
    STR_REQ(PutOn.ALL, 0, Integer.class, "strReq", "Strength Min", Metric.RAW),
    DEX_REQ(PutOn.ALL, 0, Integer.class, "dexReq", "Dexterity Min", Metric.RAW),
    INT_REQ(PutOn.ALL, 0, Integer.class, "intReq", "Intelligence Min", Metric.RAW),
    DEF_REQ(PutOn.ALL, 0, Integer.class, "defReq", "Defence Min", Metric.RAW),
    AGI_REQ(PutOn.ALL, 0, Integer.class, "agiReq", "Agility Min", Metric.RAW),
    STR(PutOn.ALL, 0, Integer.class, "str", "Strength", Metric.RAW),
    DEX(PutOn.ALL, 0, Integer.class, "dex", "Dexterity", Metric.RAW),
    INT(PutOn.ALL, 0, Integer.class, "int", "Intelligence", Metric.RAW),
    AGI(PutOn.ALL, 0, Integer.class, "agi", "Agility", Metric.RAW),
    DEF(PutOn.ALL, 0, Integer.class, "def", "Defence", Metric.RAW),
    ID(PutOn.ALL, 0, Integer.class, "id"),
    SKILLPOINTS(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), IntList.class, "skillpoints"),
    REQS(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), IntList.class, "reqs"),
    NDAM_(PutOn.WEAPON, "", String.class, "nDam_"),
    FDAM_(PutOn.WEAPON, "", String.class, "fDam_"),
    WDAM_(PutOn.WEAPON, "", String.class, "wDam_"),
    ADAM_(PutOn.WEAPON, "", String.class, "aDam_"),
    TDAM_(PutOn.WEAPON, "", String.class, "tDam_"),
    EDAM_(PutOn.WEAPON, "", String.class, "eDam_"),
    MAJOR_IDS(PutOn.ALL, "", String.class, "majorIds"),
    HPR_PCT(PutOn.ALL, 0, Integer.class, "hprPct", "Health Regen", Metric.PERCENT),
    MR(PutOn.ALL, 0, Integer.class, "mr", "Mana Regen", Metric.PERXS),
    SD_PCT(PutOn.ALL, 0, Integer.class, "sdPct", "Spell Damage", Metric.PERCENT),
    MD_PCT(PutOn.ALL, 0, Integer.class, "mdPct", "Main Attack Damage", Metric.PERCENT),
    LS(PutOn.ALL, 0, Integer.class, "ls", "Life Steal", Metric.PERXS),
    MS(PutOn.ALL, 0, Integer.class, "ms", "Mana Steal", Metric.PERXS),
    XPB(PutOn.ALL, 0, Integer.class, "xpb", "XP Bonus", Metric.PERCENT),
    LB(PutOn.ALL, 0, Integer.class, "lb", "Loot Bonus", Metric.PERCENT),
    REF(PutOn.ALL, 0, Integer.class, "ref", "Reflection", Metric.PERCENT),
    THORNS(PutOn.ALL, 0, Integer.class, "thorns", "Thorns", Metric.PERCENT),
    EXPD(PutOn.ALL, 0, Integer.class, "expd", "Exploding", Metric.PERCENT),
    SPD(PutOn.ALL, 0, Integer.class, "spd", "Walk Speed", Metric.PERCENT),
    ATK_TIER(PutOn.ALL, 0, Integer.class, "atkTier"),
    POISON(PutOn.ALL, 0, Integer.class, "poison", "Poison", Metric.PERXS),
    HP_BONUS(PutOn.ALL, 0, Integer.class, "hpBonus", "Health", Metric.RAW),
    SP_REGEN(PutOn.ALL, 0, Integer.class, "spRegen", "Soul Point Regen", Metric.PERCENT),
    E_STEAL(PutOn.ALL, 0, Integer.class, "eSteal"),
    HPR_RAW(PutOn.ALL, 0, Integer.class, "hprRaw", "Health Regen", Metric.RAW),
    SD_RAW(PutOn.ALL, 0, Integer.class, "sdRaw", "Spell Damage", Metric.RAW),
    MD_RAW(PutOn.ALL, 0, Integer.class, "mdRaw", "Main Attack Damage", Metric.RAW),
    FDAM_PCT(PutOn.ALL, 0, Integer.class, "fDamPct", "Fire Damage", Metric.PERCENT),
    WDAM_PCT(PutOn.ALL, 0, Integer.class, "wDamPct", "Water Damage", Metric.PERCENT),
    ADAM_PCT(PutOn.ALL, 0, Integer.class, "aDamPct", "Air Damage", Metric.PERCENT),
    TDAM_PCT(PutOn.ALL, 0, Integer.class, "tDamPct", "Thunder Damage", Metric.PERCENT),
    EDAM_PCT(PutOn.ALL, 0, Integer.class, "eDamPct", "Earth Damage", Metric.PERCENT),
    FDEF_PCT(PutOn.ALL, 0, Integer.class, "fDefPct", "Fire Defence", Metric.PERCENT),
    WDEF_PCT(PutOn.ALL, 0, Integer.class, "wDefPct", "Water Defence", Metric.PERCENT),
    ADEF_PCT(PutOn.ALL, 0, Integer.class, "aDefPct", "Air Defence", Metric.PERCENT),
    TDEF_PCT(PutOn.ALL, 0, Integer.class, "tDefPct", "Thunder Defence", Metric.PERCENT),
    EDEF_PCT(PutOn.ALL, 0, Integer.class, "eDefPct", "Earth Defence", Metric.PERCENT),
    SP_PCT1(PutOn.ALL, 0, Integer.class, "spPct1", "&1", Metric.PERCENT),
    SP_RAW1(PutOn.ALL, 0, Integer.class, "spRaw1", "&1", Metric.RAW),
    SP_PCT2(PutOn.ALL, 0, Integer.class, "spPct2", "&2", Metric.PERCENT),
    SP_RAW2(PutOn.ALL, 0, Integer.class, "spRaw2", "&2", Metric.RAW),
    SP_PCT3(PutOn.ALL, 0, Integer.class, "spPct3", "&3", Metric.PERCENT),
    SP_RAW3(PutOn.ALL, 0, Integer.class, "spRaw3", "&3", Metric.RAW),
    SP_PCT4(PutOn.ALL, 0, Integer.class, "spPct4", "&4", Metric.PERCENT),
    SP_RAW4(PutOn.ALL, 0, Integer.class, "spRaw4", "&4", Metric.RAW),
    RAINBOW_RAW(PutOn.ALL, 0, Integer.class, "rainbowRaw"),
    SPRINT(PutOn.ALL, 0, Integer.class, "sprint", "Sprint", Metric.PERCENT),
    SPRINT_REG(PutOn.ALL, 0, Integer.class, "sprintReg", "Sprint Regen", Metric.PERCENT),
    JH(PutOn.ALL, 0, Integer.class, "jh", "Jump Height", Metric.RAW),
    LQ(PutOn.ALL, 0, Integer.class, "lq", "Loot Quality", Metric.PERCENT),
    GXP(PutOn.ALL, 0, Integer.class, "gXp", "Gathering XP Bonus", Metric.PERCENT),
    GSPD(PutOn.ALL, 0, Integer.class, "gSpd", "Gathering Speed Bonus", Metric.PERCENT),
    DURABILITY(PutOn.CONSUMABLE, "", String.class, "durability"), //  int-int
    DURATION(PutOn.CONSUMABLE, "", String.class, "duration"), //    int-int
    CHARGES(PutOn.CONSUMABLE, 0, Integer.class, "charges");

    public final String name;
    public final String displayName;
    final PutOn on;
    final Object defaultValue;
    final Class<?> type;
    final Metric metric;

    <T> IDS(PutOn on, T defaultValue, Class<T> type, String name) {
        this(on, defaultValue, type, name, "", null);
    }

    <T> IDS(PutOn on, T defaultValue, Class<T> type, String name, String displayName, Metric metric) {
        this.on = on;
        this.defaultValue = defaultValue;
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.metric = metric;
    }

    public static List<IDS> getByMetric(Metric metric) {
        return Stream.of(IDS.values()).filter(ids -> ids.metric == metric).toList();
    }

    public static IDS getByName(String name) {
        return Stream.of(IDS.values()).filter(ids -> Objects.equals(ids.name, name)).findAny().orElse(null);
    }

    public boolean isReq() {
        return switch (this) {
            case LVL, CLASS_REQ, AGI_REQ, DEF_REQ, DEX_REQ, INT_REQ, STR_REQ -> true;
            default -> false;
        };
    }

    public enum Metric {
        PERCENT,
        RAW,
        INT_INT,
        PERXS,
        REQ
    }

    public enum PutOn {
        ALL,
        WEAPON,
        ARMOR,
        CONSUMABLE
    }

    public enum ATKSPDS {
        SUPER_SLOW,
        VERY_SLOW,
        SLOW,
        NORMAL,
        FAST,
        VERY_FAST,
        SUPER_FAST;

        public static ATKSPDS find(String string) {
            return Arrays.stream(ATKSPDS.values()).filter(atkspds -> atkspds.name().equalsIgnoreCase(string)).findAny().orElse(null);
        }
    }

    public enum Tier {
        Normal("§f", Formatting.WHITE),
        Unique("§e", Formatting.YELLOW),
        Rare("§d", Formatting.LIGHT_PURPLE),
        Legendary("§b", Formatting.AQUA),
        Fabled("§c", Formatting.RED),
        Mythic("§5", Formatting.DARK_PURPLE),
        Set("§a", Formatting.GREEN),
        Crafted("§3", Formatting.DARK_AQUA);
        public final String color;
        public final Formatting format;

        Tier(String color, Formatting format) {
            this.color = color;
            this.format = format;
        }
    }

    public enum ItemType {
        Helmet,
        Chestplate,
        Leggings,
        Boots,
        Ring,
        Bracelet,
        Necklace,
        Wand,
        Spear,
        Bow,
        Dagger,
        Relik,
        Potion,
        Scroll,
        Food;

        public Cast cast;

        public static ItemType find(String name) {
            return Arrays.stream(ItemType.values()).filter(type -> type.name().equalsIgnoreCase(name)).findAny().orElse(null);
        }

        public Cast getCast() {
            return Cast.findByWeapon(this);
        }

        public boolean isWeapon() {
            return getCast() != null;
        }

    }
}

