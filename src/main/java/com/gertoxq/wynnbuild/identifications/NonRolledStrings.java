package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metrics;

@SuppressWarnings("unused")
public interface NonRolledStrings extends SpecialStringIDs {

    NonRolledString HASH = new NonRolledString("hash");
    NonRolledString NAME = new NonRolledString("", "name", "Name", Metrics.OTHERSTR);
    NonRolledString LORE = new NonRolledString("lore");
    NonRolledString SET = new NonRolledString("set");
    NonRolledString MATERIAL = new NonRolledString("material");
    NonRolledString DROP = new NonRolledString("never", "drop", "DROP", Metrics.OTHERSTR);
    NonRolledString QUEST = new NonRolledString("quest");
    NonRolledString MAJOR_IDS = new NonRolledString("", "majorIds", "MAJOR_IDS", Metrics.MAJOR_IDS);
}
