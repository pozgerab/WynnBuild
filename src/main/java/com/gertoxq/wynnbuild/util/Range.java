package com.gertoxq.wynnbuild.util;

public record Range(Integer min, Integer max) {
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
