package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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