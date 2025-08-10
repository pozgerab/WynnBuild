package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.base.sp.SkillpointList;

import java.util.List;

/**
 * This class is used to browse all IDs, both rolled and non-rolled.
 */
public class IDs implements RolledIDs, NonRolledIDs {

    public static final ParserID<SkillpointList> SkillpointGetter = new ParserID<>("skillpoints",
            statMap -> SkillpointList.from(SP.spIds.stream().map(statMap::get).toList()));
    public static final ParserID<SkillpointList> RequiredSkillpointsGetter = new ParserID<>("required_skillpoints",
            statMap -> SkillpointList.from(SP.spReqIds.stream().map(statMap::get).toList()));

    /**
     * Does absolutely nothing, but loads this class and the ids
     */
    public static void load() {
        List.of(SpecialStringIDs.ADAM,
                NonRolledInts.ADEF,
                NonRolledStrings.NAME,
                RolledIDs.ADAM_PCT,
                NonRolledIDs.CUSTOM);
    }
}
