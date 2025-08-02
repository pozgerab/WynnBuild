package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.GuiSlot;
import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;

import java.util.*;
import java.util.stream.Collectors;

public class AtreeScreenHandler extends ContainerScreenHandler {
    static final Set<Integer> allCache = new HashSet<>();
    static final Set<Integer> readCache = new HashSet<>();
    static Set<Integer> unlockedCache = new HashSet<>(Set.of(0));
    static List<Integer> prevIds = new ArrayList<>();

    public boolean readCurrent = false;

    public AtreeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 6);
    }

    public static void resetReader() {
        readCache.clear();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

    }

    public List<AtreeNode> getSlots() {
        return slots.stream().map(AtreeNode::new).filter(node -> node.getId().map(integer -> {
            readCache.add(integer);
            return true;
        }).orElse(false)).toList();
    }

    private void travFindUnlocked(int id, Set<Integer> pageCache, Set<Integer> checked, Map<Integer, AtreeNode> allSlots, List<AtreeNode> unlockedSlots) {

        if (checked.contains(id)) {
            return;
        }
        checked.add(id);
        if (!allSlots.containsKey(id)) {
            return;
        }
        AtreeNode node = allSlots.get(id);
        if (unlockedCache.contains(id)) {
            unlockedSlots.add(node);
            pageCache.add(id);
            Ability.getById(id).children().stream().sorted().forEach(integer -> travFindUnlocked(integer, pageCache, checked, allSlots, unlockedSlots));
            return;
        }

        // if reachable, skip
        if (!node.isUnlockedOrUnreachable()) return;
        List<Integer> parents = Ability.getById(id).parents();
        // if any parent is unlocked and not reachable it must be unlocked
        if (parents.stream().anyMatch(unlockedCache::contains)) {
            unlockedSlots.add(node);
            pageCache.add(id);
            unlockedCache.add(id);
            Ability.getById(id).children().stream().sorted().forEach(integer -> travFindUnlocked(integer, pageCache, checked, allSlots, unlockedSlots));
        }
    }

    private void findEndNode(List<Integer> allId, List<Integer> remaining, Set<Integer> endNodes) {
        if (remaining.isEmpty()) return;
        List<Integer> children = new ArrayList<>(Ability.getAbilityMap().getOrDefault(remaining.getFirst(), Ability.empty()).children());
        remaining.removeFirst();
        children.forEach(kid -> {
            if (!allId.contains(kid)) {
                endNodes.add(kid);
            }
        });
        findEndNode(allId, remaining, endNodes);
    }

    public List<AtreeNode> getUnlocked() {

        Set<Integer> pageCache = new HashSet<>();
        List<AtreeNode> filtered = getSlots();
        Map<Integer, AtreeNode> idSlotMap = new HashMap<>();
        filtered.forEach(node -> node.getId().ifPresent(integer -> idSlotMap.put(integer, node)));
        List<Integer> sortedIds = idSlotMap.keySet().stream().sorted().toList();
        allCache.addAll(sortedIds);
        List<AtreeNode> slots = new ArrayList<>();

        if (prevIds.isEmpty()) {
            travFindUnlocked(sortedIds.getFirst(), pageCache, new HashSet<>(), idSlotMap, slots);
        } else {
            Set<Integer> endNodes = new HashSet<>();
            findEndNode(prevIds, new ArrayList<>(prevIds), endNodes);
            Set<Integer> checked = new HashSet<>();
            endNodes.forEach(node -> travFindUnlocked(node, pageCache, checked, idSlotMap, slots));
        }

        if (!pageCache.isEmpty()) {
            prevIds = new ArrayList<>(pageCache);
        }
        return slots;
    }

    public Set<Integer> getUnlockedIds() {
        return getUnlocked().stream().map(atreeNode -> atreeNode.id).collect(Collectors.toSet());
    }

    public void scrollAtree(int amount) {
        for (int i = 0; i < Math.abs(amount); i++) {
            new Task(() -> WynnBuild.client.execute(() -> this.leftClickSlot(amount > 0 ? GuiSlot.ATREE_UP.slot : GuiSlot.ATREE_DOWN.slot)), i * 4);
        }
    }
}
