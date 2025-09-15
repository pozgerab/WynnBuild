package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wynntils.core.components.Models;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record Ability(int id, String name, List<Integer> parents, List<Integer> children, @Nullable Integer pageNum,
                      @Nullable Integer slot, List<Integer> dependencies) {

    final static Map<String, List<Integer>> nameToId = new HashMap<>();
    private static final Map<Integer, Ability> ABILITY_MAP = new HashMap<>();
    public static JsonObject castTreeObj;
    public static Map<String, JsonElement> fullatree;

    public static Map<Integer, Ability> getAbilityMap() {
        return ABILITY_MAP;
    }

    public static Ability getById(int id) {
        return ABILITY_MAP.get(id);
    }

    public static boolean areDifferentLevel(int id1, int id2) {
        var entry = Ability.getById(id1);
        return !entry.parents().contains(id2) || !entry.children().contains(id2);
    }

    public static Optional<Integer> getIdByNameAndSlot(String name, int slot) {
        List<Integer> possibleIds = nameToId.getOrDefault(name, List.of());
        Integer foundId = null;
        for (Integer possibleId : possibleIds) {
            if (Ability.getById(possibleId).slot() == null || Objects.equals(Ability.getById(possibleId).slot(), slot)) {
                foundId = possibleId;
            }
        }
        return Optional.ofNullable(foundId);
    }

    public static void refreshTree() {
        WynnBuild.info("Refreshing atree, should only happen when changing cast...");
        ABILITY_MAP.clear();
        castTreeObj = fullatree.get(Models.Character.getClassType().getName()).getAsJsonObject();
        if (castTreeObj == null) {
            // this shouldn't be null, but if something goes wrong at initialization prevent crash
            WynnBuild.error("Something went wrong with ability tree casting, opening the character menu again should fix it or create an issue ??");
            return;
        }
        for (String key : castTreeObj.keySet()) {
            JsonObject nestedObject = castTreeObj.getAsJsonObject(key);

            String displayName = Utils.removeNum(nestedObject.get("display_name").getAsString());
            List<Integer> parents = nestedObject.getAsJsonArray("parents").asList().stream().map(JsonElement::getAsInt).toList();
            List<Integer> children = nestedObject.getAsJsonArray("children").asList().stream().map(JsonElement::getAsInt).toList();
            List<Integer> dependencies = nestedObject.getAsJsonArray("dependencies").asList().stream().map(JsonElement::getAsInt).toList();

            int id = nestedObject.get("id").getAsInt();

            JsonElement pageNumEl = nestedObject.get("pageNumber");
            Integer pageNum = pageNumEl == null ? null : pageNumEl.getAsInt();
            JsonElement slotEl = nestedObject.get("slot");
            Integer slot = slotEl == null ? null : slotEl.getAsInt();
            if (nameToId.containsKey(displayName)) {
                nameToId.get(displayName).add(id);
            } else {
                nameToId.put(displayName, new ArrayList<>(List.of(id)));
            }
            Ability ability = new Ability(id, displayName, parents, children, pageNum, slot, dependencies);
            ABILITY_MAP.put(id, ability);
        }
    }
}
