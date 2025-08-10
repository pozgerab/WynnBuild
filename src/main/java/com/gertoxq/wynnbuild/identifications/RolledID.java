package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metric;

public class RolledID extends TypedID<Integer> {

    RolledID(String name, String displayName, Metric<Integer> metric) {
        super(0, name, displayName, metric, true);
    }
}
