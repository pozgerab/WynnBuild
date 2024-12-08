package com.gertoxq.quickbuild.screens;

import com.gertoxq.quickbuild.atreeimport.ImportAtree;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.gertoxq.quickbuild.client.QuickBuildClient.*;

public class AtreeScreen extends BScreen {
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

    public AtreeScreen(GenericContainerScreen screen) {
        super(screen);
        renderSaveButtons();
        tempDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
    }

    public static void resetData() {
        unlockedCache = new HashSet<>(Set.of(0));
        prevIds.clear();
        allCache.clear();
    }

    public static void resetReader() {
        readCache.clear();
    }

    public List<AbilSlot> getSlots() {
        List<AbilSlot> slots = new ArrayList<>();
        handler.slots.forEach(slot -> {

            String name = removeNum(removeFormat(slot.getStack().getName().getString()).replace("Unlock ", "").replace(" ability", ""));

            Integer id = nameToId.getOrDefault(name, null);
            if (id != null) {
                if (readCache.contains(id) && tempDupeMap.containsKey(id.toString())) {
                    id = tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt(); //  IN CASE OF LEVEL III
                    if (readCache.contains(id) && tempDupeMap.containsKey(id.toString())) {
                        id = tempDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt();
                    }
                }
                readCache.add(id);
                slots.add(new AbilSlot(id, name, slot));
            }
        });
        return slots;
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
        List<Text> lore = getLoreFromItemStack(slot.getStack());
        Text nameText = slot.getStack().getName();
        String name = removeFormat(nameText.getString());
        if (unlockedCache.contains(id)) {
            unlockedSlots.add(new AbilSlot(id, name, slot));
            pageCache.add(id);
            unlockedCache.add(id);
            idToChildren.get(id).stream().sorted().forEach(integer -> travFindUnlocked(integer, pageCache, checked, allSlots, unlockedSlots));
            return;
        }
        if (lore == null || lore.isEmpty()) return;
        if (name.startsWith("Unlock ")) return;
        String lastStr = removeFormat(lore.getLast().getString());
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
        List<Slot> filtered = handler.slots.stream().filter(slot -> !slot.getStack().isEmpty() && nameToId.containsKey(removeNum(removeFormat(slot.getStack().getName().getString()
                .replace("Unlock ", "")
                .replace(" ability", "").trim())))).toList();
        Map<Integer, Slot> idSlotMap = new HashMap<>();
        filtered.forEach(slot -> {
            String name = removeNum(removeFormat(slot.getStack().getName().getString()
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
            endNodes.forEach(node -> {
                travFindUnlocked(node, pageCache, checked, idSlotMap, slots);
            });
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

    public void renderSaveButtons() {
        AtomicInteger i = new AtomicInteger();
        ImportAtree.getBuilds().stream().filter(save -> save.getCast() == cast)
                .forEach(build -> PRESETBUTTON.addTo(getScreen(), AXISPOS.END, AXISPOS.START, 100, 20, 0, i.getAndAdd(20), Text.literal("Load ").append(build.getName()), button -> ImportAtree.applyBuild(build.getName(), this)));
    }

    public record AbilSlot(int id, String name, Slot slot) {
    }
}
