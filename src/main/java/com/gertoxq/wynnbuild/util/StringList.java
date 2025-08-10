package com.gertoxq.wynnbuild.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class StringList extends ArrayList<String> {
    public StringList(@NotNull Collection<? extends String> c) {
        super(c);
    }

    public StringList() {
        super();
    }
}
