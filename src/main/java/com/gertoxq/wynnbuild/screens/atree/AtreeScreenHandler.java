package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.AtreeCoder;
import com.gertoxq.wynnbuild.BitVector;
import com.gertoxq.wynnbuild.GuiSlot;
import com.gertoxq.wynnbuild.screens.ContainerScreenHandler;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
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
    static Map<String, JsonElement> tempDupeMap;
    static List<Integer> prevIds = new ArrayList<>();
    static Map<String, Integer> nameToId = new HashMap<>();
    static Map<Integer, List<Integer>> idToParent = new HashMap<>();
    static Map<Integer, List<Integer>> idToChildren = new HashMap<>();

    static {
        for (String key : castTreeObj.keySet()) {
            JsonObject nestedObject = castTreeObj.getAsJsonObject(key);

            String displayName = nestedObject.get("display_name").getAsString();
            List<Integer> parents = nestedObject.get("parents").getAsJsonArray().asList().stream().map(JsonElement::getAsInt).toList();
            List<Integer> children = nestedObject.get("children").getAsJsonArray().asList().stream().map(JsonElement::getAsInt).toList();
            int id = nestedObject.get("id").getAsInt();
            nameToId.putIfAbsent(displayName, id);
            idToParent.putIfAbsent(id, parents);
            idToChildren.putIfAbsent(id, children);
        }
    }

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
            client.player.sendMessage(Text.literal("First read the Character Info data").styled(style -> style.withColor(Formatting.RED)), false);
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
            return;
        }
        //screen.getUnlockedNames().forEach(System.out::println);
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
            client.player.sendMessage(Text.literal("First read character info").styled(style -> style.withColor(Formatting.RED)), false);
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
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

    public List<AbilSlot> getSlots() {
        List<AbilSlot> realSlots = new ArrayList<>();
        slots.forEach(slot -> {

            String name = Utils.removeNum(Utils.removeFormat(slot.getStack().getName().getString()).replace("Unlock ", "").replace(" ability", ""));

            Integer id = nameToId.getOrDefault(name, null);
            if (id != null) {
                if (readCache.contains(id) && tempDupeMap.containsKey(id.toString())) {
                    id = tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt(); //  IN CASE OF LEVEL III
                    if (readCache.contains(id) && tempDupeMap.containsKey(id.toString())) {
                        id = tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt();
                    }
                }
                readCache.add(id);
                realSlots.add(new AbilSlot(id, name, slot));
            }
        });
        return realSlots;
    }

    private void travFindUnlocked(int id, Set<Integer> pageCache, Set<Integer> checked, Map<Integer, Slot> allSlots, List<AbilSlot> unlockedSlots) {

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
        Slot slot = allSlots.get(id);
        List<Text> lore = Utils.getLore(slot.getStack());
        Text nameText = slot.getStack().getName();
        String name = Utils.removeFormat(nameText.getString());
        if (unlockedCache.contains(id)) {
            unlockedSlots.add(new AbilSlot(id, name, slot));
            pageCache.add(id);
            unlockedCache.add(id);
            idToChildren.get(id).stream().sorted().forEach(integer -> travFindUnlocked(integer, pageCache, checked, allSlots, unlockedSlots));
            return;
        }
        if (lore == null || lore.isEmpty()) return;
        if (name.startsWith("Unlock ")) return;
        String lastStr = Utils.removeFormat(lore.getLast().getString());
        if (lastStr.contains("You do not meet the requirements")) return;
        if (lastStr.contains("Blocked by another ability")) return;
        List<Integer> parents = idToParent.get(id);
        //System.out.println(id+"'s parents="+parents);
        if (parents.stream().anyMatch(unlockedCache::contains)) {
            unlockedSlots.add(new AbilSlot(id, name, slot));
            pageCache.add(id);
            unlockedCache.add(id);
            //System.out.println("added "+ id);
            idToChildren.get(id).stream().sorted().forEach(integer -> travFindUnlocked(integer, pageCache, checked, allSlots, unlockedSlots));
        }
    }

    private void findEndNode(List<Integer> allId, List<Integer> remaining, Set<Integer> endNodes) {
        if (remaining.isEmpty()) return;
        List<Integer> children = new ArrayList<>(idToChildren.getOrDefault(remaining.getFirst(), new ArrayList<>()));
        remaining.removeFirst();
        children.forEach(kid -> {
            if (!allId.contains(kid)) {
                endNodes.add(kid);
            }
        });
        findEndNode(allId, remaining, endNodes);
    }

    public List<AbilSlot> getUnlocked() {

        Set<Integer> pageCache = new HashSet<>();
        List<Slot> filtered = slots.stream().filter(slot -> !slot.getStack().isEmpty() && nameToId.containsKey(Utils.removeNum(Utils.removeFormat(slot.getStack().getName().getString()
                .replace("Unlock ", "")
                .replace(" ability", "").trim())))).toList();
        Map<Integer, Slot> idSlotMap = new HashMap<>();
        filtered.forEach(slot -> {
            String name = Utils.removeNum(Utils.removeFormat(slot.getStack().getName().getString()
                    .replace("Unlock ", "")
                    .replace(" ability", "").trim()));
            //System.out.println(name);
            //System.out.println(nameToId.containsKey(name));
            Integer id = nameToId.getOrDefault(name, Integer.MAX_VALUE);
            if (allCache.contains(id) && tempDupeMap.containsKey(id.toString())) {
                id = tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt(); //  IN CASE OF LEVEL III
                if (allCache.contains(id) && tempDupeMap.containsKey(id.toString())) {
                    id = tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt();
                }
            }
            idSlotMap.put(id, slot);
        });
        List<Integer> sortedIds = idSlotMap.keySet().stream().sorted().toList();
        allCache.addAll(sortedIds);
        List<AbilSlot> slots = new ArrayList<>();

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
        return getUnlocked().stream().map(AbilSlot::id).collect(Collectors.toSet());
    }

    public void scrollAtree(int amount) {
        for (int i = 0; i < Math.abs(amount); i++) {
            new Task(() -> client.execute(() -> this.leftClickSlot(amount > 0 ? GuiSlot.ATREE_UP.slot : GuiSlot.ATREE_DOWN.slot)), i * 4);
        }
    }

    public record AbilSlot(int id, String name, Slot slot) {
    }
}
