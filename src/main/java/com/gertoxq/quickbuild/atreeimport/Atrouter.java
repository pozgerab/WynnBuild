package com.gertoxq.quickbuild.atreeimport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class Atrouter {
    private final Map<Integer, Set<Integer>> atreeGraph = new HashMap<>();
    private final Set<Integer> visited = new HashSet<>();
    private final List<Integer> route = new ArrayList<>();
    private final Set<Integer> nodesToVisit;

    public Atrouter(Set<Integer> nodesToVisit, JsonObject whole) {
        this.nodesToVisit = nodesToVisit;
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
                    }
                }

                atreeGraph.put(node, adjacentNodes);
            }
        }
    }

    public List<Integer> findRoute() {
        Integer startNode = startNode();
        if (startNode != null) {
            dfs(startNode);
        }
        return route;
    }

    private Integer startNode() {
        return nodesToVisit.stream().min(Integer::compare).orElse(null);
    }

    private void dfs(Integer node) {
        visited.add(node);
        route.add(node);
        if (atreeGraph.containsKey(node)) {
            for (Integer child : atreeGraph.get(node)) {
                if (!visited.contains(child)) {
                    dfs(child);
                }
            }
        }
    }
}