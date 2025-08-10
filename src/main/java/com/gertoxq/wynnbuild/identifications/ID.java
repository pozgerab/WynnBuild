package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metric;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ID {

    protected final static List<ID> allIDs = new ArrayList<>();
    private static final Map<String, ID> idMap = new HashMap<>(); // for quick reference
    public final @NotNull String name;
    public final @NotNull String displayName;
    public final @NotNull Object defaultValue;
    public final boolean rolled;

    protected ID(@NotNull Object defaultValue, @NotNull String name, @NotNull String displayName, boolean rolled) {
        this.name = name;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.rolled = rolled;
        idMap.put(name, this);
        allIDs.add(this);
    }

    public static List<RolledID> getCostReductionIDs() {
        return List.of(IDs.SP_PCT1, IDs.SP_PCT2, IDs.SP_PCT3, IDs.SP_PCT4, IDs.SP_RAW1, IDs.SP_RAW2, IDs.SP_RAW3, IDs.SP_RAW4);
    }

    public static Object getDefaultTypeValue(Class<?> type) {
        if (type == Integer.class) return 0;
        if (type == String.class) return "";
        if (type == Boolean.class) return false;
        // in case of list
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> TypedID<T> getByNameFrom(String displayName, Metric<T> metric) {
        return (TypedID<T>) TypedID.displayNameAndMetricMap.get(displayName + ":" + metric.getName());
    }

    public static List<ID> values() {
        return ImmutableList.copyOf(allIDs);
    }

    public static ID getByName(String name) {
        return idMap.getOrDefault(name, null);
    }

    public static <T extends ID> Set<T> getByClass(Class<T> idClass) {
        return allIDs.stream().filter(idClass::isInstance).map(idClass::cast).collect(Collectors.toSet());
    }

    public boolean isSpellCostReduction() {
        if (!(this instanceof RolledID)) return false;
        return getCostReductionIDs().contains(this);
    }

    public boolean isMorePositive() {
        return !isSpellCostReduction();
    }

}
