package com.gertoxq.wynnbuild.base.sp;

import java.util.ArrayList;
import java.util.Collection;

public class SkillpointList extends ArrayList<Integer> implements java.util.List<Integer> {

    private SkillpointList() {
        super(5);
        for (SP ignored : SP.values()) {
            add(0);
        }
    }

    private SkillpointList(Collection<Integer> skillpoints) {
        super(5);
        if (skillpoints.size() != 5) {
            throw new IllegalArgumentException("SkillpointList must contain exactly 5 skillpoints.");
        }
        this.addAll(skillpoints);
    }

    public static SkillpointList of(int sp1, int sp2, int sp3, int sp4, int sp5) {
        return new SkillpointList(java.util.List.of(sp1, sp2, sp3, sp4, sp5));
    }

    public static SkillpointList from(Collection<Integer> skillpoints) {
        return new SkillpointList(skillpoints);
    }

    public static SkillpointList empty() {
        return new SkillpointList();
    }

    @Override
    public Integer set(int index, Integer element) {
        return super.set(index, Math.min(element, 140));
    }

    public void add(SkillpointList spList) {
        for (int i = 0; i < SP.values().length; i++) {
            this.set(i, this.get(i) + spList.get(i));
        }
    }
}