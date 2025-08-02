package com.gertoxq.wynnbuild.identifications;

import static com.gertoxq.wynnbuild.identifications.ID.PutOn;

@SuppressWarnings("unused")
public interface NonRolledInts {

    NonRolledInt SLOTS = new NonRolledInt(PutOn.ALL, "slots");
    NonRolledInt HP = new NonRolledInt(PutOn.ARMOR, "hp", "Health", Metric.RAW);
    NonRolledInt FDEF = new NonRolledInt(PutOn.ARMOR, "fDef", "Fire Defence", Metric.RAW);
    NonRolledInt WDEF = new NonRolledInt(PutOn.ARMOR, "wDef", "Water Defence", Metric.RAW);
    NonRolledInt ADEF = new NonRolledInt(PutOn.ARMOR, "aDef", "Air Defence", Metric.RAW);
    NonRolledInt TDEF = new NonRolledInt(PutOn.ARMOR, "tDef", "Thunder Defence", Metric.RAW);
    NonRolledInt EDEF = new NonRolledInt(PutOn.ARMOR, "eDef", "Earth Defence", Metric.RAW);
    NonRolledInt LVL = new NonRolledInt(PutOn.ALL, 1, "lvl", "Combat Lv. Min", Metric.RAW);
    NonRolledInt STR_REQ = new NonRolledInt(PutOn.ALL, "strReq", "Strength Min", Metric.RAW);
    NonRolledInt DEX_REQ = new NonRolledInt(PutOn.ALL, "dexReq", "Dexterity Min", Metric.RAW);
    NonRolledInt INT_REQ = new NonRolledInt(PutOn.ALL, "intReq", "Intelligence Min", Metric.RAW);
    NonRolledInt DEF_REQ = new NonRolledInt(PutOn.ALL, "defReq", "Defence Min", Metric.RAW);
    NonRolledInt AGI_REQ = new NonRolledInt(PutOn.ALL, "agiReq", "Agility Min", Metric.RAW);
    NonRolledInt STR = new NonRolledInt(PutOn.ALL, "str", "Strength", Metric.RAW);
    NonRolledInt DEX = new NonRolledInt(PutOn.ALL, 0, "dex", "Dexterity", Metric.RAW);
    NonRolledInt INT = new NonRolledInt(PutOn.ALL, "int", "Intelligence", Metric.RAW);
    NonRolledInt AGI = new NonRolledInt(PutOn.ALL, "agi", "Agility", Metric.RAW);
    NonRolledInt DEF = new NonRolledInt(PutOn.ALL, "def", "Defence", Metric.RAW);
    NonRolledInt ID = new NonRolledInt(PutOn.ALL, "id");
    NonRolledInt CHARGES = new NonRolledInt(PutOn.CONSUMABLE, "charges", "Charges", Metric.RAW);
    NonRolledInt RAINBOW_RAW = new NonRolledInt(PutOn.ALL, "rainbowRaw");
}
