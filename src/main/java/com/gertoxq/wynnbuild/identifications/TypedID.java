package com.gertoxq.wynnbuild.identifications;

import java.util.ArrayList;
import java.util.List;

public class TypedID<T> extends ID {

    private static final List<TypedID<?>> typedIds = new ArrayList<>();
    public final T defaultValue;
    final Class<T> type;
    private final Metric.TypedMetric metric;

    @SuppressWarnings("unchecked")
    TypedID(PutOn on, T defaultValue, String name, String displayName, Metric.TypedMetric metric, boolean rolled) {
        super(on, defaultValue, name, displayName, metric, rolled);
        this.type = (Class<T>) defaultValue.getClass();
        this.defaultValue = defaultValue;
        this.metric = metric;
        typedIds.add(this);
    }

    TypedID(PutOn on, T defaultValue, String name, boolean rolled) {
        this(on, defaultValue, name, "", Metric.TypedMetric.createOther(), rolled);
    }

    public static List<TypedID<?>> getTypedIds() {
        return typedIds;
    }

    public Class<T> getType() {
        return type;
    }

    public Metric.TypedMetric getMetric() {
        return metric;
    }
}
