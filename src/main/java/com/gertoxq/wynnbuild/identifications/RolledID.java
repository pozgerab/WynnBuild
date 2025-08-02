package com.gertoxq.wynnbuild.identifications;

public class RolledID extends TypedID<Integer> {

    RolledID(String name, String displayName, Metric.TypedMetric metric) {
        super(PutOn.ALL, 0, name, displayName, metric, true);
    }
}
