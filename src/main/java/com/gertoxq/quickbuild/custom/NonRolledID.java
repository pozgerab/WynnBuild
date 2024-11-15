package com.gertoxq.quickbuild.custom;

public class NonRolledID<E> extends TypedID<E> {

    NonRolledID(PutOn on, E defaultValue, String name, String displayName, TypedMetric<E> metric) {
        super(on, defaultValue, name, displayName, metric, false);
    }

    NonRolledID(PutOn on, E defaultValue, String name) {
        super(on, defaultValue, name, false);
    }
}
