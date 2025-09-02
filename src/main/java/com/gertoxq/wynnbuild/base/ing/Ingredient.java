package com.gertoxq.wynnbuild.base.ing;

import com.gertoxq.wynnbuild.base.IngredientStatMap;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.identifications.ID;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.identifications.RolledID;
import com.gertoxq.wynnbuild.identifications.TypedID;

import java.util.*;
import java.util.stream.Stream;

public class Ingredient {

    private final int id;
    private final String name;
    private final int lvl;
    private final List<Profession> skills;
    private final IngredientStatMap ids;
    private final Map<PosMod, Integer> posMods;

    public static final List<? extends TypedID<Integer>> POSSIBLE_FIELDS = Stream.of(ID.getByClass(RolledID.class),
            SP.spIds, SP.spReqIds, List.of(IDs.CHARGES, IDs.DURABILITY, IDs.DURATION)).flatMap(Collection::stream).toList();

    public Ingredient(int id, String name, int lvl, List<Profession> skills, IngredientStatMap ids, Map<PosMod, Integer> posMods) {
        this.id = id;
        this.name = name;
        this.lvl = lvl;
        this.skills = skills;
        this.ids = ids;
        this.posMods = posMods;
    }

    public Set<String> fields() {
        return ids.keySet();
    }
}
