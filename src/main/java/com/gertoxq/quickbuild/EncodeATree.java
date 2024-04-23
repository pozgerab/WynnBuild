package com.gertoxq.quickbuild;

import com.gertoxq.quickbuild.client.QuickBuildClient;

import java.util.HashSet;
import java.util.Set;

public class EncodeATree {

    public static BitVector encode_atree(Set<Integer> atree_state) {
        BitVector ret_vec = new BitVector(0, 0);

        traverse(0, atree_state, new HashSet<>(), ret_vec);
        return ret_vec;
    }

    private static void traverse(int id, Set<Integer> atree_state, Set<Integer> visited, BitVector ret) {
        var head = QuickBuildClient.castTreeObj.get(String.valueOf(id)).getAsJsonObject();
        for (var child : head.get("children").getAsJsonArray()) {
            var kidId = child.getAsInt();
            if (visited.contains(kidId)) continue;
            visited.add(kidId);
            if (atree_state.contains(kidId)) {
                ret.append(1, 1);
                traverse(kidId, atree_state, visited, ret);
            } else {
                ret.append(0, 1);
            }
        }
    }
}
