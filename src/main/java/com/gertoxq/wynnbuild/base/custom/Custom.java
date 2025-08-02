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
import com.gertoxq.wynnbuild.util.Range;
import com.gertoxq.wynnbuild.util.StringList;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import com.mojang.authlib.properties.PropertyMap;
import net.fabricmc.loader.impl.util.StringUtil;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gertoxq.wynnbuild.base.Powder.POWDER_PATTERN;
import static com.gertoxq.wynnbuild.base.custom.CustomUtil.*;
import static com.gertoxq.wynnbuild.util.Utils.capitalizeFirst;
import static com.gertoxq.wynnbuild.util.Utils.log2;

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
        if (item.isEmpty() || Utils.getLore(item) == null) {
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
        //statMap.set(AllIDs.HASH, "CI-" + cursor.bitVector.sliceB64(cursor.currentIndex, cursor.endIndex));

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
            if (id == IDs.MAJOR_IDS) {
                StringList majorIds = new StringList(List.of((String) idVal));
                statMap.set(IDs.MAJOR_IDS, majorIds);
                continue;
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
                if (id == IDs.MAJOR_IDS) {
                    List<String> majorIds = statMap.get(IDs.MAJOR_IDS);
                    if (!majorIds.isEmpty()) {
                        String strVal = majorIds.getFirst();
                        long lenMask = (1L << CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN()) - 1;
                        String encodedText = bootstringEncoder.encode(strVal);
                        customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
                        customVec.append(encodedText.length() & lenMask, CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN());
                        customVec.appendB64(encodedText);
                    }
                    continue;
                }

                if (id instanceof NonRolledString nonRolledString) {
                    String strVal = statMap.get(nonRolledString);
                    if (strVal == null || strVal.isEmpty()) continue;
                    if ((nonRolledString.getMetric().equals(Metric.RANGE) && strVal.equals("0-0"))
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

    public void setFromString(String textStr) {
        int value = 0;
        if (ATKSPD_PATTERN.matcher(textStr).matches()) {
            String atkSpdString = textStr.split(" Attack Speed")[0].replace(" ", "_");
            AtkSpd atkspd = Arrays.stream(AtkSpd.values()).filter(atkspds -> atkspds.name().equalsIgnoreCase(atkSpdString)).findAny().orElse(AtkSpd.NORMAL);
            statMap.set(IDs.ATKSPD, atkspd);
        } else if (BASE_STAT_REGEX.matcher(textStr).matches()) {
            textStr = textStr.replace(":", "");
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.getLast();
            try {
                value = Integer.parseInt(strVal);
            } catch (Exception ignored) {
            }

            s.removeFirst();
            s.removeLast();

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, RAWS_BASE);

        } else if (PERX_REGEX.matcher(textStr).matches()) {
            List<String> s;
            if (textStr.split("/").length > 2) { // if custom /
                List<String> strings = new ArrayList<>(List.of(textStr.split("/", 3)));
                s = new ArrayList<>(List.of(strings.getLast().split(" ")));
            } else {
                s = new ArrayList<>(List.of(textStr.split(" ")));
            }
            String strVal = s.getFirst();
            try {
                value = Integer.parseInt(strVal.split("/")[0]);
            } catch (Exception ignored) {
            }

            s.removeFirst();

            String idName = String.join(" ", s);

            findAndSetIdentification(idName, value, PERXS);

        } else if (POWDER_PATTERN.matcher(textStr).matches()) {
            String braces = textStr.split(" ")[0];  //  "[0/3]"
            String slots = braces.replaceAll("[\\[\\]]", "");
            List<String> nums = new ArrayList<>(List.of(slots.split("/")));
            try {
                int max = Integer.parseInt(nums.get(1));
                statMap.set(IDs.SLOTS, max);
            } catch (NumberFormatException ignored) {
            }
            setPowders(Powder.getPowderFromString(textStr));

        } else if (ROLLED_PATTERN.matcher(textStr).matches()) {
            List<String> s;
            if (textStr.contains("/")) {
                var strings = new ArrayList<>(List.of(textStr.split("/")));
                s = new ArrayList<>(List.of(strings.getLast().split(" ")));
            } else {
                s = new ArrayList<>(List.of(textStr.split(" ")));
            }
            String strVal = s.getFirst();
            List<TypedID<Integer>> potentialIds;
            if (strVal.endsWith("%")) {
                potentialIds = PERCENTABLE;
            } else {
                potentialIds = RAWS;
            }
            try {
                value = Integer.parseInt(strVal.replace("%", ""));
            } catch (Exception e) {
                WynnBuild.warn("This should definately parse: {}. Error: {}", strVal.replace("%", ""), e.getMessage());
            }

            s.removeFirst();

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, potentialIds);

        } else if (RANGE_REGEX.matcher(textStr).matches()) {
            textStr = textStr.replace(":", "");
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.getLast();
            int from = 0;
            int to = 0;
            try {
                var split = strVal.split("-");
                from = Integer.parseInt(split[0]);
                to = Integer.parseInt(split[1]);
            } catch (Exception ignored) {
            }

            s.removeFirst();
            s.removeLast();

            String idName = String.join(" ", s);

            for (SpecialStringID<Range> id : RANGEDS) {
                if (Objects.equals(id.displayName, idName)) {
                    statMap.set(id, new Range(from, to));
                    break;
                }
            }
        } else if (textStr.contains(IDs.CLASS_REQ.displayName)) {
            textStr = textStr.split(": ")[1];
            String castVal = textStr.split("/")[0];
            Optional<@Nullable Cast> itemCast = Cast.find(castVal);
            itemCast.ifPresent(cast -> statMap.set(IDs.CLASS_REQ, cast));
        }
    }

    private void findAndSetIdentification(String idName, int value, List<? extends ID> potential) {
        ID id = findByNameFromList(idName, potential);
        switch (id) {
            case null -> WynnBuild.warn("interesting...");
            case RolledID rolledID -> statMap.setRange(rolledID, new Range(value, value));
            case NonRolledInt nonRolledInt -> statMap.set(nonRolledInt, value);
            default -> WynnBuild.warn("not int?? idName={} metric={}", idName, id.metric.getName());
        }

    }

    private ID findByNameFromList(String idName, List<? extends ID> potential) {
        for (RolledID spellCostReductionId : ID.getCostReductionIDs()) {
            if (!potential.contains(spellCostReductionId)) continue;
            int spellIdx = Integer.parseInt(spellCostReductionId.displayName.substring(0, 1)) - 1;
            for (Cast cast : Cast.values()) {
                if (idName.equals(cast.abilities.get(spellIdx) + " Cost")) {
                    return spellCostReductionId;
                }
            }
        }
        return potential.stream().filter(id -> Objects.equals(id.displayName, idName)).findFirst().orElseGet(() -> {
            WynnBuild.warn("No id named {}", idName);
            return null;
        });
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
        List<Text> lore = new ArrayList<>();
        try {
            ItemType type = statMap.get(IDs.TYPE);
            Cast cast = type.getCast();
            Tier tier = statMap.get(IDs.TIER);
            String name = statMap.get(IDs.NAME);

            lore.add(Text.literal(name).styled(style -> style.withColor(tier.format)));

            if (type.isWeapon()) {
                AtkSpd atsSpd = statMap.get(IDs.ATKSPD);
                lore.add(Text.literal(String.join(" ", Arrays.stream(atsSpd.name().toLowerCase().split("_")).map(StringUtil::capitalize).toList()) + " Attack Speed").styled(style -> style.withColor(Formatting.GRAY)));
                lore.add(Text.empty());
            } else lore.add(Text.empty());

            List<ID> damageIds = Data.damages.stream().map(ID::getByName).toList();
            AtomicInteger i = new AtomicInteger();
            damageIds.forEach(damId -> {
                if (statMap.get(damId).equals(damId.defaultValue)) return;
                Powder.Element element = Powder.Element.getInstance(damId.displayName.split(" ")[0]);
                String damVal = statMap.get(damId).toString();
                if (element == null) {
                    lore.add(Text.literal("✣ " + damId.displayName + ": " + damVal).styled(style -> style.withColor(Formatting.GOLD)));
                } else {
                    lore.add(Text.literal(element.icon + " " + damId.displayName + ": ")
                            .styled(style -> style.withColor(element.format))
                            .append(Text.literal(damVal).styled(style -> style.withColor(Formatting.GRAY))));
                }
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }

            ID.values().stream().filter(ids -> ids.isReq() && ids.metric == Metric.RAW).forEach(ids -> {
                if (statMap.get(ids).equals(ids.defaultValue)) return;
                Integer val = (Integer) statMap.get(ids);
                lore.add(Text.literal("✔ ").styled(style -> style.withColor(Formatting.GREEN))
                        .append(Text.literal(ids.displayName + ": " + val).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });
            if (statMap.get(IDs.CLASS_REQ) != null) {
                lore.add(Text.literal("✔ ").styled(style -> style.withColor(Formatting.GREEN))
                        .append(Text.literal(IDs.CLASS_REQ.displayName + ": " + statMap.get(IDs.CLASS_REQ).name).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            }

            lore.add(Text.empty());

            RAWS_BONUS.forEach(rawId -> {
                if (rawId.isReq()) return;
                if (statMap.get(rawId).equals(rawId.defaultValue)) return;

                int val = statMap.get(rawId);

                if (rawId.displayName.contains("&")) {
                    int nO = Integer.parseInt(rawId.displayName.split("&")[1]) - 1;

                    String abilName = cast.abilities.get(nO);
                    lore.add(colorBySign(val, true).append(" ").append(Text.literal(abilName + " Cost").styled(style -> style.withColor(Formatting.GRAY))));
                    i.incrementAndGet();
                    return;
                }
                lore.add(colorBySign(val).append(Text.literal(" " + rawId.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }

            PERCENTABLE.forEach(ids -> {
                if (statMap.get(ids).equals(ids.defaultValue)) return;
                int val = statMap.get(ids);
                if (ids.displayName.contains("&")) {
                    int nO = Integer.parseInt(ids.displayName.split("&")[1]) - 1;

                    String abilName = cast.abilities.get(nO);
                    lore.add(colorBySign(val, true).append("% ").append(Text.literal(abilName + " Cost").styled(style -> style.withColor(Formatting.GRAY))));
                    i.incrementAndGet();
                    return;
                }
                lore.add(colorBySign(val).append("%").append(Text.literal(" " + ids.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }

            PERXS.forEach(ids -> {
                if (statMap.get(ids).equals(ids.defaultValue)) return;
                Integer val = statMap.get(ids);
                lore.add(colorBySign(val).append("/ns").append(Text.literal(" " + ids.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }
            if (statMap.hasId(IDs.SLOTS)) {
                lore.add(Text.literal("[0/" + statMap.get(IDs.SLOTS) + "] Powder Slots").styled(style -> style.withColor(Formatting.GRAY)));
            }
            lore.add(Text.literal(tier + " " + type).styled(style -> style.withColor(tier.format)));

        } catch (Exception ignored) {
        }
        return lore;
    }

    public Text createItemShowcase(String mainString) {
        return Text.literal(mainString)
                .styled(style -> style.withColor(getTier().format)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Utils.reduceTextList(buildLore())))
                        .withUnderline(true));
    }

    public Text createItemShowcase() {
        return createItemShowcase(getName());
    }

}
