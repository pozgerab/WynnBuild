package com.gertoxq.wynnbuild.identifications;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ID {

    protected final static List<ID> allIDs = new ArrayList<>();
    public final @NotNull String name;
    public final @NotNull String displayName;
    public final @NotNull Object defaultValue;
    public final Metric.StaticMetric metric;
    public final boolean rolled;
    final @NotNull PutOn on;

    protected ID(@NotNull PutOn on, @NotNull Object defaultValue, @NotNull String name, @NotNull String displayName, Metric.StaticMetric metric, boolean rolled) {
        this.name = name;
        this.displayName = displayName;
        this.on = on;
        this.defaultValue = defaultValue;
        this.metric = metric;
        this.rolled = rolled;
        allIDs.add(this);
    }

    protected ID(PutOn on, Object defaultValue, String name, boolean rolled) {
        this(on, defaultValue, name, "", Metric.OTHERSTR, rolled);
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

    public static List<ID> values() {
        return ImmutableList.copyOf(allIDs);
    }

    public static List<ID> getByMetric(Metric.StaticMetric metric) {
        return allIDs.stream().filter(ids -> ids.metric == metric).toList();
    }

    public static ID getByName(String name) {
        return allIDs.stream().filter(ids -> name.equals(ids.name)).findAny().orElse(null);
    }

    public static ID getByNameIgnoreCase(String name) {
        return allIDs.stream().filter(ids -> name.equalsIgnoreCase(ids.name)).findAny().orElse(null);
    }

    public static <T> @NotNull List<SpecialStringID<T>> getByDoubleMetric(Metric.DoubleTypedMetric<T> metric) {
        List<SpecialStringID<T>> result = new ArrayList<>();

        for (SpecialStringID<?> doubleID : SpecialStringID.getDoubleIds()) {
            if (doubleID.getMetric().equals(metric)) {
                // This cast is safe because we're checking the equality of metrics
                @SuppressWarnings("unchecked")
                SpecialStringID<T> matchedDoubleID = (SpecialStringID<T>) doubleID;
                result.add(matchedDoubleID);
            }
        }

        return result;
    }

    public static <T> @NotNull List<TypedID<T>> getByTypedMetric(Metric.TypedMetric metic) {
        List<TypedID<T>> result = new ArrayList<>();
        for (TypedID<?> id : TypedID.getTypedIds()) {
            if (id.getMetric().equals(metic)) {
                @SuppressWarnings("unchecked")
                TypedID<T> matchedID = (TypedID<T>) id;
                result.add(matchedID);
            }
        }
        return result;
    }

    public static <T extends ID> Set<T> getByClass(Class<T> idClass) {
        return allIDs.stream().filter(idClass::isInstance).map(idClass::cast).collect(Collectors.toSet());
    }

    public boolean isReq() {
        if (!(this instanceof NonRolledID<?>)) return false;
        List<NonRolledID<?>> ids = List.of(IDs.LVL, IDs.CLASS_REQ, IDs.AGI_REQ, IDs.DEF_REQ, IDs.DEX_REQ, IDs.INT_REQ, IDs.STR_REQ);
        return ids.contains(this);
    }

    public boolean isSpellCostReduction() {
        if (!(this instanceof RolledID)) return false;
        return getCostReductionIDs().contains(this);
    }

    public boolean isMorePositive() {
        return !isSpellCostReduction();
    }

    public enum PutOn {
        ALL,
        WEAPON,
        ARMOR,
        CONSUMABLE
    }
}
