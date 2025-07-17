package com.gertoxq.wynnbuild.base;

import com.google.gson.JsonObject;

public class EncodingBitVector extends BitVector {

    public static JsonObject ENC;
    final JsonObject bitCodeMap;

    public EncodingBitVector(Object data, int length) {
        this(data, length, ENC);
    }

    public EncodingBitVector(Object data, int length, JsonObject bitCodeMap) {
        super(data, length);
        this.bitCodeMap = bitCodeMap;
    }

    public void appendFlag(String field, String flag) {
        this.append(this.bitCodeMap.getAsJsonObject(field).get(flag).getAsInt(),
                this.bitCodeMap.getAsJsonObject(field).get("BITLEN").getAsInt());
    }
}
