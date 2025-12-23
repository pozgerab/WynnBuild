package com.gertoxq.wynnbuild.build;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.wynntils.models.character.type.ClassType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AtreeCoder {

    private final Map<Integer, Ability> classTreeMap;

    private AtreeCoder(Map<Integer, Ability> classTreeMap) {
        this.classTreeMap = classTreeMap;
    }

    public static AtreeCoder getAtreeCoder(ClassType cast) {
        return new AtreeCoder(Ability.FULL_ABILITY_MAP.get(cast.getName()));
    }

    public BitVector encode_atree(Set<Integer> atree_state) {
        BitVector ret_vec = new BitVector(0, 0);

        traverse(0, atree_state, new HashSet<>(), ret_vec);
        return ret_vec;
    }

    private void traverse(int id, Set<Integer> atree_state, Set<Integer> visited, BitVector ret) {
        if (classTreeMap == null) {
            WynnBuild.error("CastTree is null");
            return;
        }
        var head = classTreeMap.get(id);
        for (var childId : head.children()) {
            if (visited.contains(childId)) continue;
            visited.add(childId);
            if (atree_state.contains(childId)) {
                ret.append(1, 1);
                traverse(childId, atree_state, visited, ret);
            } else {
                ret.append(0, 1);
            }
        }
    }

    public Set<Integer> decode_atree(String encoded) {
        BitVector bits = new BitVector(encoded);
        AtomicInteger i = new AtomicInteger();
        Set<Integer> ret = new HashSet<>();
        ret.add(0);
        traverse(0, new HashSet<>(), ret, bits, i);
        return ret;
    }

    private void traverse(int id, Set<Integer> visited, Set<Integer> ret, BitVector bits, AtomicInteger i) {
        if (classTreeMap == null) {
            WynnBuild.error("CastTree is null");
            return;
        }
        var head = classTreeMap.get(id);
        for (var childId : head.children()) {
            if (visited.contains(childId)) continue;
            visited.add(childId);
            if (bits.readBit(i.getAndAdd(1)) != 0) {
                ret.add(childId);
                traverse(childId, visited, ret, bits, i);
            }
        }
    }
}
