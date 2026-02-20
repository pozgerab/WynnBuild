package com.gertoxq.wynnbuild.build;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.models.character.type.ClassType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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

    /**
     * builds a tree that is possible in-game regardless of the extra ids in atree_state
     */
    public BitVector encode_atree_reqs(Set<Integer> atree_state) {
        return encode_atree(validate(atree_state));
    }

    public Set<Integer> validate(Set<Integer> atree_state) {
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        if (!atree_state.contains(0)) return new HashSet<>();
        queue.add(0);

        Map<String, Integer> archetypePoints = new HashMap<>();
        Map<String, PriorityQueue<AbilityAndReq>> pendingAbilities = new HashMap<>();

        Map<Integer, Set<Integer>> dependencyMap = new HashMap<>(); // key = dependency, value = abilities that depend on key
        // blocked abilities are not important bc this represents how the in game gui works, and you can't activate them anyway

        Set<Integer> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            int id = queue.poll();
            WynnBuild.debug("id {}", id);
            if (!atree_state.contains(id)) continue;
            if (visited.contains(id)) continue;
            Ability ability = Ability.getById(id);
            WynnBuild.debug("ability found");

            if (!ability.dependencies().isEmpty()) {

                Set<Integer> notMetDependencies = Utils.difference(ability.dependencies(), visited);

                notMetDependencies.forEach(dependency -> {

                    dependencyMap.merge(dependency, new HashSet<>(Set.of(id)), (oldSet, newSet) -> {
                        oldSet.addAll(newSet);
                        return oldSet;
                    });

                    WynnBuild.debug("missing dependency {}", id);
                });
            }

            if (ability.archetype() != null) {

                String archetype = ability.archetype();
                if (ability.archetypeReq() <= archetypePoints.getOrDefault(archetype, 0)) {
                    WynnBuild.debug("archetype req met, adding children, adding archetype point");

                    queueChildrenCheckPendingDependants(ability, dependencyMap, visited, queue);

                    int points = archetypePoints.merge(archetype, 1, Integer::sum);

                    PriorityQueue<AbilityAndReq> pending = pendingAbilities.getOrDefault(archetype, new PriorityQueue<>());
                    if (pending.isEmpty()) continue;

                    WynnBuild.debug("searching for pending archetype");

                    while (!pending.isEmpty()) {
                        AbilityAndReq pendingAbility = pending.peek();
                        int pendingId = pendingAbility.id();
                        int req = pendingAbility.req();

                        WynnBuild.debug("ability {} in line", pendingId);

                        if (req > points) break;
                        WynnBuild.debug("met req, adding to queue");

                        queue.addFirst(pendingId);
                        pending.poll();
                    }

                    continue;
                }

                WynnBuild.debug("archetype req not met, adding to pending");

                pendingAbilities.merge(archetype,
                        new PriorityQueue<>(List.of(new AbilityAndReq(id, ability.archetypeReq()))),
                        (oldQueue, newQueue) -> {
                    oldQueue.addAll(newQueue);
                    return oldQueue;
                });


            } else {
                WynnBuild.debug("no archetype req, adding children");
                queueChildrenCheckPendingDependants(ability, dependencyMap, visited, queue);
            }
        }

        return visited;
    }

    private void queueChildrenCheckPendingDependants(
            Ability ability, Map<Integer, Set<Integer>> dependencyMap,
            Set<Integer> visited, ArrayDeque<Integer> queue) {

        queue.addAll(ability.children());
        visited.add(ability.id());
        if (dependencyMap.containsKey(ability.id())) {
            WynnBuild.debug("found dependant for this");
            dependencyMap.get(ability.id()).forEach(dependant -> {
                Ability dependantAbility = Ability.getById(dependant);
                if (visited.containsAll(dependantAbility.dependencies())) {

                    WynnBuild.debug("all dependencies met for {}, adding to queue", dependant);
                    queue.addFirst(dependant);
                }
            });
        }

    }

    record AbilityAndReq(int id, int req) implements Comparable<AbilityAndReq> {

        @Override
        public int compareTo(@NotNull AbilityAndReq other) {
            return Integer.compare(req, other.req);
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
