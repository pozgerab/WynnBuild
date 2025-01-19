package com.gertoxq.wynnbuild.screens.gallery;

import com.gertoxq.wynnbuild.custom.AllIDs;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.custom.DoubleID;
import com.gertoxq.wynnbuild.custom.TypedID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Filter<T> {

    final TypedID<T> watchedId;
    final String string;

    public Filter(TypedID<T> watchedId, String string) {
        this.watchedId = watchedId;
        this.string = string;
    }

    public boolean parseString(CustomItem item) {
        return false;
    }

    public boolean isValid() {
        return false;
    }

    public static class NameFilter extends Filter<String> {

        public NameFilter(String string) {
            super(AllIDs.NAME, string);
        }

        @Override
        public boolean parseString(CustomItem item) {
            return item.get(watchedId).toLowerCase().contains(string.toLowerCase());
        }
    }

    public static class Stringtype extends Filter<String> {

        final static List<String> signs = List.of(">=", "<=", ">", "<");

        public Stringtype(TypedID<String> watchedId, String string) {
            super(watchedId, string);
        }

        @Override
        public boolean parseString(CustomItem item) {
            try {
                if (watchedId instanceof DoubleID<?, ?> doubleWatched) {
                    if (doubleWatched.getParsedType() == DoubleID.Range.class) {
                        @SuppressWarnings("unchecked")
                        DoubleID<DoubleID.Range, String> doubleId = (DoubleID<DoubleID.Range, String>) doubleWatched;

                        try {
                            int value = Integer.parseInt(string);
                            return item.get(doubleId).contains(value);
                        } catch (Exception ignored) {
                        }

                        if (signs.stream().anyMatch(string::startsWith)) {
                            for (String sign : signs) {
                                if (string.contains(sign)) {
                                    try {
                                        int value = Integer.parseInt(string.substring(string.indexOf(sign) + 1));
                                        return inequalityCase(value, sign, item, doubleId);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        } else if (string.contains("-")) {
                            List<String> strings = new ArrayList<>(Arrays.stream(string.split("-")).toList());
                            try {
                                if (strings.size() > 4 || strings.size() < 2)
                                    throw new Exception("more dashes than expected");
                                int min = Integer.parseInt(strings.size() == 3 && Objects.equals(strings.getFirst(), "") ? "-" + strings.get(1) : strings.get(0));
                                int max = Integer.parseInt(strings.size() == 4 && Objects.equals(strings.get(2), "") ? "-" + strings.get(3) : strings.get(1));
                                if (max < min) throw new Exception("min > max ??");
                                return rangeCase(new DoubleID.Range(min, max), item, doubleId);
                            } catch (Exception ignored) {
                            }
                        }

                    }
                }
            } catch (Exception ignored) {
            }
            return item.get(watchedId).equalsIgnoreCase(string);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        public boolean inequalityCase(int value, String sign, CustomItem item, DoubleID<DoubleID.Range, String> id) {

            return switch (sign) {
                case ">" -> item.get(id).max() > value;
                case "<" -> item.get(id).min() < value;
                case ">=" -> item.get(id).max() >= value;
                case "<=" -> item.get(id).min() <= value;
                default -> false;
            } && item.hasIdentification(id);
        }

        public boolean rangeCase(DoubleID.Range range, CustomItem item, DoubleID<DoubleID.Range, String> id) {
            DoubleID.Range itemRange = item.get(id);
            return itemRange.contains(range.max()) || itemRange.contains(range.min())
                    || range.contains(itemRange.max()) || range.contains(itemRange.min());
        }

    }

    public static class Inttype extends Filter<Integer> {

        final static List<String> signs = List.of(">=", "<=", ">", "<");

        public Inttype(TypedID<Integer> watchedId, String string) {
            super(watchedId, string);
        }

        @Override
        public boolean isValid() {
            if (string.equals("*")) return true;

            try {
                Integer.parseInt(string);
                return true;
            } catch (NumberFormatException ignored) {
            }
            if (signs.stream().anyMatch(string::contains)) {
                for (String sign : signs) {
                    if (string.startsWith(sign)) {
                        try {
                            Integer.parseInt(string.substring(string.indexOf(sign) + 1));
                            return true;
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else if (string.contains("-")) {
                List<String> strings = new ArrayList<>(Arrays.stream(string.split("-")).toList());
                try {
                    if (strings.size() > 4 || strings.size() < 2) throw new Exception("more dashes than expected");
                    int min = Integer.parseInt(strings.size() == 3 && Objects.equals(strings.getFirst(), "") ? "-" + strings.get(1) : strings.get(0));
                    int max = Integer.parseInt(strings.size() == 4 && Objects.equals(strings.get(2), "") ? "-" + strings.get(3) : strings.get(1));
                    if (max < min) throw new Exception("min > max ??");
                    return true;
                } catch (Exception ignored) {
                }
            }
            return false;
        }

        @Override
        public boolean parseString(CustomItem item) {

            if (string.equals("*")) return item.hasIdentification(watchedId);

            try {
                return strictCase(Integer.parseInt(string), item);
            } catch (NumberFormatException ignored) {
            }

            if (signs.stream().anyMatch(string::startsWith)) {
                for (String sign : signs) {
                    if (string.contains(sign)) {
                        try {
                            int value = Integer.parseInt(string.substring(string.indexOf(sign) + 1));
                            return inequalityCase(value, sign, item);
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else if (string.contains("-")) {
                List<String> strings = new ArrayList<>(Arrays.stream(string.split("-")).toList());
                try {
                    if (strings.size() > 4 || strings.size() < 2) throw new Exception("more dashes than expected");
                    int min = Integer.parseInt(strings.size() == 3 && Objects.equals(strings.getFirst(), "") ? "-" + strings.get(1) : strings.get(0));
                    int max = Integer.parseInt(strings.size() == 4 && Objects.equals(strings.get(2), "") ? "-" + strings.get(3) : strings.get(1));
                    if (max < min) throw new Exception("min > max ??");
                    return rangeCase(new DoubleID.Range(min, max), item);
                } catch (Exception ignored) {
                }
            }
            return false;
        }

        public boolean rangeCase(DoubleID.Range range, CustomItem item) {
            return item.hasIdentification(watchedId) && range.contains(item.get(watchedId));
        }

        public boolean strictCase(int value, CustomItem item) {
            return item.hasIdentification(watchedId) && item.get(watchedId) == value;
        }

        public boolean inequalityCase(int value, String sign, CustomItem item) {

            return switch (sign) {
                case ">" -> item.get(watchedId) > value;
                case "<" -> item.get(watchedId) < value;
                case ">=" -> item.get(watchedId) >= value;
                case "<=" -> item.get(watchedId) <= value;
                default -> false;
            } && item.hasIdentification(watchedId);
        }
    }
}
