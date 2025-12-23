package com.gertoxq.wynnbuild.webquery.providers;

import java.util.List;
import java.util.Map;

public record BuilderAtreeSchema(
        Map<String, List<BuilderAbilitySchema>> atree
) {
}
