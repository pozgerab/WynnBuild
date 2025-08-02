package com.gertoxq.wynnbuild.base.fields;

import com.gertoxq.wynnbuild.util.Utils;

public enum AtkSpd {
    //  ORDER MATTERS
    SUPER_SLOW,
    VERY_SLOW,
    SLOW,
    NORMAL,
    FAST,
    VERY_FAST,
    SUPER_FAST;

    public String getDisplayName() {
        return Utils.capitalizeAllFirst(name().replace("_", " ").toLowerCase());
    }
}
