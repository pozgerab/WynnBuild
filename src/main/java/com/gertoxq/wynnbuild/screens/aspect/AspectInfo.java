package com.gertoxq.wynnbuild.screens.aspect;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.components.Models;
import com.wynntils.utils.type.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AspectInfo {

    public static Map<String, Integer> aspectMap;

    public static List<Pair<Integer, Integer>> getAspects() {

        List<Pair<Integer, Integer>> aspects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Optional<String> iasp = Models.Aspect.getEquippedAspect(i);
            if (iasp.isEmpty()) break;

            Optional<Integer> tier = Models.Aspect.getAspectTierByName(iasp.get());
            if (tier.isEmpty())
                throw new RuntimeException("Equipped aspect couldn't be found in owned aspects: " + iasp.get());

            aspects.add(new Pair<>(getAspectId(iasp.get()), tier.get()));

        }

        return aspects;
    }

    public static Integer getAspectId(String aspect) {

        Integer id = aspectMap.get(aspect);

        if (id == null) {
            WynnBuild.warn("Could not find aspect id for {}", aspect);
        }

        return id;
    }
}
