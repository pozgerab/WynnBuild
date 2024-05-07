package com.gertoxq.quickbuild.atreeimport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class Atrouter {
    private final Map<Integer, Set<Integer>> atreeGraph = new HashMap<>();
    private final Set<Integer> visited = new HashSet<>();
    private final List<Integer> route = new ArrayList<>();
    private final Set<Integer> nodesToVisit;
    private final Set<Integer> nodesWithoutParents;

    public Atrouter(Set<Integer> nodesToVisit, JsonObject whole) {
        this.nodesToVisit = nodesToVisit;
        this.nodesWithoutParents = new HashSet<>(nodesToVisit);
        graphSetup(whole);
    }

    private void graphSetup(JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            int node = Integer.parseInt(key);
            if (nodesToVisit.contains(node)) {
                JsonObject nodeInfo = jsonObject.getAsJsonObject(key);
                JsonArray children = nodeInfo.getAsJsonArray("children");
                Set<Integer> adjacentNodes = new HashSet<>();

                for (int i = 0; i < children.size(); i++) {
                    int child = children.get(i).getAsInt();
                    if (nodesToVisit.contains(child)) {
                        adjacentNodes.add(child);
                        nodesWithoutParents.remove(child);
                    }
                }

                atreeGraph.put(node, adjacentNodes);
            }
        }
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
        visited.add(node);
        route.add(node);
        if (atreeGraph.containsKey(node)) {
            for (Integer child : atreeGraph.get(node)) {
                dfs(child);
            }
        }
    }
}