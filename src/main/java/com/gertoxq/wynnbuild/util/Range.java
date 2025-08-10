package com.gertoxq.wynnbuild.util;

import java.util.regex.Pattern;

public record Range(Integer min, Integer max) {
    public static final Pattern RANGE_PATTERN = Pattern.compile("^(\\d+)-(\\d+)$");

    public static Range empty() {
        return new Range(0, 0);
    }

    @Override
    public String toString() {
        return min.toString() + "-" + max.toString();
    }

    public boolean contains(double value) {
        return value <= max && value >= min;
    }
}
