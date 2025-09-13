package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.base.sp.SkillpointList;
import com.gertoxq.wynnbuild.build.Aspect;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.models.character.type.ClassType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncodeDecodeTest {

    @BeforeAll
    public static void setup() {
        IDs.load();
        WynnData.loadAll();
    }

    @Test
    void encodeBuild() {

        Custom helmet = WynnData.getItemData("Keratoconus").baseItem();
        Custom chestplate = WynnData.getItemData("Conduit of Spirit").baseItem();
        Custom leggings = WynnData.getItemData("Sagittarius").baseItem();
        Custom boots = WynnData.getItemData("Steamjet Walkers").baseItem();
        Custom ring1 = WynnData.getItemData("Intensity").baseItem();
        Custom ring2 = WynnData.getItemData("Moon Pool Circlet").baseItem();
        Custom bracelet = WynnData.getItemData("Vortex Bracer").baseItem();
        Custom necklace = WynnData.getItemData("Renda Langit").baseItem();
        Custom weapon = WynnData.getItemData("Hero").baseItem();

        java.util.List<Custom> equipment = java.util.List.of(helmet, chestplate, leggings, boots,
                ring1, ring2, bracelet, necklace, weapon);

        int precise = 0;
        int wynnLevel = 106;
        SkillpointList sp = SkillpointList.of(10, 40, 101, -10, 140);
        SkillpointList sp_assigned = SkillpointList.empty();
        java.util.List<Integer> tomeIdsUnused = java.util.List.of(155, 156, 161, 150, 115, 62, 63, 142);
        java.util.List<Integer> tomeIds = Collections.nCopies(14, -1);

        Set<Integer> atree_state = AtreeCoder.getAtreeCoder(ClassType.WARRIOR).decode_atree("z+xVEX9g3");
        Set<Integer> atree_stateUnused = AtreeCoder.getAtreeCoder(ClassType.WARRIOR).decode_atree("0");
        java.util.List<Aspect> aspects = java.util.List.of();

        Build build = new Build(equipment, precise == 1, sp, sp_assigned, wynnLevel, tomeIds, atree_state, aspects);

        WynnBuild.info("generated hash = {}", build.generateUrl());

    }

    @Test
    public void encodeTomes() {
        java.util.List<Custom> tomes = Stream.of(155, 156, 161, 150, 115, 62, 63, 142).map(integer -> {
            StatMap statMap = new StatMap();
            statMap.set(IDs.ID, integer);
            return new Custom(statMap);
        }).toList();
        assertEquals("lPE7QBFNV+J71", EncodeDecode.encodeTomes(tomes).toB64());
    }

    @Test
    public void encodeSp() {
        String jsEncoded = "OP0";
        Custom helmet = WynnData.getItemData("Keratoconus").baseItem();
        Custom chestplate = WynnData.getItemData("Conduit of Spirit").baseItem();
        Custom leggings = WynnData.getItemData("Sagittarius").baseItem();
        Custom boots = WynnData.getItemData("Steamjet Walkers").baseItem();
        Custom ring1 = WynnData.getItemData("Intensity").baseItem();
        Custom ring2 = WynnData.getItemData("Moon Pool Circlet").baseItem();
        Custom bracelet = WynnData.getItemData("Vortex Bracer").baseItem();
        Custom necklace = WynnData.getItemData("Renda Langit").baseItem();
        Custom weapon = WynnData.getItemData("Hero").baseItem();

        java.util.List<Custom> equipment = java.util.List.of(helmet, chestplate, leggings, boots,
                ring1, ring2, bracelet, necklace, weapon);

        SkillpointList assigned = SkillpointList.of(10, 40, 101, -10, 140);
        SkillpointList ogsp = SkillpointList.empty();

        //assertEquals(jsEncoded, EncodeDecode.encodeSp(assigned, ogsp).toB64());
    }

    @Test
    void encodePowders() {

        java.util.List<Powder> powders = Collections.nCopies(2, Powder.getPowder(Powder.Element.EARTH, 6));
        assertEquals("BE", EncodeDecode.encodePowders(powders).toB64());
    }
}