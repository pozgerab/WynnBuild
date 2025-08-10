package com.gertoxq.wynnbuild.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}