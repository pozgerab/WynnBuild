package com.gertoxq.wynnbuild.base.ing;

import com.gertoxq.wynnbuild.base.RestrictedStatMap;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.identifications.ID;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.google.common.collect.Lists;

public class IngItemIDs extends RestrictedStatMap {

    public IngItemIDs() {
        super(Lists.asList(IDs.DURABILITY, SP.spReqIds.toArray(new ID[0])));
    }
}
