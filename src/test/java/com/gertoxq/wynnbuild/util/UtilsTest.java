package com.gertoxq.wynnbuild.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    @Test
    void capitalizeAllFirst() {
        assertEquals("Capitalize All Words Like This", Utils.capitalizeAllFirst("capitalize all words like this"));
    }

    @Test
    void withSignTest() {
        assertEquals("+91", Utils.withSign(91));
        assertEquals("+0", Utils.withSign(0));
        assertEquals("-3", Utils.withSign(-3));
    }

    @Test
    void treePatternTest() {
        Pattern ABILITY_TREE_PATTERN = Pattern.compile("\udaff\udfea[\ue000|\ue057]");
        assertTrue(ABILITY_TREE_PATTERN.matcher("\uDAFF\uDFEA\uE057").matches());
    }
}