package com.gertoxq.wynnbuild.base;

import com.wynntils.models.elements.type.Powder;

import java.util.HashMap;
import java.util.Map;

public class PowderUtil {

    public static final int MAX_POWDER_LEVEL = 6;
    private final static Map<Integer, Powder> powderMap = new HashMap<>();
    public static int DEFAULT_POWDER_LEVEL = 6;

    static {
        int powderID = 0;
        for (Powder powder : Powder.values()) {
            for (int i = 1; i <= MAX_POWDER_LEVEL; ++i) {
                powderMap.put(powderID++, powder);
            }
        }
    }

    public static int getId(Powder powder, int level) {
        return powder.ordinal() * 6 + level - 1;
    }

    public static Powder getPowder(int powderId) {
        return powderMap.get(powderId);
    }

}
