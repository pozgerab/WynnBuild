package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.ItemSet;
import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.identifications.ID;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.identifications.TypedID;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.wynnbuild.screens.atree.Ability.fullatree;

public class WynnData {

    private static final Map<Integer, ItemData> dataMap = new HashMap<>();
    private static final Map<String, Integer> nameToId = new HashMap<>();
    private static final Map<String, Integer> tomeMap = new HashMap<>();
    private static final Map<String, ItemSet> setMap = new HashMap<>();
    private static final Map<Range, ItemType> modelToType = new HashMap<>();

    public static Map<Integer, ItemData> getData() {
        return dataMap;
    }

    public static Map<String, Integer> getIdMap() {
        return nameToId;
    }

    public static Map<Range, ItemType> getModelToType() {
        return modelToType;
    }

    public static ItemData getItemData(String itemName) {
        return WynnData.getData().get(WynnData.getIdMap().get(itemName));
    }

    public static ItemSet getItemSet(String setName) {
        return setMap.get(setName);
    }

    public static Map<String, ItemSet> getSetMap() {
        return setMap;
    }

    public static Map<String, Integer> getTomeMap() {
        return tomeMap;
    }

    public static void loadAll() {
        loadItems();
        loadAtree();
        loadSets();
        loadCustomModelData();
    }

    public static void loadItems() {
        InputStream dataStream = WynnBuild.class.getResourceAsStream("/" + "dataMap.json");
        InputStream tomeStream = WynnBuild.class.getResourceAsStream("/" + "tomeIdMap.json");
        try {
            assert dataStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dataStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> {
                int id = Integer.parseInt(s);
                JsonArray itemArray = jsonElement.getAsJsonArray(); // ["type":0, "name":"nameidk", "icon": {"id":"mc:iron:horse_amor", "customModelData":1]
                JsonElement iconEl = itemArray.get(0);
                JsonArray iconArray = iconEl.isJsonNull() ? null : iconEl.getAsJsonArray();
                Icon icon;
                if (iconArray != null && iconArray.size() == 3) {
                    icon = new Icon(Identifier.ofVanilla(iconArray.get(1).getAsString().split(":")[1]), iconArray.get(2).getAsInt(), null);
                } else if (iconArray != null && iconArray.size() == 2 && Objects.equals(iconArray.get(0).getAsString(), "skin")) {
                    icon = new Icon(null, null, iconArray.get(1).getAsString());
                } else {
                    icon = null;
                }

                JsonElement armorEl = itemArray.get(1);
                String armorMat = armorEl.isJsonNull() ? null : armorEl.getAsString();

                Custom item;
                try {
                    item = Custom.decodeCustom(null, itemArray.get(2).getAsString());
                } catch (Exception e) {
                    WynnBuild.error("Failed to decode item: {}", String.valueOf(e));
                    return;
                }

                nameToId.put(item.statMap.get(IDs.NAME), id);

                dataMap.put(id, new ItemData(id, item.statMap.get(IDs.NAME), item.statMap.get(IDs.TYPE), icon, armorMat, item));
            });
            assert tomeStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(tomeStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> tomeMap.put(s, jsonElement.getAsInt()));

        } catch (Exception e) {
            WynnBuild.error("didn't finish init, something went wrong with wynnbuild: {}", e);
            e.printStackTrace();
        }
    }

    public static void loadAtree() {
        InputStream atreeStream = WynnBuild.class.getResourceAsStream("/" + "atree.json");
        assert atreeStream != null;
        fullatree = ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(atreeStream, StandardCharsets.UTF_8))).asMap();
    }

    @SuppressWarnings("unchecked")
    public static void loadSets() {
        InputStream setStream = WynnBuild.class.getResourceAsStream("/" + "sets.json");
        assert setStream != null;
        ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(setStream, StandardCharsets.UTF_8))).asMap().forEach((string, setElement) -> {
            JsonObject setObject = setElement.getAsJsonObject();
            List<Map<TypedID<Integer>, Integer>> bonuses = new ArrayList<>();

            AtomicInteger legalTier = new AtomicInteger();
            AtomicInteger i = new AtomicInteger();

            setObject.getAsJsonArray("bonuses").asList().forEach(bonusMap -> {
                Map<TypedID<Integer>, Integer> bonusTier = new HashMap<>();

                bonusMap.getAsJsonObject().asMap().forEach((idName, idValueElement) -> {

                    if (SP.spIds.stream().map(nonRolledInt -> nonRolledInt.name).toList().contains(idName)) {
                        bonusTier.put((TypedID<Integer>) ID.getByName(idName), idValueElement.getAsInt());
                    } else {
                        if (idName.equals("illegal")) {
                            if (legalTier.get() > 0) return;
                            legalTier.set(i.get());
                        }
                        // Not sp id
                    }
                });

                i.getAndIncrement();
                bonuses.add(bonusTier);
            });
            ItemSet itemSet = new ItemSet(
                    string,
                    setObject.getAsJsonArray("items").asList().stream().map(JsonElement::getAsString).toList(),
                    bonuses,
                    legalTier.get() == 0 ? null : legalTier.get()
            );
            setMap.put(string, itemSet);
        });
    }

    public static void loadCustomModelData() {
        InputStream iconStream = WynnBuild.class.getResourceAsStream("/" + "icons.json");
        assert iconStream != null;
        ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(iconStream, StandardCharsets.UTF_8))).asMap().forEach((itemType, rangeElement) -> {
            JsonObject rangeObject = rangeElement.getAsJsonObject();
            int min = rangeObject.get("min").getAsInt();
            int max = rangeObject.get("max").getAsInt();
            modelToType.put(new Range(min, max), ItemType.valueOf(itemType));
        });
    }

    public record Icon(Identifier id, Integer customModelData, String headId) {
        public boolean isHead() {
            return headId != null;
        }
    }

    public record ItemData(int id, String name, ItemType type, Icon icon, String armorMaterial,
                           Custom baseItem) {
        public boolean isArmor() {
            return type.isArmor();
        }

        public boolean isCustomHead() {
            return icon != null && icon.isHead() && type == ItemType.Helmet;
        }

        public boolean isWeapon() {
            return type.isWeapon();
        }

        public boolean hasIcon() {
            return this.icon != null;
        }

        public boolean hasMaterial() {
            return this.armorMaterial != null;
        }
    }

}
