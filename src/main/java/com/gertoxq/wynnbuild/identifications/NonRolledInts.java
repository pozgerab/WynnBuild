package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metrics;
import com.gertoxq.wynnbuild.util.Range;

@SuppressWarnings("unused")
public interface NonRolledInts {

    NonRolledInt SLOTS = new NonRolledInt("slots", "SLOTS", Metrics.SLOTS);
    NonRolledInt HP = new NonRolledInt("hp", "Health", Metrics.RAW_BASE);
    NonRolledInt FDEF = new NonRolledInt("fDef", "Fire Defence", Metrics.RAW_BASE);
    NonRolledInt WDEF = new NonRolledInt("wDef", "Water Defence", Metrics.RAW_BASE);
    NonRolledInt ADEF = new NonRolledInt("aDef", "Air Defence", Metrics.RAW_BASE);
    NonRolledInt TDEF = new NonRolledInt("tDef", "Thunder Defence", Metrics.RAW_BASE);
    NonRolledInt EDEF = new NonRolledInt("eDef", "Earth Defence", Metrics.RAW_BASE);
    NonRolledInt LVL = new NonRolledInt(1, "lvl", "Combat Lv. Min", Metrics.REQS);
    NonRolledInt STR_REQ = new NonRolledInt("strReq", "Strength Min", Metrics.REQS);
    NonRolledInt DEX_REQ = new NonRolledInt("dexReq", "Dexterity Min", Metrics.REQS);
    NonRolledInt INT_REQ = new NonRolledInt("intReq", "Intelligence Min", Metrics.REQS);
    NonRolledInt DEF_REQ = new NonRolledInt("defReq", "Defence Min", Metrics.REQS);
    NonRolledInt AGI_REQ = new NonRolledInt("agiReq", "Agility Min", Metrics.REQS);
    NonRolledInt STR = new NonRolledInt("str", "Strength", Metrics.RAW_BONUS);
    NonRolledInt DEX = new NonRolledInt("dex", "Dexterity", Metrics.RAW_BONUS);
    NonRolledInt INT = new NonRolledInt("int", "Intelligence", Metrics.RAW_BONUS);
    NonRolledInt AGI = new NonRolledInt("agi", "Agility", Metrics.RAW_BONUS);
    NonRolledInt DEF = new NonRolledInt("def", "Defence", Metrics.RAW_BONUS);
    NonRolledInt ID = new NonRolledInt("id");
    NonRolledInt CHARGES = new NonRolledInt("charges", "Charges", Metrics.CHARGES);
    NonRolledInt DURABILITY = new NonRolledInt("durability", "Durability", Metrics.DURABILITY);
    NonRolledInt DURATION = new NonRolledInt("duration", "Duration", Metrics.DURATION);
    NonRolledInt RAINBOW_RAW = new NonRolledInt("rainbowRaw");
}
