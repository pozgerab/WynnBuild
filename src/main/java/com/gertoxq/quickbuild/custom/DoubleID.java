package com.gertoxq.quickbuild.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DoubleID<T, R> extends TypedID<R> {

    private static final List<DoubleID<?, ?>> doubleIds = new ArrayList<>();
    private final Parser<T, R> defaParser;
    private final DoubleTypedMetric<T, R> metric;
    private final Class<T> ogType;

    DoubleID(PutOn on, String name, String displayName, DoubleTypedMetric<T, R> metric) {
        super(on, metric.translator.translator().parser().apply(metric.translator.value()), name, displayName, metric);
        this.defaParser = metric.translator;
        this.metric = metric;
        this.ogType = metric.translator.workClass;
        doubleIds.add(this);
    }

    public static List<DoubleID<?, ?>> getDoubleIds() {
        return doubleIds;
    }

    public Class<T> getParsedType() {
        return ogType;
    }

    public DoubleTypedMetric<T, R> getMetric() {
        return metric;
    }

    public Parser<T, R> getParser() {
        return defaParser;
    }

    public R parse(T value) {
        return defaParser.translator.parser.apply(value);
    }

    public record Parser<T, R>(T value, Translator<T, R> translator, Class<T> workClass) {

        public static <O extends Enum<O>> Parser<O, String> enumParser(O value) {
            return new Parser<>(value, new Translator<>(Enum::toString, s -> O.valueOf(value.getDeclaringClass(), s)), value.getDeclaringClass());
        }

        @SuppressWarnings("unchecked")
        public static <T> Parser<T, T> simple(T value) {
            return new Parser<>(value, new Translator<>(v -> v, v -> v), (Class<T>) value.getClass());
        }

        public static <O extends Enum<O>> Parser<O, String> enumNullableParser(String nullCase, Class<O> enumClass) {
            return new Parser<>(null, new Translator<>(a -> a != null ? a.toString() : nullCase, s -> {
                try {
                    return O.valueOf(enumClass, s);
                } catch (Exception ignored) {
                    return null;
                }
            }), enumClass);
        }

        public static Parser<Range, String> rangeParser(Range range) {
            return new Parser<>(range, new Translator<>(a -> a.min.toString() + "-" + a.max.toString(), s -> {
                int min = 0;
                int max = 0;
                List<String> strings = new ArrayList<>(Arrays.stream(s.split("-")).toList());
                try {
                    min = Integer.parseInt(strings.get(0));
                    max = Integer.parseInt(strings.get(1));
                } catch (NumberFormatException ignored) {
                }
                return new Range(min, max);
            }), Range.class);
        }

        public static Parser<Range, String> rangeParser() {
            return rangeParser(Range.empty());
        }

        record Translator<T, R>(Function<T, R> parser, Function<R, T> getter) {
        }
    }

    public record Range(Integer min, Integer max) {
        public static Range empty() {
            return new Range(0, 0);
        }
    }

}
