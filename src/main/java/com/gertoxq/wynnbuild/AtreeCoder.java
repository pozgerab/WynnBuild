package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.client.WynnBuildClient;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.client;

public class AtreeCoder {

    public static BitVector encode_atree(Set<Integer> atree_state) {
        BitVector ret_vec = new BitVector(0, 0);

        traverse(0, atree_state, new HashSet<>(), ret_vec);
        return ret_vec;
    }

    private static void traverse(int id, Set<Integer> atree_state, Set<Integer> visited, BitVector ret) {
        if (WynnBuildClient.castTreeObj == null) {
            client.player.sendMessage(Text.literal("CastTree is null"));
        }
        var head = WynnBuildClient.castTreeObj.get(String.valueOf(id)).getAsJsonObject();
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
        BitVector bits = new BitVector(encoded, 0);
        System.out.println(Arrays.toString(bits.bits.getArray()));
        AtomicInteger i = new AtomicInteger();
        Set<Integer> ret = new HashSet<>();
        ret.add(0);
        traverse(0, new HashSet<>(), ret, bits, i);
        return ret;
    }

    private static void traverse(int id, Set<Integer> visited, Set<Integer> ret, BitVector bits, AtomicInteger i) {
        if (WynnBuildClient.castTreeObj == null) {
            client.player.sendMessage(Text.literal("CastTree is null"));
        }
        var head = WynnBuildClient.castTreeObj.get(String.valueOf(id)).getAsJsonObject();
        for (var child : head.get("children").getAsJsonArray()) {
            var kidId = child.getAsInt();
            if (visited.contains(kidId)) continue;
            visited.add(kidId);
            if (bits.readBit(i.getAndAdd(1)) != 0) {
                ret.add(kidId);
                traverse(kidId, visited, ret, bits, i);
            }
        }
    }
}
