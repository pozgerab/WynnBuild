package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.identifications.*;
import com.gertoxq.wynnbuild.util.Range;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StatMap extends HashMap<String, Object> {

    protected final Map<String, Integer> minRolls = new HashMap<>();
    protected final Map<String, Integer> maxRolls = new HashMap<>();

    public int getMin(RolledID id) {
        return minRolls.getOrDefault(id.name, id.defaultValue);
    }

    public int getMax(RolledID id) {
        return maxRolls.getOrDefault(id.name, getMin(id));
    }

    public Range getRange(RolledID id) {
        return new Range(getMin(id), getMax(id));
    }

    public void setMin(RolledID id, int value) {
        minRolls.put(id.name, value);
    }

    public void setMax(RolledID id, int value) {
        maxRolls.put(id.name, value);
    }

    public void setRange(RolledID id, Range range) {
        setMin(id, range.min());
        setMax(id, range.max());
    }

    public <T> T get(ParserID<T> parserID) {
        return parserID.getter().apply(this);
    }

    public ImmutableMap<String, Object> minRolls() {
        return ImmutableMap.copyOf(minRolls);
    }

    public ImmutableMap<String, Object> maxRolls() {
        return ImmutableMap.copyOf(maxRolls);
    }

    public void set(ID id, Object value) {
        put(id.name, value);
    }

    public void setUnknown(ID id, Object value) {
        if (id instanceof RolledID rolledID && value instanceof Integer intval) {
            setRange(rolledID, new Range(intval, intval));
        } else {
            set(id, value);
        }
    }

    public <T> void set(TypedID<T> id, T value) {
        put(id.name, value);
    }

    public Object get(ID id) {
        return get(id.name);
    }

    public <T> T get(TypedID<T> id) {
        if (id instanceof RolledID rolledID) {
            if (!get(IDs.FIXID))
                WynnBuild.warn("You are getting a rolled id while the ids are not fix. Returning minRoll for id {}", id.name);
            return id.getType().cast(getMin(rolledID));
        }
        return id.getType().cast(getOrDefault(id.name, id.defaultValue));
    }

    public <K> void set(SpecialStringID<K> id, K value) {
        put(id.name, id.parse(value));
    }

    public <K> K get(SpecialStringID<K> id) {
        return id.get(get((TypedID<String>) id));
    }

    public boolean hasId(ID id) {
        if (id instanceof RolledID rolledID) {
            return !Objects.equals(getRange(rolledID), Range.empty());
        }
        if (id == IDs.ATKSPD) {
            return !Objects.equals(get(IDs.ATKSPD), null) && !Objects.equals(get(IDs.ATKSPD), AtkSpd.NORMAL);
        }
        return !Objects.equals(get((TypedID<?>) id), id.defaultValue);
    }

    @Override
    public String toString() {
        return "Stats" + super.toString() + "\n\tMinRolls" + minRolls() + "\n\tMaxRolls" + maxRolls();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StatMap statMap)) return super.equals(o);

        return super.equals(statMap) && statMap.minRolls().equals(this.minRolls()) && statMap.maxRolls().equals(this.maxRolls());
    }
}
