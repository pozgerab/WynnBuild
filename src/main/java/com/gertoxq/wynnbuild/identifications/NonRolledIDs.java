package com.gertoxq.wynnbuild.identifications;

import it.unimi.dsi.fastutil.ints.IntList;

@SuppressWarnings("unused")
public interface NonRolledIDs extends NonRolledInts, NonRolledStrings {
    NonRolledID<Boolean> FIXID = new NonRolledID<>(false, "fixID");
    NonRolledID<Boolean> NONE = new NonRolledID<>(false, "NONE");
    NonRolledID<Boolean> CRAFTED = new NonRolledID<>(false, "crafted");
    NonRolledID<Boolean> CUSTOM = new NonRolledID<>(false, "custom");
    NonRolledID<IntList> SKILLPOINTS = new NonRolledID<>(IntList.of(0, 0, 0, 0, 0), "skillpoints");
    NonRolledID<IntList> REQS = new NonRolledID<>(IntList.of(0, 0, 0, 0, 0), "reqs");
}
