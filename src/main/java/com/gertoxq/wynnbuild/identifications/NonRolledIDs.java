package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.ID.PutOn;
import it.unimi.dsi.fastutil.ints.IntList;

@SuppressWarnings("unused")
public interface NonRolledIDs extends NonRolledInts, NonRolledStrings {
    NonRolledID<Boolean> FIXID = new NonRolledID<>(PutOn.ALL, false, "fixID");
    NonRolledID<Boolean> NONE = new NonRolledID<>(PutOn.ALL, false, "NONE");
    NonRolledID<Boolean> CRAFTED = new NonRolledID<>(PutOn.ALL, false, "crafted");
    NonRolledID<Boolean> CUSTOM = new NonRolledID<>(PutOn.ALL, false, "custom");
    NonRolledID<IntList> SKILLPOINTS = new NonRolledID<>(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), "skillpoints");
    NonRolledID<IntList> REQS = new NonRolledID<>(PutOn.ALL, IntList.of(0, 0, 0, 0, 0), "reqs");
}
