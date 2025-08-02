package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.util.Range;
import com.gertoxq.wynnbuild.util.StringList;

@SuppressWarnings("unused")
public interface SpecialStringIDs {

    SpecialStringID<Tier> TIER = new SpecialStringID<>(ID.PutOn.ALL, "tier", "Tier", Metric.TIER);
    SpecialStringID<ItemType> TYPE = new SpecialStringID<>(ID.PutOn.ALL, "type", " Geartype", Metric.TYPE);

    SpecialStringID<Range> NDAM = new SpecialStringID<>(ID.PutOn.WEAPON, "nDam", "Neutral Damage", Metric.RANGE);
    SpecialStringID<Range> FDAM = new SpecialStringID<>(ID.PutOn.WEAPON, "fDam", "Fire Damage", Metric.RANGE);
    SpecialStringID<Range> WDAM = new SpecialStringID<>(ID.PutOn.WEAPON, "wDam", "Water Damage", Metric.RANGE);
    SpecialStringID<Range> ADAM = new SpecialStringID<>(ID.PutOn.WEAPON, "aDam", "Air Damage", Metric.RANGE);
    SpecialStringID<Range> TDAM = new SpecialStringID<>(ID.PutOn.WEAPON, "tDam", "Thunder Damage", Metric.RANGE);
    SpecialStringID<Range> EDAM = new SpecialStringID<>(ID.PutOn.WEAPON, "eDam", "Earth Damage", Metric.RANGE);
    SpecialStringID<AtkSpd> ATKSPD = new SpecialStringID<>(ID.PutOn.WEAPON, "atkSpd", " Attack Speed", Metric.ATTACK_SPEED);

    SpecialStringID<Cast> CLASS_REQ = new SpecialStringID<>(ID.PutOn.ALL, "classReq", "Class Req", Metric.CAST);

    SpecialStringID<StringList> MAJOR_IDS = new SpecialStringID<>(ID.PutOn.ALL, "majorIds", "MAJOR_IDS", Metric.MAJOR_IDS);
}
