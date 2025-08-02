package com.gertoxq.wynnbuild.identifications;

@SuppressWarnings("unused")
public interface NonRolledStrings extends SpecialStringIDs {

    NonRolledString HASH = new NonRolledString(ID.PutOn.ALL, "hash");
    NonRolledString NAME = new NonRolledString(ID.PutOn.ALL, "", "name", "Name", Metric.OTHERSTR);
    NonRolledString LORE = new NonRolledString(ID.PutOn.ALL, "lore");
    NonRolledString SET = new NonRolledString(ID.PutOn.ALL, "set", "set");
    NonRolledString MATERIAL = new NonRolledString(ID.PutOn.ALL, "material");
    NonRolledString DROP = new NonRolledString(ID.PutOn.ALL, "never", "drop", "DROP", Metric.OTHERSTR);
    NonRolledString QUEST = new NonRolledString(ID.PutOn.ALL, "quest");
    NonRolledString DURABILITY = new NonRolledString(ID.PutOn.CONSUMABLE, "durability", "Durability"); //  int-int
    NonRolledString DURATION = new NonRolledString(ID.PutOn.CONSUMABLE, "duration", "Duration"); //    int-int
}
