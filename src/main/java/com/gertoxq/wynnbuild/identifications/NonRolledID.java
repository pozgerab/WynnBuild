package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metric;

public class NonRolledID<E> extends TypedID<E> {

    NonRolledID(E defaultValue, String name, String displayName, Metric<E> metric) {
        super(defaultValue, name, displayName, metric, false);
    }

    NonRolledID(E defaultValue, String name) {
        super(defaultValue, name, false);
    }

}
