package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.Powder;
import com.gertoxq.wynnbuild.build.Gear;

import java.util.List;

import static com.gertoxq.wynnbuild.base.EncodingBitVector.ENC;

public class EncodeDecode {

    static byte VECTOR_FLAG = 0xC;
    static int VERSION_BITLEN = 10;

    public static EncodingBitVector encodeHeader(int wynnVersionId) {
        EncodingBitVector headerVec = new EncodingBitVector(0, 0);

        headerVec.append(VECTOR_FLAG, 6);
        headerVec.append(wynnVersionId, VERSION_BITLEN);
        return headerVec;
    }

    public static EncodingBitVector encodeEquipment(List<Gear> equipment, List<List<Powder.Element>> powders, int wynnVersionId) {
        EncodingBitVector equipmentVec = new EncodingBitVector(0, 0);

        for (int i = 0; i < equipment.size(); i++) {

            Gear gear = equipment.get(i);
            int equipmentKind = gear.crafted ? 0x02 : 0x00;
        }

        return equipmentVec;
    }
}
