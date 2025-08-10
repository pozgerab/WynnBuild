package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metric;

public class NonRolledString extends NonRolledID<String> {

    NonRolledString(String defaultValue, String name, String displayName, Metric<String> metric) {
        super(defaultValue, name, displayName, metric);
    }

    NonRolledString(String name) {
        super("", name);
    }

}
