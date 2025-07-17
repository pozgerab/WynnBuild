package com.gertoxq.wynnbuild.base.bitcodemaps;

import com.gertoxq.wynnbuild.base.BitCodeMapSized;

public enum EQUIPMENT_KIND implements BitCodeMapSized {
    NORMAL(0),
    CRAFTED(1),
    CUSTOM(2);

    final int val;

    EQUIPMENT_KIND(int val) {
        this.val = val;
    }

    @Override
    public int BITLEN() {
        return 2;
    }
}
