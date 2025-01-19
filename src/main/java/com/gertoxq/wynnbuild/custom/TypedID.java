package com.gertoxq.wynnbuild.custom;

import java.util.ArrayList;
import java.util.List;

public class TypedID<T> extends ID {

    private static final List<TypedID<?>> typedIds = new ArrayList<>();
    final Class<T> type;
    final T defaultValue;
    private final TypedMetric<T> metric;

    @SuppressWarnings("unchecked")
    TypedID(PutOn on, T defaultValue, String name, String displayName, TypedMetric<T> metric, boolean rolled) {
        super(on, defaultValue, name, displayName, metric, rolled);
        this.type = (Class<T>) defaultValue.getClass();
        this.defaultValue = defaultValue;
        this.metric = metric;
        typedIds.add(this);
    }

    TypedID(PutOn on, T defaultValue, String name, boolean rolled) {
        this(on, defaultValue, name, "", TypedMetric.createOther(), rolled);
    }

    public static List<TypedID<?>> getTypedIds() {
        return typedIds;
    }

    public Class<T> getType() {
        return type;
    }

    public TypedMetric<T> getMetric() {
        return metric;
    }
}
