package com.gertoxq.wynnbuild.atreeimport;

import com.gertoxq.wynnbuild.screens.atree.Ability;

import java.util.*;
import java.util.stream.Collectors;

public class Atrouter {
    public final Set<Integer> nodesWithoutParents;
    private final Map<Integer, Set<Integer>> atreeGraph = new HashMap<>();
    private final Map<Integer, Set<Integer>> dependencies = new HashMap<>();
    private final Set<Integer> visited = new HashSet<>();
    private final List<Integer> route = new ArrayList<>();
    private final Set<Integer> nodesToVisit;
    private final Set<Integer> allNodes;

    public Atrouter(Set<Integer> nodesToVisit, Set<Integer> allNodesToVisit) {
        this.nodesToVisit = nodesToVisit;
        this.nodesWithoutParents = new HashSet<>(nodesToVisit);
        this.allNodes = allNodesToVisit;
        graphSetup();
    }

    private void graphSetup() {

        Ability.getAbilityMap().forEach((node, ability) -> {
            if (nodesToVisit.contains(node)) {

                dependencies.put(node, ability.dependencies().stream().filter(nodesToVisit::contains).collect(Collectors.toSet()));

                atreeGraph.put(node, ability.children().stream().filter(child -> {
                    if (nodesToVisit.contains(child) && Ability.areDifferentLevel(node, child)) {
                        nodesWithoutParents.remove(child);
                        return true;
                    }
                    return false;
                }).collect(Collectors.toSet()));

                if (ability.parents().stream().noneMatch(parent -> Ability.areDifferentLevel(node, parent) && !nodesToVisit.contains(parent) && allNodes.contains(parent))) {
                    nodesWithoutParents.remove(node);
                }
            }
        });
    }

    public List<Integer> findRoute() {
        for (Integer startNode : nodesWithoutParents) {
            if (!visited.contains(startNode)) {
                dfs(startNode);
            }
        }

        for (Integer node : nodesToVisit) {
            if (!visited.contains(node)) {
                route.add(node);
            }
        }

        return route;
    }

    private void dfs(Integer node) {
        if (visited.contains(node)) return;

        if (dependencies.containsKey(node)) {
            for (Integer dependency : dependencies.get(node)) {
                if (!visited.contains(dependency)) {
                    dfs(dependency);
                }
            }
        }

        visited.add(node);
        route.add(node);

        if (atreeGraph.containsKey(node)) {
            for (Integer child : atreeGraph.get(node)) {
                dfs(child);
            }
        }
    }
}