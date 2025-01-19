package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.custom.ID;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;

public class WynnData {

    private static final Map<Integer, ItemData> dataMap = new HashMap<>();
    private static final Map<String, Integer> nameToId = new HashMap<>();

    public static Map<Integer, ItemData> getData() {
        return dataMap;
    }

    public static Map<String, Integer> getIdMap() {
        return nameToId;
    }

    public static void load() {
        InputStream dataStream = WynnBuild.class.getResourceAsStream("/" + "dataMap.json");
        InputStream dupeStream = WynnBuild.class.getResourceAsStream("/" + "dupes.json");
        InputStream atreeStream = WynnBuild.class.getResourceAsStream("/" + "atree.json");
        InputStream tomeStream = WynnBuild.class.getResourceAsStream("/" + "tomeIdMap.json");
        try {
            assert dataStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dataStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> {
                int id = Integer.parseInt(s);
                JsonArray itemArray = jsonElement.getAsJsonArray(); // ["type":0, "name":"nameidk", "icon": {"id":"mc:iron:horse_amor", "customModelData":1]
                String name = itemArray.get(0).getAsString();
                nameToId.put(name, id);
                ID.ItemType type = ID.ItemType.values()[itemArray.get(1).getAsInt()];
                JsonElement iconEl = itemArray.get(2);
                JsonArray iconArray = iconEl.isJsonNull() ? null : itemArray.get(2).getAsJsonArray();
                Icon icon;
                if (iconArray != null && iconArray.size() == 3) {
                    icon = new Icon(Identifier.ofVanilla(iconArray.get(1).getAsString().split(":")[1]), iconArray.get(2).getAsInt(), null);
                } else if (iconArray != null && iconArray.size() == 2 && Objects.equals(iconArray.get(0).getAsString(), "skin")) {
                    icon = new Icon(null, null, iconArray.get(1).getAsString());
                } else {
                    icon = null;
                }

                JsonElement armorEl = itemArray.get(3);
                String armorMat = armorEl.isJsonNull() ? null : armorEl.getAsString();

                CustomItem item = CustomItem.getCustomFromHash(itemArray.get(4).getAsString(), a -> a
                        .setDisplaysOf(icon, armorMat)
                );

                dataMap.put(id, new ItemData(id, name, type, icon, armorMat, item));
            });

            assert dupeStream != null;
            dupeMap = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dupeStream, StandardCharsets.UTF_8))).asMap();
            assert atreeStream != null;
            fullatree = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(atreeStream, StandardCharsets.UTF_8))).asMap();
            assert tomeStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(tomeStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> tomeMap.put(s, jsonElement.getAsInt()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public record Icon(Identifier id, Integer customModelData, String headId) {
        public boolean isHead() {
            return headId != null;
        }
    }

    public record ItemData(int id, String name, ID.ItemType type, Icon icon, String armorMaterial,
                           CustomItem baseItem) {
        public boolean isArmor() {
            return type.isArmor();
        }

        public boolean isCustomHead() {
            return icon != null && icon.isHead() && type == ID.ItemType.Helmet;
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
