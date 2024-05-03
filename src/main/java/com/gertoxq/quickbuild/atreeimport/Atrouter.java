package com.gertoxq.quickbuild.atreeimport;

import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class Atrouter {
    private final Map<Integer, Set<Integer>> atreeGraph = new HashMap<>();
    private final Set<Integer> visited = new HashSet<>();
    private final List<Integer> route = new ArrayList<>();
    private final Set<Integer> nodesToVisit;

    public Atrouter(Set<Integer> nodesToVisit) {
        this.nodesToVisit = nodesToVisit;
        parseJson();
    }

    private void parseJson() {
        JsonObject jsonObject = QuickBuildClient.castTreeObj;
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
        Set<Integer> topLevelParents = new HashSet<>(nodesToVisit);
        atreeGraph.values().forEach(topLevelParents::removeAll);

        for (Integer startNode : topLevelParents) {
            if (!visited.contains(startNode)) {
                dfs(startNode);
            }
        }
        return route;
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