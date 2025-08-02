package com.gertoxq.wynnbuild.identifications;

public class NonRolledInt extends NonRolledID<Integer> {
    NonRolledInt(PutOn on, Integer defaultValue, String name, String displayName, Metric.TypedMetric metric) {
        super(on, defaultValue, name, displayName, metric);
    }

    NonRolledInt(PutOn on, String name) {
        super(on, 0, name);
    }

    NonRolledInt(PutOn on, String name, String displayName, Metric.TypedMetric metic) {
        super(on, 0, name, displayName, metic);
    }
}
