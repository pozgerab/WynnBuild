package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.Powder;
import com.gertoxq.wynnbuild.base.StatMap;
import com.gertoxq.wynnbuild.base.fields.AtkSpd;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.base.util.BitVectorCursor;
import com.gertoxq.wynnbuild.base.util.BootstringEncoder;
import com.gertoxq.wynnbuild.base.util.EncodingBitVector;
import com.gertoxq.wynnbuild.identifications.*;
import com.gertoxq.wynnbuild.identifications.metric.Metric;
import com.gertoxq.wynnbuild.util.WynnData;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.regex.Matcher;

import static com.gertoxq.wynnbuild.base.custom.CustomUtil.getFromStack;
import static com.gertoxq.wynnbuild.identifications.metric.Metrics.*;
import static com.gertoxq.wynnbuild.util.Utils.*;

public class Custom {

    public static final CustomEnc CUSTOM_ENC = new CustomEnc();
    static final BootstringEncoder bootstringEncoder = new BootstringEncoder(0, 1, 52, 104, 700, 38, '-');
    public final StatMap statMap;
    public Item material;
    public int modelData = 0;
    public String headId;
    private List<Powder> powders = new ArrayList<>();

    public Custom(StatMap statMap) {
        this.statMap = statMap;
        ID.getByClass(NonRolledID.class).stream()
                .filter(id -> !Objects.equals(id.defaultValue, ID.getDefaultTypeValue(id.getType())) && !statMap.hasId(id))
                .forEach(id -> statMap.set((TypedID<?>) id, id.defaultValue));

        // after populating if item is weapon set atkspd if not present
        if (statMap.get(IDs.TYPE).isWeapon() && !statMap.hasId(IDs.ATKSPD)) {
            statMap.set(IDs.ATKSPD, AtkSpd.NORMAL);
        }
    }

    public Custom() {
        this(new StatMap());
    }

    public Custom(ItemStack item) {
        if (item.isEmpty() || getLore(item) == null) {
            statMap = new StatMap();
            statMap.set(IDs.NONE, true);
            return;
        }
        this.statMap = getFromStack(item).statMap;

        getBaseItemId().ifPresentOrElse(id -> {
            statMap.set(IDs.ID, id);
            statMap.set(IDs.CUSTOM, false);
        }, () -> statMap.set(IDs.CUSTOM, true));
        statMap.set(IDs.ID, getBaseItemId().orElse(-1));
    }

    public static Custom decodeCustom(BitVectorCursor cursor, String hash) {

        assert cursor != null || hash != null : "Either cursor or hash must be provided";
        if (cursor == null) {
            try {
                cursor = new BitVectorCursor(new BitVector(hash));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid hash provided", e);
            }
        }

        StatMap statMap = new StatMap();
        long legacy = cursor.advance();

        long version = cursor.advanceBy(CUSTOM_ENC.CUSTOM_VERSION_BITLEN());
        boolean fixedIDs = cursor.advanceBy(CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG().BITLEN()) == CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG().FIXED;
        statMap.set(IDs.FIXID, fixedIDs);

        while (cursor.currentIndex + CUSTOM_ENC.ID_IDX_BITLEN() <= cursor.endIndex) {

            int idIdx = cursor.advanceBy(CUSTOM_ENC.ID_IDX_BITLEN());

            ID id = Data.ci_save_order_ids.get(idIdx);

            if (id instanceof RolledID rolledID) {

                int idLen = cursor.advanceBy(CUSTOM_ENC.ID_LENGTH_BITLEN()) + 1;
                int extension = 32 - idLen;
                int minRoll = (cursor.advanceBy(idLen) << extension) >> extension;

                statMap.setMin(rolledID, minRoll);

                if (!fixedIDs) {
                    int maxRoll = (cursor.advanceBy(idLen) << extension) >> extension;
                    statMap.setMax(rolledID, maxRoll);
                } else {
                    statMap.setMax(rolledID, minRoll);
                }
                continue;
            }

            Object idVal;

            if (id instanceof NonRolledString nonRolledString) {

                idVal = switch (nonRolledString.name) {
                    case "type" -> ItemType.values()[cursor.advanceBy(CUSTOM_ENC.ITEM_TYPE_BITLEN())].name();
                    case "tier" -> Tier.values()[cursor.advanceBy(CUSTOM_ENC.ITEM_TIER_BITLEN())].name();
                    case "atkSpd" -> AtkSpd.values()[cursor.advanceBy(CUSTOM_ENC.ITEM_ATK_SPD_BITLEN())].name();
                    case "classReq" -> Cast.values()[cursor.advanceBy(CUSTOM_ENC.ITEM_CLASS_REQ_BITLEN())].name();
                    default -> {
                        long textLen = cursor.advanceBy(CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN()) & 0xFFFFFFFFL;
                        String text = cursor.advanceByChars((int) textLen);
                        yield bootstringEncoder.decode(text);
                    }
                };
            } else {
                int idLen = cursor.advanceBy(CUSTOM_ENC.ID_LENGTH_BITLEN()) + 1;
                int extension = 32 - idLen;
                idVal = cursor.advanceBy(idLen) << extension >> extension;
            }
            statMap.set(id, idVal);
        }
        statMap.set(IDs.CUSTOM, true);
        return new Custom(statMap);
    }

    private static MutableText colorBySign(int value) {
        return Custom.colorBySign(value, false);
    }

    private static MutableText colorBySign(int value, boolean reverse) {
        return Text.literal(value > 0 ? "+" + value : String.valueOf(value)).styled(style -> style.withColor(value > 0 == !reverse ? Formatting.GREEN : Formatting.RED));
    }

    public EncodingBitVector encodeCustom(boolean verbose) {
        EncodingBitVector customVec = new EncodingBitVector(0, 0);

        customVec.append(0, 1);

        customVec.append(CUSTOM_ENC.CUSTOM_ENCODING_VERSION(), CUSTOM_ENC.CUSTOM_VERSION_BITLEN());

        boolean fixedIDs = false;
        if (statMap.get(IDs.FIXID)) {
            fixedIDs = true;
            customVec.appendFlag(CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG(), CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG().FIXED);
        } else {
            customVec.appendFlag(CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG(), CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG().RANGED);
        }

        for (int i = 0; i < Data.ci_save_order.size(); i++) {
            ID id = Data.ci_save_order_ids.get(i);

            if (id instanceof RolledID rolledID) {

                int valMin = statMap.getMin(rolledID);
                int valMax = statMap.getMax(rolledID);
                if (valMin == 0 && valMax == 0) continue;

                customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
                int minLen = (int) Math.max(1, Math.floor(log2(Math.abs(valMin))) + 2);
                int maxLen = (int) Math.max(1, Math.floor(log2(Math.abs(valMax))) + 2);
                int idLen = Math.clamp(minLen, maxLen, 32);
                long mask = (1L << idLen) - 1;
                customVec.append(idLen - 1, CUSTOM_ENC.ID_LENGTH_BITLEN());
                customVec.append(valMin & mask, idLen);
                if (!fixedIDs) customVec.append(valMax & mask, idLen);
            } else {

                if (id instanceof NonRolledString nonRolledString) {
                    String strVal = statMap.get(nonRolledString);
                    if (strVal == null || strVal.isEmpty()) continue;
                    if ((nonRolledString.getMetric().equals(RANGE) && strVal.equals("0-0"))
                            || (!verbose && List.of("lore", "majorIds", "quest", "materials", "drop", "set").contains(nonRolledString.name))) {
                        continue;
                    }

                    customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
                    switch (nonRolledString.name) {
                        case "type" ->
                                customVec.append(Data.all_types.indexOf(capitalizeFirst(strVal)), CUSTOM_ENC.ITEM_TYPE_BITLEN());
                        case "tier" -> customVec.append(Data.tiers.indexOf(strVal), CUSTOM_ENC.ITEM_TIER_BITLEN());
                        case "atkSpd" ->
                                customVec.append(Data.attackSpeeds.indexOf(strVal), CUSTOM_ENC.ITEM_ATK_SPD_BITLEN());
                        case "classReq" ->
                                customVec.append(Data.classes.indexOf(capitalizeFirst(strVal)), CUSTOM_ENC.ITEM_CLASS_REQ_BITLEN());
                        default -> {
                            long lenMask = (1L << CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN()) - 1;
                            String encodedText = bootstringEncoder.encode(strVal);
                            customVec.append(encodedText.length() & lenMask, CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN());
                            customVec.appendB64(encodedText);
                        }
                    }
                } else if (id instanceof NonRolledInt nonRolledInt) {
                    int intVal = statMap.get(nonRolledInt);
                    if (nonRolledInt == IDs.ID) intVal = Math.max(0, intVal);
                    if (Objects.equals(intVal, 0)) continue;

                    customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
                    int len = (int) Math.min(32, Math.floor(log2(Math.abs(intVal))) + 2);
                    long mask = (1L << len) - 1;
                    customVec.append(len - 1, CUSTOM_ENC.ID_LENGTH_BITLEN());
                    customVec.append(intVal & mask, len);
                }
            }
        }

        customVec.append(0, 6 - (customVec.length % 6));
        return customVec;
    }

    public void setFromLoreLine(Text line) {
        String searched = removeFormat(line.getString());
        Metric<?> metric = null;
        Matcher matcher = null;
        for (Metric<?> currMetric : metrics()) {
            if (currMetric.pattern() != null) {
                matcher = currMetric.pattern().matcher(searched);
                if (!matcher.matches()) continue;
                metric = currMetric;
                break;
            }
        }
        if (matcher == null || metric == null) return;

        ID id;
        Object value;

        if (metric.fromId()) {
            id = ID.getByName(metric.getName());
            value = metric.getRealValue(matcher.group(1));
        } else {
            id = ID.getByNameFrom(matcher.group("id"), metric);
            value = metric.getRealValue(matcher.group("value"));
        }

        if (id == null || value == null) {
            WynnBuild.error("Skipping line: could not parse \"{}\". metric = {}, value = {}.", searched, metric.getName(), value);
            return;
        }

        statMap.setUnknown(id, value);

        if (metric == ATTACK_SPEED) {

            if (!statMap.get(IDs.TYPE).isWeapon()) {
                statMap.set(IDs.TYPE, ItemType.Spear); // will be set later
            }
        } else if (metric == CLASS_REQ) {

            Cast cast = CLASS_REQ.parser.translator().get((String) value);
            if (statMap.get(IDs.TYPE).isWeapon()) {
                statMap.set(IDs.TYPE, cast.weapon);
            }
        } else if (metric == SLOTS) {

            setPowders(Powder.getPowderFromString(searched));
        }

    }

    public Optional<Integer> getBaseItemId() {
        return Optional.ofNullable(statMap.get(IDs.ID) == -1 ? null : statMap.get(IDs.ID));
    }

    public Tier getTier() {
        return statMap.get(IDs.TIER);
    }

    public ItemType getType() {
        return statMap.get(IDs.TYPE);
    }

    public String getName() {
        return statMap.get(IDs.NAME);
    }

    public boolean isNone() {
        return statMap.get(IDs.NONE);
    }

    public List<Powder> getPowders() {
        // if custom, powder stats are already calculated
        if (statMap.get(IDs.CUSTOM)) return new ArrayList<>();
        return powders;
    }

    public void setPowders(List<Powder> powders) {
        assert powders.size() <= statMap.get(IDs.SLOTS) : "Powder amount exceeded max";
        this.powders = powders;
    }

    public void addPowder(Powder powder) {
        if (powders.size() <= statMap.get(IDs.SLOTS)) {
            throw new IndexOutOfBoundsException("Powder slots full: " + getName() + " powders: " + powders);
        }
        powders.add(powder);
    }

    public Custom setDisplaysOf(int id) {
        var itemInst = WynnData.getData().get(id);
        setDisplaysOf(itemInst.icon(), itemInst.armorMaterial());
        return this;
    }

    public Custom setDisplays() {
        if (getBaseItemId().isEmpty()) return this;
        return setDisplaysOf(getBaseItemId().get());
    }

    public Custom setDisplaysOf(WynnData.Icon icon, String armorMat) {
        if (icon != null && icon.headId() == null) {
            modelData = icon.customModelData();
            material = Registries.ITEM.get(icon.id());
        } else if (armorMat != null) {
            material = Registries.ITEM.get(Identifier.ofVanilla((armorMat.equals("chain") ? "chainmail" : armorMat) + "_" + getType().name().toLowerCase()));
        } else if (icon != null) {
            material = Items.PLAYER_HEAD;
            headId = icon.headId();
        } else material = Items.BARRIER;
        return this;
    }

    public ItemStack createStack() {
        ItemStack itemStack = new ItemStack(material != null ? material : Items.BARRIER);
        itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(((float) modelData)), List.of(), List.of(), List.of()));
        itemStack.set(DataComponentTypes.LORE, new LoreComponent(buildLore()));
        itemStack.set(DataComponentTypes.ITEM_NAME, Text.literal(getName()).styled(style -> style.withColor(getTier().format)));
        if (headId != null) {
            try {
                itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.of(UUID.fromString(headId)), new PropertyMap()));
            } catch (Exception ignored) {

            }
        }
        return itemStack;
    }

    public List<Text> buildLore() {
        //  placeholder
        return new ArrayList<>();
    }

    public Text createItemShowcase(String mainString) {
        return Text.literal(mainString)
                .styled(style -> style.withColor(getTier().format)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, reduceTextList(buildLore())))
                        .withUnderline(true));
    }

    public Text createItemShowcase() {
        return createItemShowcase(getName());
    }

}
