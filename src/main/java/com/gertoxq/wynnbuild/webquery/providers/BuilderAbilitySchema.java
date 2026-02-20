package com.gertoxq.wynnbuild.webquery.providers;

import java.util.Set;
import java.util.TreeSet;

public record BuilderAbilitySchema(
        int id,
        String display_name,
        Set<Integer> parents,
        Set<Integer> dependencies,
        TreeSet<Integer> children,
        String archetype,
        int archetype_req
) {
}
