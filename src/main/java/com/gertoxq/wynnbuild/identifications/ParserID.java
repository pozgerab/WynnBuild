package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.StatMap;

import java.util.function.Function;

public class ParserID<T> {

    final String name;
    final Function<StatMap, ? extends T> getter;

    public ParserID(String name, Function<StatMap, ? extends T> getter) {
        this.name = name;
        this.getter = getter;
    }

    public Function<StatMap, ? extends T> getter() {
        return getter;
    }
}
