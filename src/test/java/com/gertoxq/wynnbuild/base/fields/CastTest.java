package com.gertoxq.wynnbuild.base.fields;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CastTest {

    @Test
    void testFindByWeapon() {

        for (ItemType itemType : ItemType.values()) {
            assertDoesNotThrow(() -> {
                Cast.findByWeapon(itemType);
            }, "No cast found for weapon type");
        }

    }
}