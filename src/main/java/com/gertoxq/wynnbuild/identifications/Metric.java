package com.gertoxq.wynnbuild.identifications;

import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.util.Range;
import com.gertoxq.wynnbuild.util.StringList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Metric {
    public static final TypedMetric PERCENT = new TypedMetric("percent");
    public static final TypedMetric RAW = new TypedMetric("raw");
    public static final DoubleTypedMetric<Range> RANGE = new DoubleTypedMetric<>("range", SpecialStringID.Parser.rangeParser());
    public static final TypedMetric PERXS = new TypedMetric("perxs");
    public static final StaticMetric REQ = new StaticMetric("req");
    public static final TypedMetric OTHERSTR = new TypedMetric("other");
    public static final TypedMetric OTHERINT = new TypedMetric("other");
    public static final DoubleTypedMetric<Tier> TIER = new DoubleTypedMetric<>("tier", SpecialStringID.Parser.enumParser(Tier.Normal));
    public static final DoubleTypedMetric<ItemType> TYPE = new DoubleTypedMetric<>("type", SpecialStringID.Parser.enumParser(ItemType.Helmet));
    public static final DoubleTypedMetric<AtkSpd> ATTACK_SPEED = new DoubleTypedMetric<>("attack_speed", SpecialStringID.Parser.enumNullableParser("", AtkSpd.class));
    public static final DoubleTypedMetric<Cast> CAST = new DoubleTypedMetric<>("cast", SpecialStringID.Parser.enumNullableParser("", Cast.class));
    public static final TypedMetric BOOL = new TypedMetric("bool");
    public static final DoubleTypedMetric<StringList> MAJOR_IDS = new DoubleTypedMetric<>("major_ids", SpecialStringID.Parser.stringListWrapperParser(new StringList()));

    public static class StaticMetric {
        private final String name;

        private StaticMetric(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class TypedMetric extends StaticMetric {
        private TypedMetric(String name) {
            super(name);
        }

        @Contract(" -> new")
        public static @NotNull TypedMetric createOther() {
            return new TypedMetric("other");
        }
    }

    public static class DoubleTypedMetric<T> extends TypedMetric {
        final SpecialStringID.Parser<T> translator;

        private DoubleTypedMetric(String name, SpecialStringID.Parser<T> translator) {
            super(name);
            this.translator = translator;
        }
    }
}
