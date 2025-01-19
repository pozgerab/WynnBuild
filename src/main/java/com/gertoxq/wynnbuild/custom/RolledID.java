package com.gertoxq.wynnbuild.custom;

public class RolledID extends TypedID<Integer> {

    RolledID(PutOn on, Integer defaultValue, String name, String displayName, TypedMetric<Integer> metric) {
        super(on, defaultValue, name, displayName, metric, true);
    }

    RolledID(PutOn on, Integer defaultValue, String name) {
        super(on, defaultValue, name, true);
    }
}
