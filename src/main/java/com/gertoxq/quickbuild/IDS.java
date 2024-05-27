package com.gertoxq.quickbuild;

import java.util.HashMap;
import java.util.Map;

public enum IDS {
    STRENGTH,
    DEXTERITY,
    INTELLIGENCE,
    DEFENSE,
    AGILITY;

    public static Map<Integer, IDS> getStatContainerMap() {
        Map<Integer, IDS> map = new HashMap<>();
        map.put(11, STRENGTH);
        map.put(12, DEXTERITY);
        map.put(13, INTELLIGENCE);
        map.put(14, DEFENSE);
        map.put(15, AGILITY);
        return map;
    }
    public static Map<IDS, Integer> createStatMap() {
        Map<IDS, Integer> map = new HashMap<>();

        for (IDS id : IDS.values()) {
            map.put(id, 0);
        }
        return map;
    }
}
