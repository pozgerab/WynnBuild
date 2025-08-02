package com.gertoxq.wynnbuild.identifications;

public class NonRolledID<E> extends TypedID<E> {

    NonRolledID(PutOn on, E defaultValue, String name, String displayName, Metric.TypedMetric metric) {
        super(on, defaultValue, name, displayName, metric, false);
    }

    NonRolledID(PutOn on, E defaultValue, String name) {
        super(on, defaultValue, name, false);
    }
}
