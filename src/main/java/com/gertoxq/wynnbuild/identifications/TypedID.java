package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.identifications.metric.Metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypedID<T> extends ID {

    public static final Map<String, ID> displayNameAndMetricMap = new HashMap<>(); // "Display Name:metricname" -> ID
    private static final List<TypedID<?>> typedIds = new ArrayList<>();
    private static final Pattern SPELL_COST_PATTERN = Pattern.compile("(\\d)(?:\\Qst\\E|\\Qnd\\E|\\Qrd\\E|\\Qth\\E) Spell Cost");
    public final Metric<T> metric;
    final Class<T> type;
    public T defaultValue;

    @SuppressWarnings("unchecked")
    TypedID(T defaultValue, String name, String displayName, Metric<T> metric, boolean rolled) {
        super(defaultValue, name, displayName, rolled);
        this.type = (Class<T>) defaultValue.getClass();
        this.defaultValue = defaultValue;
        this.metric = metric;
        typedIds.add(this);
        Matcher matcher = SPELL_COST_PATTERN.matcher(displayName);
        if (matcher.matches()) {
            int abilIdx = Integer.parseInt(matcher.group(1)) - 1;
            for (Cast cast : Cast.values()) {
                displayNameAndMetricMap.put(cast.abilities.get(abilIdx) + " Cost:" + metric.getName(), this);
            }
        }
        displayNameAndMetricMap.put(displayName + ":" + metric.getName(), this);
    }

    TypedID(T defaultValue, String name, boolean rolled) {
        this(defaultValue, name, "", Metric.other(), rolled);
    }

    public static List<TypedID<?>> getTypedIds() {
        return typedIds;
    }

    public Class<T> getType() {
        return type;
    }

    public Metric<?> getMetric() {
        return metric;
    }
}
