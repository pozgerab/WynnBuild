package com.gertoxq.wynnbuild.base.sp;

import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.identifications.NonRolledInt;
import com.gertoxq.wynnbuild.identifications.TypedID;
import com.gertoxq.wynnbuild.util.WynnData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SP {
    STRENGTH(IDs.STR, IDs.STR_REQ),
    DEXTERITY(IDs.DEX, IDs.DEX_REQ),
    INTELLIGENCE(IDs.INT, IDs.INT_REQ),
    DEFENSE(IDs.DEF, IDs.DEF_REQ),
    AGILITY(IDs.AGI, IDs.AGI_REQ);

    public static final List<NonRolledInt> spIds = Arrays.stream(SP.values()).map(sp -> sp.id).toList();
    public static final List<NonRolledInt> spReqIds = Arrays.stream(SP.values()).map(sp -> sp.reqId).toList();
    final NonRolledInt id;
    final NonRolledInt reqId;

    SP(NonRolledInt id, NonRolledInt reqId) {
        this.id = id;
        this.reqId = reqId;
    }

    public static Map<Integer, SP> getStatContainerMap() {
        Map<Integer, SP> map = new HashMap<>();
        map.put(11, STRENGTH);
        map.put(12, DEXTERITY);
        map.put(13, INTELLIGENCE);
        map.put(14, DEFENSE);
        map.put(15, AGILITY);
        return map;
    }

    public static void applySkillpoints(SkillpointList skillPoints, Custom item, Map<String, Integer> activeSetCounts) {
        skillPoints.add(item.statMap.get(IDs.SkillpointGetter));

        if (!item.statMap.hasId(IDs.SET)) return;
        String setName = item.statMap.get(IDs.SET);
        int setCount = activeSetCounts.getOrDefault(setName, 0);
        Map<TypedID<Integer>, Integer> oldBonus = new HashMap<>();

        if (setCount > 0) {
            oldBonus = WynnData.getItemSet(setName).getBonuses(setCount - 1);
            activeSetCounts.put(setName, setCount + 1);
        } else {
            activeSetCounts.put(setName, 1);
        }
        Map<TypedID<Integer>, Integer> newBonus = WynnData.getItemSet(setName).getBonuses(setCount);

        for (int i = 0; i < SP.values().length; i++) {
            SP sp = SP.values()[i];
            int delta = newBonus.getOrDefault(sp.id, 0) - oldBonus.getOrDefault(sp.id, 0);
            skillPoints.set(i, skillPoints.get(i) + delta);
        }
    }

    public static SkillpointList calculateFinalSp(List<Custom> equipment) {

        SkillpointList skillPoints = SkillpointList.empty();
        Map<String, Integer> activeSetCounts = new HashMap<>();

        for (Custom item : equipment) {
            SkillpointList reqs = item.statMap.get(IDs.RequiredSkillpointsGetter);
            String set = item.statMap.get(IDs.SET);

            for (int i = 0; i < 5; i++) {
                if (skillPoints.get(i) < reqs.get(i) && reqs.get(i) > 0) {
                    skillPoints.set(i, reqs.get(i));
                }
            }

            applySkillpoints(skillPoints, item, activeSetCounts);
        }

        return skillPoints;

    }
}
