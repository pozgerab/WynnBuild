package com.gertoxq.wynnbuild.base.util;

import com.gertoxq.wynnbuild.base.bitcodemaps.BitMapEncoding;

public class EncodingBitVector extends BitVector {


    public EncodingBitVector(String data) {
        super(data);
    }

    public EncodingBitVector(long data, int length) {
        super(data, length);
    }


    public void appendFlag(BitMapEncoding.BitSized field, int flag) {
        this.append(flag, field.BITLEN());
    }
}
