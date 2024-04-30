package com.gertoxq.quickbuild;

import com.gertoxq.quickbuild.client.QuickBuildClient;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gertoxq.quickbuild.client.QuickBuildClient.client;

public class EncodeATree {

    public static BitVector encode_atree(Set<Integer> atree_state) {
        BitVector ret_vec = new BitVector(0, 0);

        traverse(0, atree_state, new HashSet<>(), ret_vec);
        return ret_vec;
    }

    private static void traverse(int id, Set<Integer> atree_state, Set<Integer> visited, BitVector ret) {
        if (QuickBuildClient.castTreeObj == null) {
            client.player.sendMessage(Text.literal("CastTree is null"));
        }
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

    public static Set<Integer> decode_atree(String encoded) {
        List<Integer> bits = BitVector.fromB64(encoded);
        int i = 0;
        Set<Integer> ret = new HashSet<>();
        ret.add(0);
        traverse(0, new HashSet<>(), ret, bits, i);
        return ret;
    }
    private static void traverse(int id, Set<Integer> visited, Set<Integer> ret, List<Integer> bits, int i) {
        if (QuickBuildClient.castTreeObj == null) {
            client.player.sendMessage(Text.literal("CastTree is null"));
        }
        var head = QuickBuildClient.castTreeObj.get(String.valueOf(id)).getAsJsonObject();
        for (var child : head.get("children").getAsJsonArray()) {
            var kidId = child.getAsInt();
            if (visited.contains(kidId)) continue;
            visited.add(kidId);
            if (bits.get(i) == 1) {
                i += 1;
                ret.add(kidId);
                traverse(kidId,visited, ret, bits, i);
            }
        }
    }
}
