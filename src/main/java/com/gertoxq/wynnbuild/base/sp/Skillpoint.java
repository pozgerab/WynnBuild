package com.gertoxq.wynnbuild.base.sp;

import com.wynntils.core.components.Models;
import com.wynntils.models.elements.type.Skill;

public class Skillpoint {

    public static int getTotalSkillpoints(Skill skill) {
        return Models.SkillPoint.getTotalSkillPoints(skill) - Models.SkillPoint.getStatusEffectSkillPoints(skill);
    }

    public static int getManualPoints(Skill skill) {
        return getTotalSkillpoints(skill) - Models.SkillPoint.getGearSkillPoints(skill)
                - Models.SkillPoint.getCraftedSkillPoints(skill) - Models.SkillPoint.getTomeSkillPoints(skill) - Models.SkillPoint.getSetBonusSkillPoints(skill);
    }

}
