package com.gertoxq.wynnbuild.identifications.metric;

import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.identifications.SpecialStringID;
import com.gertoxq.wynnbuild.util.Range;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Metrics {
    public static final Metric<Integer> PERCENT = Metric.standardInt("percent",
            Pattern.compile("(?<value>[+-]\\d+)%(?:/\\d+%)? (?<id>[A-Z0-9][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*)"));

    public static final Metric<Integer> RAW_BONUS = Metric.standardInt("raw_bonus",
            Pattern.compile("(?<value>[+-]\\d+)(?:/\\d+)? (?<id>[A-Z0-9][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*)"));

    public static final Metric<Integer> RAW_BASE = Metric.standardInt("raw_base",
            Pattern.compile("\\S (?<id>[A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*): (?<value>[+-]\\d+)"));

    public static final Metric<Integer> REQS = Metric.standardInt("reqs",
            Pattern.compile("\\S (?<id>[A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z.]*)*): (?<value>\\d+)"));

    public static final Metric.Advanced<Range> RANGE = Metric.parsed("range",
            Pattern.compile("\\S (?<id>[A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*): (?<value>\\d+-\\d+)"),
            SpecialStringID.Parser.rangeParser());

    public static final Metric<Integer> PER5S = Metric.standardInt("per5s",
            Pattern.compile("(?<value>[+-]\\d+)/5s(?:/\\d+/5s)? (?<id>[A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*)"));

    public static final Metric<Integer> PER3S = Metric.standardInt("per3s",
            Pattern.compile("(?<value>[+-]\\d+)/3s(?:/\\d+/3s)? (?<id>[A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*)"));

    public static final Metric.Advanced<ItemType> TYPE = Metric.parsedUniqueEnum("type",
            Pattern.compile(Arrays.stream(ItemType.values()).map(itemType -> Pattern.quote(itemType.name())).collect(Collectors.joining("|", "(?:", ")"))
                    + Arrays.stream(ItemType.values()).map(itemType -> Pattern.quote(itemType.name())).collect(Collectors.joining("|", " (", ")")) + "(?:\\s\\[\\d+/\\d+ Durability])?"),
            SpecialStringID.Parser.enumParser(ItemType.Helmet));

    public static final Metric<String> OTHERSTR = Metric.other(String.class);
    public static final Metric<Boolean> BOOL = Metric.other(Boolean.class);

    public static final Metric<String> MAJOR_IDS = Metric.uniqueString("majorIds",
            Pattern.compile("\\+([A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*):.*"));

    public static final Metric.Advanced<AtkSpd> ATTACK_SPEED = Metric.parsedUnique("atkSpd",
            Pattern.compile(Arrays.stream(AtkSpd.values())
                    .map(atkspds -> Pattern.quote(atkspds.getDisplayName()))
                    .collect(Collectors.joining("|", "(", ")")) + " Attack Speed"),
            string -> AtkSpd.valueOf(string.replace(" ", "_").toUpperCase()),
            SpecialStringID.Parser.enumNullableParser("", AtkSpd.class));

    public static final Metric.Advanced<Cast> CLASS_REQ = Metric.parsedUniqueEnum("classReq",
            Pattern.compile("\\S Class Req: "
                    + Arrays.stream(Cast.values()).map(cast -> Pattern.quote(cast.name())).collect(Collectors.joining("|", "(", ")"))
                    + "/"
                    + Arrays.stream(Cast.values()).map(cast -> Pattern.quote(cast.alias)).collect(Collectors.joining("|", "(?:", ")"))),
            SpecialStringID.Parser.enumNullableParser("", Cast.class));

    public static final Metric<Integer> DURATION = Metric.uniqueInt("duration",
            Pattern.compile("- Duration: (\\d)+ Seconds"));

    public static final Metric<Integer> DURABILITY = Metric.uniqueInt("durability",
            Pattern.compile(".* \\[\\d+/(\\d+) Durability]"));

    public static final Metric<Integer> CHARGES = Metric.uniqueInt("charges", null);

    public static final Metric.Advanced<Tier> TIER = Metric.parsedUniqueEnum("tier",
            Pattern.compile(Arrays.stream(ItemType.values()).map(itemType -> Pattern.quote(itemType.name())).collect(Collectors.joining("|", "(", ")")) + " Item (?:\\[\\d+])?"),
            SpecialStringID.Parser.enumParser(Tier.Normal));

    public static final Metric<Integer> SLOTS = Metric.uniqueInt("slots",
            Pattern.compile("\\[\\d+/(\\d+)] Powder Slots(?: \\[.*])?"));

    public static List<Metric<?>> metrics() {
        return List.copyOf(Metric.allMetric);
    }

}
