package com.gertoxq.wynnbuild.identifications.metric;

import com.gertoxq.wynnbuild.identifications.SpecialStringID;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Metric<T> {

    static final List<Metric<?>> allMetric = new ArrayList<>();

    private final String name;
    private final Pattern pattern;
    private final boolean fromId;
    private final Function<String, T> realValueFactory;

    private Metric(String name, Pattern pattern, boolean fromId, Function<String, T> factory) {
        this.name = name;
        this.pattern = pattern;
        this.fromId = fromId;
        this.realValueFactory = factory;
        allMetric.add(this);
    }

    public static Metric<String> uniqueString(String name, Pattern pattern) {
        return new Metric<>(name, pattern, true, a -> a);
    }

    public static Metric<Integer> uniqueInt(String name, Pattern pattern) {
        return new Metric<>(name, pattern, true, Integer::parseInt);
    }

    public static Metric<Integer> standardInt(String name, Pattern pattern) {
        return new Metric<>(name, pattern, false, Integer::parseInt);
    }

    public static <T> Advanced<T> parsedUnique(String name, Pattern pattern, Function<String, T> displayToParsed, SpecialStringID.Parser<T> parser) {
        return new Advanced<>(name, pattern, true, displayToParsed, parser);
    }

    public static <T> Advanced<T> parsedUnique(String name, Pattern pattern, SpecialStringID.Parser<T> parser) {
        return new Advanced<>(name, pattern, true, parser);
    }

    public static <T> Advanced<T> parsedUniqueEnum(String name, Pattern pattern, SpecialStringID.Parser<T> parser) {
        return new Advanced<>(name, pattern, true, parser.translator().getter(), parser);
    }

    public static <T> Advanced<T> parsed(String name, Pattern pattern, SpecialStringID.Parser<T> parser) {
        return new Advanced<>(name, pattern, false, parser);
    }

    public static @NotNull <T> Metric<T> other(Class<T> tClass) {
        return new Metric<>("other", null, false, tClass::cast);
    }

    public static @NotNull <T> Metric<T> other() {
        return new Metric<>("other", null, false, null);
    }

    public boolean fromId() {
        return fromId;
    }

    public T getRealValue(String string) {
        return realValueFactory.apply(string);
    }

    public Pattern pattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    public static class Advanced<Parsed> extends Metric<String> {
        public final SpecialStringID.Parser<Parsed> parser;

        private Advanced(String name, Pattern pattern, boolean fromId, SpecialStringID.Parser<Parsed> parser) {
            super(name, pattern, fromId, a -> a);
            this.parser = parser;
        }

        private Advanced(String name, Pattern pattern, boolean fromId, Function<String, Parsed> transformer, SpecialStringID.Parser<Parsed> parser) {
            super(name, pattern, fromId, string -> parser.translator().parse(transformer.apply(string)));
            this.parser = parser;
        }
    }
}
