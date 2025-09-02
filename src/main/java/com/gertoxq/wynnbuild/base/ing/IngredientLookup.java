package com.gertoxq.wynnbuild.base.ing;

import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.util.WynnData;

import java.util.List;

public class IngredientLookup {

    private final Custom custom;

    private IngredientLookup(Custom custom) {
        this.custom = custom;
    }

    public static IngredientLookup lookup(Custom custom) {
        return new IngredientLookup(custom);
    }
}
