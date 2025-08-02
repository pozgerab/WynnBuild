package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.base.bitcodemaps.BitMapEncoding;

public class CustomEnc implements BitMapEncoding {
    public int CUSTOM_VERSION_BITLEN() {
        return 7;
    }

    public int CUSTOM_ENCODING_VERSION() {
        return 2;
    }

    public CustomFixedIdsFlag CUSTOM_FIXED_IDS_FLAG() {
        return new CustomFixedIdsFlag();
    }

    public int ID_IDX_BITLEN() {
        return 10;
    }

    public int ID_LENGTH_BITLEN() {
        return 5;
    }

    public int ITEM_TYPE_BITLEN() {
        return 4;
    }

    public int ITEM_TIER_BITLEN() {
        return 4;
    }

    public int ITEM_ATK_SPD_BITLEN() {
        return 4;
    }

    public int ITEM_CLASS_REQ_BITLEN() {
        return 4;
    }

    public int TEXT_CHAR_LENGTH_BITLEN() {
        return 16;
    }

    public static class CustomFixedIdsFlag implements BitSized {
        public final int FIXED = 0;
        public final int RANGED = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }
}
