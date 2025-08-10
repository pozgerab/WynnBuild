package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.identifications.metric.Metric;
import com.gertoxq.wynnbuild.util.Range;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;

public class SpecialStringID<T> extends NonRolledString {

    private static final List<SpecialStringID<?>> doubleIds = new ArrayList<>();
    private final Parser<T> parser;
    private final Class<T> ogType;

    SpecialStringID(String name, String displayName, Metric.Advanced<T> metric) {
        super(metric.parser.translator().parse(metric.parser.value()), name, displayName, metric);
        this.parser = metric.parser;
        this.ogType = metric.parser.workClass;
        doubleIds.add(this);
    }

    public static List<SpecialStringID<?>> getDoubleIds() {
        return doubleIds;
    }

    public Class<T> getParsedType() {
        return ogType;
    }

    public Metric.Advanced<T> getMetric() {
        return (Metric.Advanced<T>) metric;
    }

    public String parse(T value) {
        return parser.translator.parser.apply(value);
    }

    public T get(String value) {
        return parser.translator.getter.apply(value);
    }

    public record Parser<T>(T value, Translator<T> translator, Class<T> workClass) {

        public static <O extends Enum<O>> Parser<O> enumParser(O value) {
            return new Parser<>(value, new Translator<>(Enum::toString, s -> O.valueOf(value.getDeclaringClass(), s)), value.getDeclaringClass());
        }

        public static <O extends Enum<O>> Parser<O> enumNullableParser(String nullCase, Class<O> enumClass) {
            return new Parser<>(null, new Translator<>(a -> a != null ? a.toString() : nullCase, s -> {
                try {
                    return O.valueOf(enumClass, s);
                } catch (Exception ignored) {
                    return null;
                }
            }), enumClass);
        }

        public static Parser<Range> rangeParser(Range range) {
            return new Parser<>(range, new Translator<>(Range::toString, s -> {
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

        public static Parser<Range> allowEmptyRangeParser(@Nullable Range range) {
            return new Parser<>(range, new Translator<>(range1 -> range1 == null ? "" : range1.toString(), string -> {
                if (string.isEmpty()) return null;
                Matcher matcher = Range.RANGE_PATTERN.matcher(string);
                if (!matcher.matches()) return null;
                return new Range(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }), Range.class);
        }

        public static Parser<Range> rangeParser() {
            return rangeParser(Range.empty());
        }

        public record Translator<T>(Function<T, String> parser, Function<String, T> getter) {
            public String parse(T val) {
                return parser.apply(val);
            }

            public T get(String val) {
                return getter.apply(val);
            }
        }
    }

}
