package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.StatMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IDTest {

    @BeforeAll
    public static void setup() {
        IDs.load();
    }

    @Test
    public void ID_shouldParseBackCorrectly() {

        StatMap statMap = new StatMap();
        statMap.set(IDs.MAJOR_IDS, "HERO");
        assertEquals("HERO", statMap.get((NonRolledID<?>) IDs.MAJOR_IDS));
    }

    @Test
    public void ID_majorIDshouldReturnEmpty() {
        assertEquals("", new StatMap().get(IDs.MAJOR_IDS));
    }

}
