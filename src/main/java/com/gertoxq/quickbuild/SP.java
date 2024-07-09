package com.gertoxq.quickbuild;

import java.util.HashMap;
import java.util.Map;

public enum SP {
    STRENGTH,
    DEXTERITY,
    INTELLIGENCE,
    DEFENSE,
    AGILITY;

    public static Map<Integer, SP> getStatContainerMap() {
        Map<Integer, SP> map = new HashMap<>();
        map.put(11, STRENGTH);
        map.put(12, DEXTERITY);
        map.put(13, INTELLIGENCE);
        map.put(14, DEFENSE);
        map.put(15, AGILITY);
        return map;
    }

    public static Map<SP, Integer> createStatMap() {
        Map<SP, Integer> map = new HashMap<>();

        for (SP id : SP.values()) {
            map.put(id, 0);
        }
        return map;
    }
}
