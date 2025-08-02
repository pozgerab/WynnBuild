package com.gertoxq.wynnbuild.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void capitalizeAllFirst() {
        assertEquals("Capitalize All Words Like This", Utils.capitalizeAllFirst("capitalize all words like this"));
    }
}