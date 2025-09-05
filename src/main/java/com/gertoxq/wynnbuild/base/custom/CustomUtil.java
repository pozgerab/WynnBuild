package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.identifications.IDs;
import com.gertoxq.wynnbuild.identifications.SpecialStringID;
import com.gertoxq.wynnbuild.identifications.TypedID;
import com.gertoxq.wynnbuild.util.Range;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.core.components.Models;
import com.wynntils.models.items.items.game.GearItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.gertoxq.wynnbuild.util.Utils.removeFormat;
import static com.gertoxq.wynnbuild.util.Utils.withSign;

public class CustomUtil {

    public static final Pattern CRAFTED_NAME_PERCENT_PATTERN = Pattern.compile(" \\[.*?]");

    public static Custom getFromStack(ItemStack item) {

        Custom custom = new Custom();
        custom.statMap.set(IDs.FIXID, true);

        if (item.isEmpty() || (item.getItem() == Items.SNOW && item.getName().getString().contains("Accessory Slot"))) {
            custom.statMap.set(IDs.NONE, true);
            return custom;
        }

        custom.material = item.getItem();

        if (item.contains(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            custom.modelData = item.get(DataComponentTypes.CUSTOM_MODEL_DATA).getFloat(0).intValue();
            ItemType type = ItemType.getFromCustomModelData(custom.modelData);
            if (type != null) {
                custom.statMap.set(IDs.TYPE, type);
            }
        } else {
            for (ItemType type : ItemType.ARMORS) {
                if (custom.material.toString().contains(type.name().toLowerCase())) {
                    custom.statMap.set(IDs.TYPE, type);
                    break;
                }
            }
        }

        String name = removeFormat(item.getName().getString());
        if (CRAFTED_NAME_PERCENT_PATTERN.matcher(name).find()) {

            custom.statMap.set(IDs.TIER, Tier.Crafted);
            name = name.replaceAll(CRAFTED_NAME_PERCENT_PATTERN.pattern(), "");
            custom.statMap.set(IDs.ID, -2);
            custom.statMap.set(IDs.CRAFTED, true);

        } else {
            TextColor nameColor = item.getName().getStyle().getColor();
            Tier tier = Stream.of(Tier.values()).filter(t -> Objects.equals(TextColor.fromFormatting(t.format), nameColor)).findAny().orElse(Tier.Normal);
            custom.statMap.set(IDs.TIER, tier);

            custom.statMap.set(IDs.ID, WynnData.getIdMap().getOrDefault(name, -1));
            custom.statMap.set(IDs.CUSTOM, custom.getBaseItemId().isEmpty());
        }

        custom.statMap.set(IDs.NAME, name);

        List<Text> lore = Utils.getLore(item);
        if (lore == null) {
            custom.statMap.set(IDs.NONE, true);
            return custom;
        }

        lore.forEach(custom::setFromLoreLine);

        return custom;
    }

    public static Text buildRolledRaw(int value, TypedID<Integer> id) {
        return buildRolledLikeText(value, id, false);
    }

    public static Text buildRolledPercent(int value, TypedID<Integer> id) {
        return buildRolledLikeText(value, id, true);
    }

    private static Text buildRolledLikeText(int value, TypedID<Integer> id, boolean percent) {
        return Text.literal(Utils.withSign(value)).append(percent ? "%" : "").styled(style -> style.withColor(id.isMorePositive() ^ (value < 0) ? Formatting.GREEN : Formatting.RED))
                .append(" ")
                .append(Text.literal(id.displayName).styled(style -> style.withColor(Formatting.GRAY)));
    }

    public static Text buildBaseText(Integer value, TypedID<Integer> id) {
        return Text.literal(id.displayName).styled(style -> style.withColor(Formatting.GRAY))
                .append(": ")
                .append(value.toString());
    }

    public static Text buildRanged(Range rangeStr, SpecialStringID<Range> id) {
        return Text.literal(id.displayName).append(": ").append(rangeStr.toString());
    }

    private static Text buildPerx(Integer value, TypedID<Integer> id, boolean is5) {
        return Text.literal(withSign(value) + (is5 ? "/5s" : "/3s")).styled(style -> style.withColor(id.isMorePositive() ^ (value < 0) ? Formatting.GREEN : Formatting.RED))
                .append(" ")
                .append(Text.literal(id.displayName).styled(style -> style.withColor(Formatting.GRAY)));
    }

    public static Text buildPer5(Integer value, TypedID<Integer> id) {
        return buildPerx(value, id, true);
    }

    public static Text buildPer3(Integer value, TypedID<Integer> id) {
        return buildPerx(value, id, false);
    }

    public static Text buildCast(Cast cast) {
        return Text.literal("Class Req: ").append(cast.name());
    }

    public static Text buildMajorId(String value) {
        return Text.literal("+" + value).styled(style -> style.withColor(Formatting.AQUA));
    }

    public static Text buildTier(Tier value) {
        return Text.literal(value + " Item").styled(style -> style.withColor(value.format));
    }

    public static Text buildDuration(Integer value) {
        return Text.literal("- Duration: ").append(value.toString()).append(" Seconds");
    }

    public static Text buildAtkSpdText(AtkSpd atkSpd) {
        return Text.literal(atkSpd.getDisplayName() + " Attack Speed").styled(style -> style.withColor(Formatting.GRAY));
    }

}
