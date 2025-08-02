package com.gertoxq.wynnbuild.util;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.StatMap;
import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.identifications.NonRolledString;
import com.gertoxq.wynnbuild.identifications.RolledIDs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class WynnDataTest {

    @BeforeAll
    public static void setup() {
        IDs.load();
    }

    @Test
    public void shouldAllDecode() {
        assertDoesNotThrow(WynnData::loadItems);
        InputStream dataStream = WynnBuild.class.getResourceAsStream("/" + "dataMap.json");
        assertNotNull(dataStream);
        ((JsonObject) JsonParser.parseReader(
                new InputStreamReader(dataStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> {
            JsonArray itemArray = jsonElement.getAsJsonArray();

            String hash = itemArray.get(2).getAsString();
            Custom item = new Custom(new StatMap());
            try {
                 item = Custom.decodeCustom(null, hash);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assertEquals(item.statMap, Custom.decodeCustom(null, item.encodeCustom(true).toB64()).statMap);
        });
    }

    @Test
    public void testLoadSets() {
        assertDoesNotThrow(WynnData::loadSets);
    }

    @Test
    void testloadCustomModelData() {
        assertDoesNotThrow(WynnData::loadCustomModelData);
    }
}