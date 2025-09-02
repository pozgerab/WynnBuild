package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.identifications.ID;

import java.util.List;

public class RestrictedStatMap extends StatMap {

    public final List<ID> ALLOWED;

    public RestrictedStatMap(List<ID> allowed) {
        this.ALLOWED = List.copyOf(allowed);
    }

    @Override
    public void set(ID id, Object value) {
        if (!ALLOWED.contains(id)) throw new IllegalArgumentException(id.name + " NOT ALLOWED IN " + ALLOWED.stream().map(id1 -> id1.name).toList());
        super.set(id, value);
    }
}
