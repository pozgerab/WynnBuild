package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.base.StatMap;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.identifications.*;
import com.gertoxq.wynnbuild.util.Range;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gertoxq.wynnbuild.util.Utils.removeFormat;

public class CustomUtil {

    public static final Pattern BASE_STAT_REGEX = Pattern.compile("\\S [A-Z][a-zA-Z.]*(?:\\s+[A-Z][a-zA-Z.]*)*: [+-]?\\d+");
    public static final Pattern CRAFTED_NAME_PERCENT_PATTERN = Pattern.compile(" \\[.*?]À");
    public static final Pattern ATKSPD_PATTERN = Pattern.compile(Arrays.stream(AtkSpd.values())
            .map(atkspds -> Pattern.quote(atkspds.getDisplayName()))
            .collect(Collectors.joining("|", "(", ")")) + " Attack Speed");
    public static final Pattern ROLLED_PATTERN = Pattern.compile("([+-]\\d+%?)(/\\d+%?)? ([A-Za-z0-9]+(?:\\s+[A-Za-z0-9]+)*)");
    public static final Pattern PERX_REGEX = Pattern.compile("([+-]\\d+/[35]s)(/\\d+/[35]s)? ([A-Za-z0-9]+(?:\\s+[A-Za-z0-9]+)*)");
    public static final Pattern RANGE_REGEX = Pattern.compile("\\S [A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*: \\d+-\\d+");
    public static final List<TypedID<Integer>> PERCENTABLE = ID.getByTypedMetric(Metric.PERCENT);
    public static final List<TypedID<Integer>> RAWS = ID.getByTypedMetric(Metric.RAW);
    public static final List<TypedID<Integer>> RAWS_BONUS = RAWS.stream().filter(integerTypedID -> integerTypedID instanceof RolledID).toList();
    public static final List<TypedID<Integer>> RAWS_BASE = RAWS.stream().filter(integerTypedID -> !(integerTypedID instanceof RolledID)).toList();
    public static final List<SpecialStringID<Range>> RANGEDS = ID.getByDoubleMetric(Metric.RANGE);
    public static final List<TypedID<Integer>> PERXS = ID.getByTypedMetric(Metric.PERXS);

    public static Custom getFromStack(ItemStack item) {

        Custom custom = new Custom();
        custom.statMap.set(IDs.FIXID, true);

        if (item.isEmpty()) {
            custom.statMap.set(IDs.NONE, true);
            return custom;
        }

        custom.material = item.getItem();

        if (item.contains(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            custom.modelData = item.get(DataComponentTypes.CUSTOM_MODEL_DATA).getFloat(0).intValue();
            custom.statMap.set(IDs.TYPE, ItemType.getFromCustomModelData(custom.modelData));
        } else {
            for (ItemType type : ItemType.ARMORS) {
                if (custom.material.toString().contains(type.name().toLowerCase())) {
                    custom.statMap.set(IDs.TYPE, type);
                    break;
                }
            }
        }

        TextColor nameColor = item.getName().getStyle().getColor();

        Tier tier = Stream.of(Tier.values()).filter(t -> Objects.equals(TextColor.fromFormatting(t.format), nameColor)).findAny().orElse(Tier.Normal);
        custom.statMap.set(IDs.TIER, tier);

        String name = removeFormat(item.getName().getString());

        if (tier != Tier.Crafted) {
            custom.statMap.set(IDs.ID, WynnData.getIdMap().getOrDefault(name, -1));
            custom.statMap.set(IDs.CUSTOM, custom.getBaseItemId().isEmpty());
        } else {
            custom.statMap.set(IDs.ID, -2);
            custom.statMap.set(IDs.CRAFTED, true);
            if (CRAFTED_NAME_PERCENT_PATTERN.matcher(name).hasMatch()) {
                name = name.replaceAll(CRAFTED_NAME_PERCENT_PATTERN.pattern(), "");
            }
        }

        custom.statMap.set(IDs.NAME, name);

        List<Text> lore = Utils.getLore(item);
        if (lore == null) {
            custom.statMap.set(IDs.NONE, true);
            return custom;
        }

        lore.forEach(text -> {
            String textStr = removeFormat(text.getString());
            custom.setFromString(textStr);
        });

        return custom;
    }

}
