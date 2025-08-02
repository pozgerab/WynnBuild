package com.gertoxq.wynnbuild.identifications;

public class NonRolledString extends NonRolledID<String> {

    NonRolledString(PutOn on, String defaultValue, String name, String displayName, Metric.TypedMetric metric) {
        super(on, defaultValue, name, displayName, metric);
    }

    NonRolledString(PutOn on, String name, String displayName) {
        super(on, "", name, displayName, Metric.OTHERSTR);
    }

    NonRolledString(PutOn on, String name) {
        super(on, "", name);
    }
}
