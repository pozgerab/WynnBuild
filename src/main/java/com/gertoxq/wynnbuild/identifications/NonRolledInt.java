package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metric;

public class NonRolledInt extends NonRolledID<Integer> {
    NonRolledInt(Integer defaultValue, String name, String displayName, Metric<Integer> metric) {
        super(defaultValue, name, displayName, metric);
    }

    NonRolledInt(String name) {
        super(0, name);
    }

    NonRolledInt(String name, String displayName, Metric<Integer> metric) {
        super(0, name, displayName, metric);
    }

}
