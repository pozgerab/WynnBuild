package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.castTreeObj;

public record Ability(int id, String name, List<Integer> parents, List<Integer> children, @Nullable Integer pageNum,
                      @Nullable Integer slot) {

    private static final Map<Integer, Ability> ABILITY_MAP = new HashMap<>();
    static Map<String, List<Integer>> nameToId = new HashMap<>();

    public static Map<Integer, Ability> getAbilityMap() {
        return ABILITY_MAP;
    }

    public static Ability getById(int id) {
        return ABILITY_MAP.get(id);
    }

    @Contract(" -> new")
    public static @NotNull Ability empty() {
        return new Ability(-1, "Empty", List.of(), List.of(), -1, -1);
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
        System.out.println("Refreshing atree, should only happen when changing cast...");
        ABILITY_MAP.clear();
        for (String key : castTreeObj.keySet()) {
            JsonObject nestedObject = castTreeObj.getAsJsonObject(key);

            String displayName = Utils.removeNum(nestedObject.get("display_name").getAsString());
            List<Integer> parents = nestedObject.get("parents").getAsJsonArray().asList().stream().map(JsonElement::getAsInt).toList();
            List<Integer> children = nestedObject.get("children").getAsJsonArray().asList().stream().map(JsonElement::getAsInt).toList();
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
            Ability ability = new Ability(id, displayName, parents, children, pageNum, slot);
            ABILITY_MAP.put(id, ability);
        }
    }
}
