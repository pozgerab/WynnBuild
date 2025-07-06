package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.AtreeCoder;
import com.gertoxq.wynnbuild.BitVector;
import com.gertoxq.wynnbuild.GuiSlot;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;

public class AtreeScreenHandler extends ContainerScreenHandler {
    static final Set<Integer> allCache = new HashSet<>();
    static final Set<Integer> readCache = new HashSet<>();
    static Set<Integer> unlockedCache = new HashSet<>(Set.of(0));
    static List<Integer> prevIds = new ArrayList<>();

    public boolean readCurrent = false;

    public AtreeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(syncId, playerInventory, inventory, 6);
        if (!readAtree) {
            startAtreead();
        }
    }

    public static void resetData() {
        unlockedCache = new HashSet<>(Set.of(0));
        prevIds.clear();
        allCache.clear();
    }

    public static void resetReader() {
        readCache.clear();
    }

    public void saveATree() {
        if (castTreeObj == null) {
            assert client.player != null;
            displayErr("First read the Character Info data");
            return;
        }
        Set<Integer> unlockedIds = getUnlockedIds();
        //System.out.println("Unlocked "+ Arrays.toString(unlockedIds.toArray()));
        unlockedAbilIds.addAll(unlockedIds);
        atreeState.addAll(unlockedAbilIds);
        //System.out.println("Unlocked "+ Arrays.toString(unlockedAbilIds.toArray()));
    }

    public void startAtreead() {
        if (castTreeObj == null) {
            assert client != null;
            assert client.player != null;
            displayErr("First read character info");
            return;
        }
        final AtreeScreen atreeScreen = AtreeScreen.CURRENT_ATREE_SCREEN;
        AtomicBoolean allowClick = new AtomicBoolean(false);
        ScreenMouseEvents.allowMouseClick(atreeScreen).register((screen1, mouseX, mouseY, button) -> allowClick.get());
        ScreenKeyboardEvents.allowKeyPress(atreeScreen).register((screen, key, scancode, modifiers) -> allowClick.get());
        atreeScreen.setMessage(Text.literal("Reading...").styled(style -> style.withColor(Formatting.RED)));
        atreeScreen.setMessageVisible(true);
        final int pages = 9;
        AtreeScreenHandler.resetData();
        scrollAtree(-pages);            // 21
        new Task(this::saveATree, pages * 4 + 20);
        for (int i = 0; i < pages; i++) {
            new Task(() -> scrollAtree(1), i * ATREE_IDLE + pages * 4 + 24); //  28   38   34
            new Task(this::saveATree, i * ATREE_IDLE + ATREE_IDLE / 2 + pages * 4 + 24); //  30  25  28... 51
        }
        new Task(() -> {
            allowClick.set(true);
            BitVector encodedTree = AtreeCoder.encode_atree(atreeState);
            atreeSuffix = encodedTree.toB64();
            getConfigManager().getConfig().setAtreeEncoding(atreeSuffix);
            getConfigManager().saveConfig();
            atreeScreen.setMessage(Text.literal("Ability tree read successfully: ").append(atreeSuffix).styled(style -> style.withColor(Formatting.GREEN)));
            new Task(() -> atreeScreen.setMessageVisible(false), 200);
            readCurrent = true;
        }, pages * ATREE_IDLE + 8 + pages * 4 + 20);     // 45
        readAtree = true;
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

        //System.out.println("Checked="+checked);
        if (checked.contains(id)) {
            //System.out.println(id + " is already checked");
            return;
        }
        //System.out.println(id);
        checked.add(id);
        if (!allSlots.containsKey(id)) {
            //System.out.println(id + " is not found in slots");
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
        //System.out.println(id+"'s parents="+parents);
        // if any parent is unlocked and not reachable it must be unlocked
        if (parents.stream().anyMatch(unlockedCache::contains)) {
            unlockedSlots.add(node);
            pageCache.add(id);
            unlockedCache.add(id);
            //System.out.println("added "+ id);
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
        filtered.forEach(node -> {
            node.getId().ifPresent(integer -> idSlotMap.put(integer, node));
        });
        List<Integer> sortedIds = idSlotMap.keySet().stream().sorted().toList();
        allCache.addAll(sortedIds);
        List<AtreeNode> slots = new ArrayList<>();

        //System.out.println("sortedIds="+sortedIds);
        //System.out.println("PrevIds="+prevIds);
        if (prevIds.isEmpty()) {
            travFindUnlocked(sortedIds.getFirst(), pageCache, new HashSet<>(), idSlotMap, slots);
        } else {
            Set<Integer> endNodes = new HashSet<>();
            findEndNode(prevIds, new ArrayList<>(prevIds), endNodes);
            Set<Integer> checked = new HashSet<>();
            //System.out.println("topNode="+endNodes);
            endNodes.forEach(node -> travFindUnlocked(node, pageCache, checked, idSlotMap, slots));
        }
        //System.out.println("unlockedWhole="+unlockedCache);
        //System.out.println("unlockedPage="+pageCache);
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
            new Task(() -> client.execute(() -> this.leftClickSlot(amount > 0 ? GuiSlot.ATREE_UP.slot : GuiSlot.ATREE_DOWN.slot)), i * 4);
        }
    }
}
