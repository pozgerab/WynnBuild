package com.gertoxq.quickbuild.custom;

import com.gertoxq.quickbuild.Base64;
import com.gertoxq.quickbuild.Cast;
import com.gertoxq.quickbuild.Powder;
import com.gertoxq.quickbuild.client.QuickBuildClient;
import com.gertoxq.quickbuild.util.WynnData;
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
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.gertoxq.quickbuild.client.QuickBuildClient.*;
import static com.gertoxq.quickbuild.custom.CustomItem.Data.*;

public class CustomItem {
    public static final String baseStatSchema = "\\S [A-Z][a-zA-Z.]*(?:\\s+[A-Z][a-zA-Z.]*)*: [+-]?\\d+";
    public static final Pattern baseStatRegex = Pattern.compile(baseStatSchema);
    public static final String tilsSchema = "\\s\\[(100(\\.0)?|[1-9]?[0-9](\\.[0-9])?)%].*";
    public static final String bonusSchema = "[+-]?\\d+%? [A-Za-z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*";
    public static final Pattern bonusRegex = Pattern.compile(bonusSchema);
    public static final String rangeSchema = "\\S [A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*: \\d+-\\d+";
    public static final Pattern rangeRegex = Pattern.compile(rangeSchema);
    public static final String perxSchema = "[+-]\\d+/[35]s .*";
    public static final Pattern perxRegex = Pattern.compile(perxSchema);
    public static final String slotSchema = "^\\[([0-5])/([1-5])] Powder Slots(?: .*)?";
    public static final Pattern slotRegex = Pattern.compile(slotSchema);
    private static final List<TypedID<Integer>> percentable = ID.getByTypedMetric(ID.Metric.PERCENT);
    private static final List<TypedID<Integer>> raws = ID.getByTypedMetric(ID.Metric.RAW);
    private static final List<DoubleID<DoubleID.Range, String>> rangeds = ID.getByDoubleMetric(ID.Metric.RANGE);
    private static final List<TypedID<Integer>> perxs = ID.getByTypedMetric(ID.Metric.PERXS);
    public final Map<String, Object> statMap;
    public Item material;
    public int modelData = 0;
    public String headId;

    public CustomItem(Map<String, Object> statMap) {
        this.statMap = statMap;
        statMap.putIfAbsent("minRolls", new HashMap<String, Integer>());
        statMap.putIfAbsent("maxRolls", new HashMap<String, Integer>());
        ID.values().forEach(ids -> {
            if (ids instanceof NonRolledID<?>) {
                statMap.putIfAbsent(ids.name, ids.defaultValue);
            } else if (ids instanceof RolledID rolled) {
                setRolledIfAbsent(rolled, rolled.defaultValue);
            }
        });
    }

    public CustomItem() {
        this(new HashMap<>());
    }

    public static @Nullable CustomItem getItem(@NotNull ItemStack item) {
        return getItem(item, null);
    }

    public static @Nullable CustomItem getItem(@NotNull ItemStack item, ID.ItemType defType) {
        CustomItem custom = new CustomItem();

        //  TODO:   FIX ID RECOGNITION: CURRENTLY DOESNT KNOW IF RAW OR PERCENT I GUESS COULD BE ANY PROBLEM :(

        custom.material = item.getItem();
        try {
            custom.modelData = item.get(DataComponentTypes.CUSTOM_MODEL_DATA).value();
        } catch (NullPointerException e) {
            System.out.println("No custom model data, prob armor or custom item");
        }

        TextColor nameColor = item.getName().getStyle().getColor();
        String name = removeTilFormat(removeFormat(item.getName().getString()));

        Item defItem = item.getItem();
        ID.Tier tier = Stream.of(ID.Tier.values()).filter(t -> Objects.equals(TextColor.fromFormatting(t.format), nameColor)).findAny().orElse(ID.Tier.Normal);

        custom.set(AllIDs.TIER, tier);

        custom.set(AllIDs.NAME, name);

        List<Text> lore = getLoreFromItemStack(item);
        if (lore == null) return null;

        if (custom.getType().isWeapon()) {          //  WILL BE SET LATER, THIS IS JUST IN CASE A VALUE WON'T BE FOUND
            custom.set(AllIDs.ATKSPD, ID.ATKSPDS.NORMAL);
        }

        lore.forEach(text -> {
            String textStr = removeTilFormat(removeFormat(text.getString()));
            custom.setFromString(textStr);
        });


        if (custom.getType().name().equals(AllIDs.TYPE.defaultValue)) {
            for (ID.ItemType type : types) {
                if (defItem.toString().contains(type.name().toLowerCase())) {
                    custom.set(AllIDs.TYPE, type);
                    break;
                }
            }
        }

        Integer maybeId = idMap.getOrDefault(name, null);

        if (maybeId != null && WynnData.getData().containsKey(maybeId)) {
            var def = WynnData.getData().get(maybeId);
            custom.set(AllIDs.TYPE, def.type());
            custom.set(AllIDs.ATKSPD, def.baseItem().get(AllIDs.ATKSPD));
        } else if (defType != null && custom.getType().name().equals(AllIDs.TYPE.defaultValue)) {
            custom.set(AllIDs.TYPE, defType);
        }

        if (custom.get(AllIDs.LVL).equals(0)) {
            return null;
        }

        return custom;
    }

    public static String getItemHash(ItemStack item, ID.ItemType type) {
        CustomItem custom = getItem(item, type);

        return custom == null ? "" : custom.encodeCustom(true);
    }

    private static double log(double b, double n) {
        return Math.log(n) / Math.log(b);
    }

    public static @NotNull String removeTilFormat(@NotNull String string) {
        return string.replaceAll(tilsSchema, "").replace("*", "").replace("?", "");
    }

    @SuppressWarnings("unchecked")
    public static @Nullable CustomItem getCustomFromHash(String hash, Function<CustomItem, CustomItem> after) {
        if (hash == null || hash.isEmpty()) return null;
        String name = hash;
        Map<String, Object> statMap = new HashMap<>();

        try {
            if (name.startsWith("CI-")) {
                name = name.substring(3);
            }

            char version = name.charAt(0);
            boolean fixID = Character.getNumericValue(name.charAt(1)) == 1;
            String tag = name.substring(2);

            statMap.put("minRolls", new HashMap<String, Integer>());
            statMap.put("maxRolls", new HashMap<String, Integer>());

            if (version == '1') {
                statMap.put("fixID", fixID);

                while (!tag.isEmpty()) {
                    String id = ci_save_order.get(Base64.toInt(tag.substring(0, 2)));
                    ID identification = ID.getByName(id);
                    if (identification == null) {
                        System.out.println(id);
                        continue;
                    }
                    int len = 2;
                    try {
                        len = Base64.toInt(tag.substring(2, 4));
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("probably nonrolled-string");
                    }

                    if (identification.rolled) {
                        int sign = Integer.parseInt(tag.substring(4, 5));
                        int minRoll = Base64.toInt(tag.substring(5, 5 + len));

                        if (!fixID) {
                            int maxRoll = Base64.toInt(tag.substring(5 + len, 5 + 2 * len));
                            if (sign > 1) maxRoll *= -1;
                            if (sign % 2 == 1) minRoll *= -1;

                            ((Map<String, Integer>) statMap.get("minRolls")).put(id, minRoll);
                            ((Map<String, Integer>) statMap.get("maxRolls")).put(id, maxRoll);
                            statMap.put(id, minRoll);

                            tag = tag.substring(5 + 2 * len);
                        } else {
                            if (sign != 0) minRoll *= -1;
                            ((Map<String, Integer>) statMap.get("minRolls")).put(id, minRoll);
                            ((Map<String, Integer>) statMap.get("maxRolls")).put(id, minRoll);

                            tag = tag.substring(5 + len);
                        }
                    } else {
                        Object val;
                        if (nonRolled_strings.contains(id)) {
                            switch (id) {
                                case "tier":
                                    val = tiers.get(Base64.toInt(tag.substring(2, 3)));
                                    len = -1;
                                    break;
                                case "type":
                                    val = all_types.get(Base64.toInt(tag.substring(2, 3)));
                                    len = -1;
                                    break;
                                case "atkSpd":
                                    val = attackSpeeds.get(Base64.toInt(tag.substring(2, 3)));
                                    len = -1;
                                    break;
                                case "classReq":
                                    val = classes.get(Base64.toInt(tag.substring(2, 3)));
                                    len = -1;
                                    break;
                                default:
                                    val = tag.substring(4, 4 + len).replace("%20", " ");
                                    break;
                            }
                            tag = tag.substring(4 + len);
                        } else {
                            int sign = Integer.parseInt(tag.substring(4, 5));
                            val = Base64.toInt(tag.substring(5, 5 + len));
                            if (sign == 1) val = -(int) val;
                            tag = tag.substring(5 + len);
                        }
                        if (id.equals("majorIds") && val instanceof String) {
                        }
                        statMap.put(id, val);
                    }
                }
                return after.apply(new CustomItem(statMap));
            }
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println(statMap);
            return null;
        }
        return null;
    }

    public static @Nullable CustomItem getCustomFromHash(String hash) {
        return getCustomFromHash(hash, CustomItem::setDisplays);
    }

    public CustomItem setDisplaysOf(int id) {
        var itemInst = WynnData.getData().get(id);
        setDisplaysOf(itemInst.icon(), itemInst.armorMaterial());
        return this;
    }

    public CustomItem setDisplaysOf(WynnData.Icon icon, String armorMat) {
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

    public CustomItem setDisplays() {
        if (getBaseItemId() == null) return this;
        return setDisplaysOf(getBaseItemId());
    }

    public ItemStack createStack() {
        ItemStack itemStack = new ItemStack(material != null ? material : Items.BARRIER);
        itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(modelData));
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

    public ID.Tier getTier() {
        return get(AllIDs.TIER);
    }

    public ID.ItemType getType() {
        return get(AllIDs.TYPE);
    }

    public String getName() {
        return get(AllIDs.NAME);
    }

    public Integer getBaseItemId() {
        return WynnData.getIdMap().getOrDefault(getName(), null);
    }

    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Integer> minRolls() {
        return (Map<String, Integer>) statMap.get("minRolls");
    }

    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Integer> maxRolls() {
        return (Map<String, Integer>) statMap.get("maxRolls");
    }

    public @NotNull Object get(@NotNull ID id) {
        if (id instanceof NonRolledID<?>) {
            return statMap.getOrDefault(id.name, id.defaultValue);
        } else if (id instanceof RolledID rolled) {
            return minRolls().getOrDefault(id.name, rolled.defaultValue);
        }
        throw new RuntimeException("Object #get ID. Wait, not rolled nor non-rolled??? id: " + id.name);
    }

    public @NotNull Integer getRolled(@NotNull RolledID id) {
        return minRolls().getOrDefault(id.name, id.defaultValue);
    }

    public void setRolled(@NotNull RolledID id, Integer value) {
        minRolls().put(id.name, value);
        maxRolls().put(id.name, value);
    }

    public void setRolledIfAbsent(@NotNull RolledID id, Integer value) {
        minRolls().putIfAbsent(id.name, value);
        maxRolls().putIfAbsent(id.name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T get(@NotNull TypedID<T> id) {
        if (id instanceof NonRolledID<T>) return (T) statMap.getOrDefault(id.name, id.defaultValue);
        else if (id instanceof RolledID rolled) {
            return (T) getRolled(rolled); //    TODO:   FIX THE ENIRE ROLLS SYSTEM!!!
        }
        throw new RuntimeException("T get Typed<T>. Wait, not rolled nor non-rolled??? id: " + id.name);
    }

    public void set(@NotNull ID id, Object value) {
        if (id instanceof RolledID rolled) {
            try {
                setRolled(rolled, (Integer) value);
            } catch (ClassCastException e) {
                e.printStackTrace();
                System.out.println("void set ID Object. Failed to cast to int while id is rolled (only ints)");
            }
        } else {
            statMap.put(id.name, value);
        }
    }

    public <T> void set(@NotNull TypedID<T> id, T value) {
        if (id instanceof RolledID rolledID) {
            setRolled(rolledID, (Integer) value);
        } else statMap.put(id.name, value);
    }

    public <T, R> void set(@NotNull DoubleID<T, R> id, T value) {
        statMap.put(id.name, id.parse(value));
    }

    @SuppressWarnings("unchecked")
    public <T, R> T get(@NotNull DoubleID<T, R> id) {
        return id.getParser().translator().getter().apply((R) statMap.getOrDefault(id.name, id.defaultValue));
    }

    public boolean hasIdentification(@NotNull ID id) {
        return !get(id).equals(id.defaultValue);
    }

    public String encodeCustom(boolean verbose) {
        StringBuilder hashBuilder = new StringBuilder();
        hashBuilder.append("1");
        boolean fixId = get(AllIDs.FIXID);
        hashBuilder.append(0);
        set(AllIDs.FIXID, false);
        //hashBuilder.append(get(AllIDs.FIXID) ? 1 : 0); CURRENTLY DOESNT WORK WITH NEW HPPENG

        for (int i = 0; i < ci_save_order.size(); i++) {
            String id = ci_save_order.get(i);
            ID identification = ID.getByName(id);
            if (identification == null) {
                System.out.println(id + " WAS NULL!!!!");
                continue;
            }
            Object val = get(identification);
            if (identification instanceof RolledID rolledID) {
                int val_min = getRolled(rolledID);
                int val_max = val_min;
                int sign = (val_min < 0 ? 1 : 0) + 2 * (val_max < 0 ? 1 : 0);

                int min_len = Math.max(1, (int) Math.ceil(log(64, Math.abs(val_min) + 1)));
                int max_len = Math.max(1, (int) Math.ceil(log(64, Math.abs(val_max) + 1)));
                int len = Math.max(min_len, max_len);
                val_min = Math.abs(val_min);
                //val_max = Math.abs(val_max);

                if (val_min != 0) {
                    if (get(AllIDs.FIXID)) {
                        hashBuilder.append(Base64.fromIntN(i, 2))
                                .append(Base64.fromIntN(len, 2))
                                .append(sign)
                                .append(Base64.fromIntN(val_min, len));
                    } else {
                        hashBuilder.append(Base64.fromIntN(i, 2))
                                .append(Base64.fromIntN(len, 2))
                                .append(sign)
                                .append(Base64.fromIntN(val_min, len))
                                .append(Base64.fromIntN(val_min, len));

                    }
                }
            } else if (id.equals("majorIds") && val instanceof String) {
            } else if (val instanceof String sVal && !sVal.isEmpty()) {
                if (Data.damages.contains(id) && val.equals("0-0") ||
                        (!verbose && Arrays.asList("lore", "majorIds", "quest", "materials", "drop", "set").contains(id))) {
                    continue;
                }
                switch (id) {
                    case "type" -> hashBuilder.append(Base64.fromIntN(i, 2))
                            .append(Base64.fromIntN(all_types.indexOf(sVal.substring(0, 1).toUpperCase() + sVal.substring(1)), 1));
                    case "tier" -> hashBuilder.append(Base64.fromIntN(i, 2))
                            .append(Base64.fromIntN(tiers.indexOf(sVal), 1));
                    case "atkSpd" -> hashBuilder.append(Base64.fromIntN(i, 2))
                            .append(Base64.fromIntN(attackSpeeds.indexOf(sVal), 1));
                    case "classReq" -> hashBuilder.append(Base64.fromIntN(i, 2))
                            .append(Base64.fromIntN(classes.indexOf(sVal), 1));
                    default -> hashBuilder.append(Base64.fromIntN(i, 2))
                            .append(Base64.fromIntN(sVal.replaceAll(" ", "%20").length(), 2))
                            .append(sVal.replaceAll(" ", "%20"));
                }
            } else if (val instanceof Integer nVal && nVal != 0) {
                int len = Math.max(1, (int) Math.ceil(log(64, Math.abs(nVal) + 1)));
                int sign = (nVal / Math.abs(nVal)) < 0 ? 1 : 0;
                hashBuilder.append(Base64.fromIntN(i, 2))
                        .append(Base64.fromIntN(len, 2))
                        .append(sign)
                        .append(Base64.fromIntN(Math.abs(nVal), len));
            }

        }
        return hashBuilder.toString();

    }

    public List<Text> buildLore() {
        List<Text> lore = new ArrayList<>();
        try {
            ID.ItemType type = get(AllIDs.TYPE);
            Cast cast = type.getCast() != null ? type.getCast() : QuickBuildClient.cast;
            ID.Tier tier = get(AllIDs.TIER);
            String name = get(AllIDs.NAME);

            lore.add(Text.literal(name).styled(style -> style.withColor(tier.format)));

            if (type.isWeapon()) {
                ID.ATKSPDS atsSpd = get(AllIDs.ATKSPD);
                lore.add(Text.literal(String.join(" ", Arrays.stream(atsSpd.name().toLowerCase().split("_")).map(StringUtil::capitalize).toList()) + " Attack Speed").styled(style -> style.withColor(Formatting.GRAY)));
                lore.add(Text.empty());
            } else lore.add(Text.empty());

            List<ID> damageIds = damages.stream().map(ID::getByName).toList();
            AtomicInteger i = new AtomicInteger();
            damageIds.forEach(damId -> {
                if (get(damId).equals(damId.defaultValue)) return;
                Powder.Element element = Powder.Element.getInstance(damId.displayName.split(" ")[0]);
                String damVal = get(damId).toString();
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

            ID.values().stream().filter(ids -> ids.isReq() && ids.metric == ID.Metric.RAW).forEach(ids -> {
                if (get(ids).equals(ids.defaultValue)) return;
                Integer val = (Integer) get(ids);
                lore.add(Text.literal("✔ ").styled(style -> style.withColor(Formatting.GREEN))
                        .append(Text.literal(ids.displayName + ": " + val).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });
            if (get(AllIDs.CLASS_REQ) != null) {
                lore.add(Text.literal("✔ ").styled(style -> style.withColor(Formatting.GREEN))
                        .append(Text.literal(AllIDs.CLASS_REQ.displayName + ": " + get(AllIDs.CLASS_REQ).name).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            }

            lore.add(Text.empty());

            raws.forEach(rawId -> {
                if (rawId.isReq()) return;
                if (get(rawId).equals(rawId.defaultValue)) return;

                int val = get(rawId);

                if (rawId.displayName.contains("&")) {
                    int nO = Integer.parseInt(rawId.displayName.split("&")[1]) - 1;

                    String abilName = cast.abilities.get(nO);
                    lore.add(colorByPos(val, true).append(" ").append(Text.literal(abilName + " Cost").styled(style -> style.withColor(Formatting.GRAY))));
                    i.incrementAndGet();
                    return;
                }
                lore.add(colorByPos(val).append(Text.literal(" " + rawId.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }

            percentable.forEach(ids -> {
                if (get(ids).equals(ids.defaultValue)) return;
                int val = get(ids);
                if (ids.displayName.contains("&")) {
                    int nO = Integer.parseInt(ids.displayName.split("&")[1]) - 1;

                    String abilName = cast.abilities.get(nO);
                    lore.add(colorByPos(val, true).append("% ").append(Text.literal(abilName + " Cost").styled(style -> style.withColor(Formatting.GRAY))));
                    i.incrementAndGet();
                    return;
                }
                lore.add(colorByPos(val).append("%").append(Text.literal(" " + ids.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }

            perxs.forEach(ids -> {
                if (get(ids).equals(ids.defaultValue)) return;
                Integer val = get(ids);
                lore.add(colorByPos(val).append("/ns").append(Text.literal(" " + ids.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
            }
            if (hasIdentification(AllIDs.SLOTS)) {
                lore.add(Text.literal("[0/" + get(AllIDs.SLOTS) + "] Powder Slots").styled(style -> style.withColor(Formatting.GRAY)));
            }
            lore.add(Text.literal(tier + " " + type).styled(style -> style.withColor(tier.format)));

        } catch (Exception ignored) {
        }
        return lore;
    }

    private MutableText colorByPos(int value) {
        return colorByPos(value, false);
    }

    private MutableText colorByPos(int value, boolean reverse) {
        return Text.literal(value > 0 ? "+" + value : String.valueOf(value)).styled(style -> style.withColor(value > 0 == !reverse ? Formatting.GREEN : Formatting.RED));
    }

    public void setFromString(String textStr) {
        int value = 0;
        if (textStr.contains(" Attack Speed")) {
            String atkSpdString = textStr.split(" Attack Speed")[0].replace(" ", "_");
            ID.ATKSPDS atkspd = Arrays.stream(ID.ATKSPDS.values()).filter(atkspds -> atkspds.name().equalsIgnoreCase(atkSpdString)).findAny().orElse(ID.ATKSPDS.NORMAL);
            this.set(AllIDs.ATKSPD, atkspd);
        } else if (baseStatRegex.matcher(textStr).matches()) {
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
            findAndSetIdentification(idName, value, raws);

        } else if (perxRegex.matcher(textStr).matches()) {
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.getFirst();
            try {
                value = Integer.parseInt(strVal.split("/")[0]);
            } catch (Exception ignored) {
            }

            s.removeFirst();

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, perxs);

        } else if (slotRegex.matcher(textStr).matches()) {
            String braces = textStr.split(" ")[0];  //  "[0/3]"
            String slots = braces.replaceAll("[\\[\\]]", "");
            List<String> nums = new ArrayList<>(List.of(slots.split("/")));
            try {
                int max = Integer.parseInt(nums.get(1));
                this.set(AllIDs.SLOTS, max);
            } catch (NumberFormatException ignored) {
            }

        } else if (bonusRegex.matcher(textStr).matches()) {
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.getFirst();
            List<TypedID<Integer>> potentialIds;
            if (strVal.endsWith("%")) {
                potentialIds = percentable;
            } else {
                potentialIds = raws;
            }
            try {
                value = Integer.parseInt(strVal.replace("%", ""));
            } catch (Exception ignored) {
            }

            s.removeFirst();

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, potentialIds);

        } else if (rangeRegex.matcher(textStr).matches()) {
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

            for (DoubleID<DoubleID.Range, String> id : rangeds) {
                if (Objects.equals(id.displayName, idName)) {
                    this.set(id, new DoubleID.Range(from, to));
                    break;
                }
            }
        } else if (textStr.contains(AllIDs.CLASS_REQ.displayName)) {
            textStr = textStr.split(": ")[1];
            String castVal = textStr.split("/")[0];
            Cast itemCast = Cast.find(castVal);
            if (itemCast != null) {
                this.set(AllIDs.CLASS_REQ, itemCast);
            }
        }
    }

    private void findAndSetIdentification(String idName, int value, List<TypedID<Integer>> potential) {
        for (TypedID<Integer> ids : potential) {
            if (ids.isSpellCostReduction()) {
                int nO;
                try {
                    nO = Integer.parseInt(ids.displayName.split("&")[1]) - 1;
                } catch (Exception ignored) {
                    continue;
                }

                if (Arrays.stream(Cast.values()).anyMatch(cast -> idName.equals(cast.abilities.get(nO) + " Cost"))) {
                    set(ids, get(ids) + value);
                    break;
                }
            }
            if (Objects.equals(ids.displayName, idName)) {
                set(ids, ids.name.equals(AllIDs.LVL.name) ? value : (get(ids) + value));
                break;
            }
        }
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

    public static class Data {
        public static List<String> ci_save_order = List.of(
                "name", "lore", "tier", "set", "slots", "type",
                "material", "drop", "quest",
                "nDam", "fDam", "wDam", "aDam", "tDam", "eDam",
                "atkSpd", "hp",
                "fDef", "wDef", "aDef", "tDef", "eDef",
                "lvl", "classReq",
                "strReq", "dexReq", "intReq", "defReq", "agiReq",
                "str", "dex", "int", "agi", "def", "id",
                "skillpoints", "reqs",
                "nDam_", "fDam_", "wDam_", "aDam_", "tDam_", "eDam_",
                "majorIds", "hprPct", "mr",
                "sdPct", "mdPct",
                "ls", "ms", "xpb", "lb",
                "ref", "thorns", "expd", "spd", "atkTier", "poison", "hpBonus", "spRegen", "eSteal", "hprRaw",
                "sdRaw", "mdRaw",
                "fDamPct", "wDamPct", "aDamPct", "tDamPct", "eDamPct",
                "fDefPct", "wDefPct", "aDefPct", "tDefPct", "eDefPct",
                "spPct1", "spRaw1", "spPct2", "spRaw2", "spPct3", "spRaw3", "spPct4", "spRaw4",
                "rSdRaw",
                "sprint", "sprintReg", "jh", "lq", "gXp", "gSpd", "durability", "duration", "charges", "maxMana", "critDamPct",
                /*"sdRaw", "rSdRaw",*/ "nSdRaw", "eSdRaw", "tSdRaw", "wSdRaw", "fSdRaw", "aSdRaw",
                /*"sdPct",*/ "rSdPct", "nSdPct", "eSdPct", "tSdPct", "wSdPct", "fSdPct", "aSdPct",
                /*"mdRaw",*/ "rMdRaw", "nMdRaw", "eMdRaw", "tMdRaw", "wMdRaw", "fMdRaw", "aMdRaw",
                /*"mdPct",*/ "rMdPct", "nMdPct", "eMdPct", "tMdPct", "wMdPct", "fMdPct", "aMdPct",
                "damRaw", "rDamRaw", "nDamRaw", "eDamRaw", "tDamRaw", "wDamRaw", "fDamRaw", "aDamRaw",
                "damPct", "rDamPct", "nDamPct", /*"eDamPct", "tDamPct", "wDamPct", "fDamPct", "aDamPct",*/
                "healPct",
                "kb", "weakenEnemy", "slowEnemy",
                "rDefPct"
        );
        public static List<String> all_types = List.of("Helmet", "Chestplate", "Leggings", "Boots",
                "Ring", "Bracelet", "Necklace", "Wand", "Spear", "Bow", "Dagger", "Relik", "Potion", "Scroll", "Food",
                "WeaponTome", "ArmorTome", "GuildTome", "LootrunTome", "GatherXpTome", "DungeonXpTome", "MobXpTome");
        public static List<String> attackSpeeds = List.of("SUPER_SLOW", "VERY_SLOW", "SLOW", "NORMAL", "FAST", "VERY_FAST", "SUPER_FAST");
        public static List<String> damages = List.of("nDam", "eDam", "tDam", "wDam", "fDam", "aDam");
        public static List<String> tiers = List.of("Normal", "Unique", "Rare", "Legendary", "Fabled", "Mythic", "Set", "Crafted");
        public static List<String> classes = List.of("Warrior", "Assassin", "Mage", "Archer", "Shaman");
        public static List<String> nonRolled_strings = List.of(
                "name", "lore", "tier", "set", "type", "material", "drop", "quest", "majorIds",
                "classReq", "atkSpd", "displayName", "nDam", "fDam", "wDam", "aDam", "tDam",
                "eDam", "nDam_", "fDam_", "wDam_", "aDam_", "tDam_", "eDam_", "durability", "duration");
        public static List<String> rolledIDs = List.of(
                "hprPct",
                "mr",
                "sdPct",
                "mdPct",
                "ls",
                "ms",
                "xpb",
                "lb",
                "ref",
                "thorns",
                "expd",
                "spd",
                "atkTier",
                "poison",
                "hpBonus",
                "spRegen",
                "eSteal",
                "hprRaw",
                "sdRaw",
                "mdRaw",
                "fDamPct", "wDamPct", "aDamPct", "tDamPct", "eDamPct",
                "fDefPct", "wDefPct", "aDefPct", "tDefPct", "eDefPct",
                "spPct1", "spRaw1",
                "spPct2", "spRaw2",
                "spPct3", "spRaw3",
                "spPct4", "spRaw4",
                "rSdRaw",
                "sprint",
                "sprintReg",
                "jh",
                "lq",
                "gXp",
                "gSpd",
// wynn2 damages.
                "eMdPct", "eMdRaw", "eSdPct", "eSdRaw",/*"eDamPct,"*/"eDamRaw", "eDamAddMin", "eDamAddMax",
                "tMdPct", "tMdRaw", "tSdPct", "tSdRaw",/*"tDamPct,"*/"tDamRaw", "tDamAddMin", "tDamAddMax",
                "wMdPct", "wMdRaw", "wSdPct", "wSdRaw",/*"wDamPct,"*/"wDamRaw", "wDamAddMin", "wDamAddMax",
                "fMdPct", "fMdRaw", "fSdPct", "fSdRaw",/*"fDamPct,"*/"fDamRaw", "fDamAddMin", "fDamAddMax",
                "aMdPct", "aMdRaw", "aSdPct", "aSdRaw",/*"aDamPct,"*/"aDamRaw", "aDamAddMin", "aDamAddMax",
                "nMdPct", "nMdRaw", "nSdPct", "nSdRaw", "nDamPct", "nDamRaw", "nDamAddMin", "nDamAddMax",      // neutral which is now an element
                /*"mdPct","mdRaw","sdPct","sdRaw",*/"damPct", "damRaw", "damAddMin", "damAddMax",          // These are the old ids. Become proportional.
                "rMdPct", "rMdRaw", "rSdPct",/*"rSdRaw",*/"rDamPct", "rDamRaw", "rDamAddMin", "rDamAddMax",  // rainbow (the "element" of all minus neutral). rSdRaw is rainraw
                "critDamPct",
                "spPct1Final", "spPct2Final", "spPct3Final", "spPct4Final",
                "healPct", "kb", "weakenEnemy", "slowEnemy", "rDefPct", "maxMana"
        );
    }
}