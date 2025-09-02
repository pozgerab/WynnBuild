package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.identifications.metric.Metrics;
import com.gertoxq.wynnbuild.util.Range;

@SuppressWarnings("unused")
public interface SpecialStringIDs {

    SpecialStringID<Tier> TIER = new SpecialStringID<>("tier", "Tier", Metrics.TIER);
    SpecialStringID<ItemType> TYPE = new SpecialStringID<>("type", " Geartype", Metrics.TYPE);

    SpecialStringID<Range> NDAM = new SpecialStringID<>("nDam", "Neutral Damage", Metrics.RANGE);
    SpecialStringID<Range> FDAM = new SpecialStringID<>("fDam", "Fire Damage", Metrics.RANGE);
    SpecialStringID<Range> WDAM = new SpecialStringID<>("wDam", "Water Damage", Metrics.RANGE);
    SpecialStringID<Range> ADAM = new SpecialStringID<>("aDam", "Air Damage", Metrics.RANGE);
    SpecialStringID<Range> TDAM = new SpecialStringID<>("tDam", "Thunder Damage", Metrics.RANGE);
    SpecialStringID<Range> EDAM = new SpecialStringID<>("eDam", "Earth Damage", Metrics.RANGE);
    SpecialStringID<AtkSpd> ATKSPD = new SpecialStringID<>("atkSpd", " Attack Speed", Metrics.ATTACK_SPEED);

    SpecialStringID<Cast> CLASS_REQ = new SpecialStringID<>("classReq", "Class Req", Metrics.CLASS_REQ);
}
