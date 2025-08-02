package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.Powder;
import com.gertoxq.wynnbuild.base.StatMap;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.util.Range;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gertoxq.wynnbuild.base.Powder.POWDER_PATTERN;
import static com.gertoxq.wynnbuild.base.custom.CustomUtil.CRAFTED_NAME_PERCENT_PATTERN;
import static com.gertoxq.wynnbuild.base.custom.CustomUtil.ROLLED_PATTERN;
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
        String loreStr = "+39% Main Attack Damage";
        assertTrue(ROLLED_PATTERN.matcher(loreStr).matches());
        custom.setFromString(loreStr);
        assertEquals(new Range(39, 39), custom.statMap.getRange(IDs.MD_PCT));
    }

    @Test
    public void setPowdersFromString() {
        Custom custom = new Custom();
        String line = "[4/4] Powder Slots "
                + Stream.of(Powder.Element.EARTH, Powder.Element.FIRE, Powder.Element.THUNDER, Powder.Element.WATER)
                .map(element -> element.icon).collect(Collectors.joining(" ", "[", "]"));
        assertTrue(POWDER_PATTERN.matcher(line).matches());
        custom.setFromString(line);
        WynnBuild.info("powders = {}", custom.getPowders());
    }

    @Test
    public void testCraftedNamePattern() {
        String craftedName = "Any Crafted Item [100%]À";
        assertTrue(CRAFTED_NAME_PERCENT_PATTERN.matcher(craftedName).find());
        assertEquals("Any Crafted Item", craftedName.replaceAll(CRAFTED_NAME_PERCENT_PATTERN.pattern(), ""));
    }

}