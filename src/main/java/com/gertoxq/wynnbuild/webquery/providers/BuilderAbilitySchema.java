package com.gertoxq.wynnbuild.webquery.providers;

import java.util.List;

public record BuilderAbilitySchema(
        int id,
        String display_name,
        List<Integer> parents,
        List<Integer> dependencies,
        List<Integer> children
) {}
