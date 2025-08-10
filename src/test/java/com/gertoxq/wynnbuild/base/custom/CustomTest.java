package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.Powder;
import com.gertoxq.wynnbuild.base.StatMap;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.util.Range;
import net.minecraft.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gertoxq.wynnbuild.base.Powder.POWDER_PATTERN;
import static com.gertoxq.wynnbuild.base.custom.CustomUtil.CRAFTED_NAME_PERCENT_PATTERN;
import static org.junit.jupiter.api.Assertions.*;

class CustomTest {

    @BeforeAll
    public static void setup() {
        IDs.load();
    }

    @Test
    public void Custom_shouldPopulateStatMap() {
        StatMap statMap = new StatMap();
        new Custom(statMap);

        assertEquals(1, statMap.get("lvl"));
        assertEquals("Helmet", statMap.get("type"));
    }

    @Test
    public void Custom_shouldPopulateAtkSpd() {
        StatMap statMapUndefined = new StatMap();
        statMapUndefined.set(IDs.TYPE, ItemType.Spear);
        new Custom(statMapUndefined);

        assertEquals(AtkSpd.NORMAL, statMapUndefined.get(IDs.ATKSPD));

        StatMap statMapDefined = new StatMap();
        statMapDefined.set(IDs.TYPE, ItemType.Spear);
        statMapDefined.set(IDs.ATKSPD, AtkSpd.SUPER_FAST);
        new Custom(statMapDefined);

        assertNotEquals(AtkSpd.NORMAL, statMapDefined.get(IDs.ATKSPD));
    }

    @Test
    public void setFromString_shouldSetAllTypes() {

        Custom custom = new Custom();
        List<Text> lore = new ArrayList<>();
        lore.add(Text.literal("+39% Main Attack Damage"));
        lore.add(Text.literal("+21 Strength"));
        lore.add(Text.literal("? Health: +2000"));
        lore.add(Text.literal("? Neutral Damage: 200-300"));
        lore.add(Text.literal("Super Fast Attack Speed"));
        lore.add(Text.literal("? Class Req: Archer/Hunter"));
        lore.add(Text.literal("? Combat Lv. Min: 105"));
        lore.add(Text.literal("? Intelligence Min: 20"));
        lore.add(Text.literal("-9 Totem Cost"));
        lore.add(Text.literal("+20% 2nd Spell Cost"));
        lore.add(Text.literal("+8/3s Mana Steal"));

        for (Text text : lore) {
            custom.setFromLoreLine(text);
        }

        assertEquals(new Range(39, 39), custom.statMap.getRange(IDs.MD_PCT));
        assertEquals(21, custom.statMap.get(IDs.STR));
        assertEquals(2000, custom.statMap.get(IDs.HP));
        assertEquals(new Range(200, 300), custom.statMap.get(IDs.NDAM));
        assertEquals(AtkSpd.SUPER_FAST, custom.statMap.get(IDs.ATKSPD));
        assertEquals(Cast.Archer, custom.statMap.get(IDs.CLASS_REQ));
        assertEquals(ItemType.Bow, custom.statMap.get(IDs.TYPE));
        assertEquals(20, custom.statMap.get(IDs.INT_REQ));
        assertEquals(105, custom.statMap.get(IDs.LVL));
        assertEquals(new Range(-9, -9), custom.statMap.getRange(IDs.SP_RAW1));
        assertEquals(new Range(20, 20), custom.statMap.getRange(IDs.SP_PCT2));
        assertEquals(new Range(8, 8), custom.statMap.getRange(IDs.MS));
    }

    @Test
    public void setPowdersFromString() {
        Custom custom = new Custom();
        String line = "[4/4] Powder Slots "
                + Stream.of(Powder.Element.EARTH, Powder.Element.FIRE, Powder.Element.THUNDER, Powder.Element.WATER)
                .map(element -> element.icon).collect(Collectors.joining(" ", "[", "]"));
        assertTrue(POWDER_PATTERN.matcher(line).matches());
        custom.setFromLoreLine(Text.literal(line));
        WynnBuild.info("powders = {}", custom.getPowders());
    }

    @Test
    public void testCraftedNamePattern() {
        String craftedName = "Any Crafted Item [100%]";
        assertTrue(CRAFTED_NAME_PERCENT_PATTERN.matcher(craftedName).find());
        assertEquals("Any Crafted Item", craftedName.replaceAll(CRAFTED_NAME_PERCENT_PATTERN.pattern(), ""));
    }

}