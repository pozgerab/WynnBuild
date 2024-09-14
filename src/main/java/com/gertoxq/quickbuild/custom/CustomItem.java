package com.gertoxq.quickbuild.custom;

import com.gertoxq.quickbuild.Base64;
import com.gertoxq.quickbuild.Cast;
import com.gertoxq.quickbuild.Powder;
import com.gertoxq.quickbuild.client.QuickBuildClient;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.gertoxq.quickbuild.client.QuickBuildClient.*;
import static com.gertoxq.quickbuild.custom.CustomItem.Data.*;

public class CustomItem {
    public static final String baseStatSchema = "\\S [A-Z][a-zA-Z.]*(?:\\s+[A-Z][a-zA-Z.]*)*: [+-]?\\d+";
    public static final Pattern baseStatRegex = Pattern.compile(baseStatSchema);
    public static final String tilsSchema = "\\s\\[(100(\\.0)?|[1-9]?[0-9](\\.[0-9])?)%].*";
    public static final Pattern tilsRegex = Pattern.compile(tilsSchema);
    public static final String bonusSchema = "[+-]?\\d+%? [A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*";
    public static final Pattern bonusRegex = Pattern.compile(bonusSchema);
    public static final String rangeSchema = "\\S [A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*: \\d+-\\d+";
    public static final Pattern rangeRegex = Pattern.compile(rangeSchema);
    public static final String perxSchema = "[+-]\\d/[35]s .*";
    public static final Pattern perxRegex = Pattern.compile(perxSchema);
    private static final List<IDS> percentable = IDS.getByMetric(IDS.Metric.PERCENT);
    private static final List<IDS> raws = IDS.getByMetric(IDS.Metric.RAW);
    private static final List<IDS> rangeds = IDS.getByMetric(IDS.Metric.INT_INT);
    private static final List<IDS> perxs = IDS.getByMetric(IDS.Metric.PERXS);
    public Map<String, Object> statMap = new HashMap<>();

    public CustomItem() {
        List.of(IDS.values()).forEach(ids -> statMap.put(ids.name, ids.defaultValue));
    }

    public CustomItem(Map<String, Object> statMap) {
        this.statMap = statMap;
        List.of(IDS.values()).forEach(ids -> statMap.putIfAbsent(ids.name, ids.defaultValue));
    }

    public static @Nullable CustomItem getItem(@NotNull ItemStack item) {
        return getItem(item, null);
    }

    public static @Nullable CustomItem getItem(@NotNull ItemStack item, IDS.ItemType defType) {
        CustomItem custom = new CustomItem();

        TextColor nameColor = item.getName().getStyle().getColor();
        String name = removeTilFormat(item.getName().getString());

        Item defItem = item.getItem();
        IDS.Tier tier = Stream.of(IDS.Tier.values()).filter(t -> Objects.equals(TextColor.fromFormatting(t.format), nameColor)).findAny().orElse(IDS.Tier.Normal);

        custom.set(IDS.TIER, tier.name());

        custom.set(IDS.NAME, name);

        List<Text> lore = getLoreFromItemStack(item);
        if (lore == null) return null;

        lore.forEach(text -> {
            String textStr = removeTilFormat(removeFormat(text.getString()));
            custom.setFromString(textStr);
        });


        if (custom.getType().name() == IDS.TYPE.defaultValue) {
            for (IDS.ItemType type : types) {
                if (defItem.toString().contains(type.name().toLowerCase())) {
                    custom.set(IDS.TYPE, type.name());
                    break;
                }
            }
        }
        if (defType != null && custom.getType().name() == IDS.TYPE.defaultValue) {
            custom.set(IDS.TYPE, defType.name());
        }

        if (custom.statMap.get("lvl").equals(0)) {
            return null;
        }

        if (custom.getType().isWeapon()) {          //  WILL BE SET LATER, THIS IS JUST IN CASE A VALUE WON'T BE FOUND
            custom.set(IDS.ATKSPD, IDS.ATKSPDS.NORMAL.name());
        }
        return custom;
    }

    public static String getItemHash(ItemStack item, IDS.ItemType type) {
        CustomItem custom = getItem(item);

        return custom == null ? "" : custom.encodeCustom(true);
    }

    private static double log(double b, double n) {
        return Math.log(n) / Math.log(b);
    }

    public static @NotNull String removeTilFormat(@NotNull String string) {
        return string.replaceAll(tilsSchema, "").replace("*", "").replace("?", "");
    }

    public static @Nullable CustomItem getCustomFromHash(String hash) {
        if (hash == null || hash.isEmpty()) return null;
        Map<String, Object> statMap = new HashMap<>();

        try {
            if (hash.startsWith("CI-")) {
                hash = hash.substring(3);
            }
            String version = hash.substring(0, 1);
            boolean fixID = Boolean.parseBoolean(String.valueOf(Integer.parseInt(hash.substring(1, 2), 10)));
            String tag = hash.substring(2);

            if (version.equals("1")) {
                if (fixID) {
                    statMap.put("fixID", true);
                }
                while (!tag.isEmpty()) {
                    String id = ci_save_order.get(Base64.toInt(tag.substring(0, 2)));
                    int len = Base64.toInt(tag.substring(2, 4));
                    Object val;
                    if (nonRolled_strings.contains(id)) {
                        switch (id) {
                            case "tier" -> {
                                val = tiers.get(Base64.toInt(tag.substring(2, 3)));
                                len = -1;
                            }
                            case "type" -> {
                                val = all_types.get(Base64.toInt(tag.substring(2, 3)));
                                len = -1;
                            }
                            case "atkSpd" -> {
                                val = attackSpeeds.get(Base64.toInt(tag.substring(2, 3)));
                                len = -1;
                            }
                            case "classReq" -> {
                                System.out.println(tag
                                );
                                System.out.println(Base64.toInt(tag.substring(3, 4)));
                                val = classes.get(Base64.toInt(tag.substring(2, 3)));
                                len = -1;
                            }
                            default -> val = tag.substring(4, 4 + len).replace("%20", " ");
                        }
                        tag = tag.substring(4 + len);
                    } else {
                        int sign = Integer.parseInt(tag.substring(4, 5), 10);
                        val = Base64.toInt(tag.substring(5, 5 + len));
                        if (sign == 1) {
                            val = (Integer) val * -1;
                        }
                        tag = tag.substring(5 + len);
                    }
                    statMap.put(id, val);
                }
                return new CustomItem(statMap);
            }
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println(statMap);
            return null;
        }
        return null;
    }

    public IDS.Tier getTier() {
        return IDS.Tier.valueOf((String) statMap.get(IDS.TIER.name));
    }

    public IDS.ItemType getType() {
        return IDS.ItemType.find(statMap.get(IDS.TYPE.name).toString());
    }

    public String getName() {
        return (String) statMap.get(IDS.NAME.name);
    }

    public Integer getBaseItemId() {
        return idMap.getOrDefault(getName(), null);
    }

    public void set(@NotNull IDS id, Object value) {
        statMap.put(id.name, value);
    }

    public String encodeCustom(boolean verbose) {
        StringBuilder hashBuilder = new StringBuilder();
        hashBuilder.append("11");

        for (int i = 0; i < Data.ci_save_order.size(); i++) {
            String id = Data.ci_save_order.get(i);
            Object val = statMap.get(id);
            if (Objects.equals(id, "majorIds")) {
                if (val instanceof IntList l && !l.isEmpty()) {
                    val = l.getInt(0);
                } else val = "";
            }
            if (val instanceof String sVal && !sVal.isEmpty()) {
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
                            .append(Base64.fromIntN(Data.attackSpeeds.indexOf(sVal), 1));
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
            IDS.ItemType type = IDS.ItemType.find((String) statMap.get(IDS.TYPE.name));
            Cast cast = type.getCast() != null ? type.getCast() : QuickBuildClient.cast;
            IDS.Tier tier = IDS.Tier.valueOf(statMap.get(IDS.TIER.name).toString());
            String name = (String) statMap.get(IDS.NAME.name);

            lore.add(Text.literal(name).styled(style -> style.withColor(tier.format)));

            if (type.isWeapon()) {
                IDS.ATKSPDS atsSpd = IDS.ATKSPDS.find((String) statMap.get(IDS.ATKSPD.name));
                lore.add(Text.literal(String.join(" ", Arrays.stream(atsSpd.name().toLowerCase().split("_")).map(StringUtil::capitalize).toList()) + " Attack Speed").styled(style -> style.withColor(Formatting.GRAY)));
                lore.add(Text.empty());
            } else lore.add(Text.empty());

            List<IDS> damageIds = damages.stream().map(IDS::getByName).toList();
            AtomicInteger i = new AtomicInteger();
            damageIds.forEach(damId -> {
                if (statMap.get(damId.name).equals(damId.defaultValue)) return;
                Powder.Element element = Powder.Element.getInstance(damId.displayName.split(" ")[0]);
                String damVal = statMap.get(damId.name).toString();
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

            Arrays.stream(IDS.values()).filter(ids -> ids.isReq() && ids.metric == IDS.Metric.RAW).forEach(ids -> {
                if (statMap.get(ids.name).equals(ids.defaultValue)) return;
                Integer val = (Integer) statMap.get(ids.name);
                lore.add(Text.literal(ids.displayName + ": " + val).styled(style -> style.withColor(Formatting.GRAY)));
                i.incrementAndGet();
            });

            lore.add(Text.empty());

            raws.forEach(rawId -> {
                if (rawId.isReq()) return;
                if (statMap.get(rawId.name).equals(rawId.defaultValue)) return;

                Integer val = (Integer) statMap.get(rawId.name);

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
                if (statMap.get(ids.name).equals(ids.defaultValue)) return;
                Integer val = (Integer) statMap.get(ids.name);
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
                if (statMap.get(ids.name).equals(ids.defaultValue)) return;
                Integer val = (Integer) statMap.get(ids.name);
                lore.add(colorByPos(val).append("/ns").append(Text.literal(" " + ids.displayName).styled(style -> style.withColor(Formatting.GRAY))));
                i.incrementAndGet();
            });

            if (i.get() != 0) {
                lore.add(Text.empty());
                i.set(0);
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
        if (baseStatRegex.matcher(textStr).matches()) {
            textStr = textStr.replace(":", "");
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.get(s.size() - 1);
            try {
                value = Integer.parseInt(strVal);
            } catch (Exception ignored) {
            }

            s.removeFirst();
            s.removeLast();

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, raws);

        } else if (bonusRegex.matcher(textStr).matches()) {
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.get(0);
            List<IDS> potentialIds;
            if (strVal.endsWith("%")) {
                potentialIds = percentable;
            } else {
                potentialIds = raws;
            }
            try {
                value = Integer.parseInt(strVal.replace("%", ""));
            } catch (Exception ignored) {
            }

            s.remove(0);

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, potentialIds);

        } else if (rangeRegex.matcher(textStr).matches()) {
            textStr = textStr.replace(":", "");
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.get(s.size() - 1);
            int from = 0;
            int to = 0;
            try {
                var split = strVal.split("-");
                from = Integer.parseInt(split[0]);
                to = Integer.parseInt(split[1]);
            } catch (Exception ignored) {
            }

            s.remove(0);
            s.remove(s.size() - 1);

            String idName = String.join(" ", s);

            for (IDS ids : rangeds) {
                if (Objects.equals(ids.displayName, idName) && this.statMap.get(ids.name) instanceof String) {
                    this.set(ids, from + "-" + to);
                    break;
                }
            }
        } else if (perxRegex.matcher(textStr).matches()) {
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.get(0);
            try {
                value = Integer.parseInt(strVal.split("/")[0]);
            } catch (Exception ignored) {
            }

            s.remove(0);

            String idName = String.join(" ", s);
            findAndSetIdentification(idName, value, perxs);

        } else if (textStr.contains(" Attack Speed")) {
            String atkSpdString = textStr.replace(" Attack Speed", "").replace(" ", "_");
            IDS.ATKSPDS atkspd = Arrays.stream(IDS.ATKSPDS.values()).filter(atkspds -> atkspds.name().equalsIgnoreCase(atkSpdString)).findAny().orElse(IDS.ATKSPDS.NORMAL);
            this.set(IDS.ATKSPD, atkspd.name());
        } else if (textStr.contains(IDS.CLASS_REQ.displayName)) {
            textStr = textStr.replace(":", "");
            List<String> s = new ArrayList<>(List.of(textStr.split(" ")));
            String strVal = s.get(s.size() - 1);
            String castVal = strVal.split("/")[0];
            Cast itemCast = Cast.find(castVal);
            if (itemCast != null) {
                this.set(IDS.CLASS_REQ, itemCast.name);
                this.set(IDS.TYPE, itemCast.weapon.name());
            }
        }
    }

    private void findAndSetIdentification(String idName, int value, @NotNull List<IDS> potential) {
        for (IDS ids : potential) {
            if (ids.displayName.contains("&")) {
                int nO;
                try {
                    nO = Integer.parseInt(ids.displayName.split("&")[1]) - 1;
                } catch (Exception ignored) {
                    continue;
                }
                String abilName = cast.abilities.get(nO);
                if (idName.equals(abilName + " Cost") && this.statMap.get(ids.name) instanceof Integer intVal) {
                    this.set(ids, intVal + value);
                    break;
                }
            }
            if (Objects.equals(ids.displayName, idName) && this.statMap.get(ids.name) instanceof Integer intVal) {
                this.set(ids, intVal + value);
                break;
            }
        }
    }

    public static class Data {
        public static List<String> ci_save_order = List.of(
                "name", "lore", "tier", "set", "slots", "type", "material", "drop", "quest",
                "nDam", "fDam", "wDam", "aDam", "tDam", "eDam", "atkSpd", "hp", "fDef", "wDef",
                "aDef", "tDef", "eDef", "lvl", "classReq", "strReq", "dexReq", "intReq", "defReq",
                "agiReq", "str", "dex", "int", "agi", "def", "id", "skillpoints", "reqs", "nDam_",
                "fDam_", "wDam_", "aDam_", "tDam_", "eDam_", "majorIds", "hprPct", "mr", "sdPct",
                "mdPct", "ls", "ms", "xpb", "lb", "ref", "thorns", "expd", "spd", "atkTier",
                "poison", "hpBonus", "spRegen", "eSteal", "hprRaw", "sdRaw", "mdRaw", "fDamPct",
                "wDamPct", "aDamPct", "tDamPct", "eDamPct", "fDefPct", "wDefPct", "aDefPct",
                "tDefPct", "eDefPct", "spPct1", "spRaw1", "spPct2", "spRaw2", "spPct3", "spRaw3",
                "spPct4", "spRaw4", "rainbowRaw", "sprint", "sprintReg", "jh", "lq", "gXp",
                "gSpd", "durability", "duration", "charges"
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
                "hprPct", "mr", "sdPct", "mdPct", "ls", "ms", "xpb", "lb", "ref", "thorns",
                "expd", "spd", "atkTier", "poison", "hpBonus", "spRegen", "eSteal", "hprRaw",
                "sdRaw", "mdRaw", "fDamPct", "wDamPct", "aDamPct", "tDamPct", "eDamPct", "fDefPct",
                "wDefPct", "aDefPct", "tDefPct", "eDefPct", "spPct1", "spRaw1", "spPct2", "spRaw2",
                "spPct3", "spRaw3", "spPct4", "spRaw4", "rSdRaw", "sprint", "sprintReg", "jh", "lq",
                "gXp", "gSpd", "eMdPct", "eMdRaw", "eSdPct", "eSdRaw", "eDamRaw", "eDamAddMin",
                "eDamAddMax", "tMdPct", "tMdRaw", "tSdPct", "tSdRaw", "tDamRaw", "tDamAddMin",
                "tDamAddMax", "wMdPct", "wMdRaw", "wSdPct", "wSdRaw", "wDamRaw", "wDamAddMin",
                "wDamAddMax", "fMdPct", "fMdRaw", "fSdPct", "fSdRaw", "fDamRaw", "fDamAddMin",
                "fDamAddMax", "aMdPct", "aMdRaw", "aSdPct", "aSdRaw", "aDamRaw", "aDamAddMin",
                "aDamAddMax", "nMdPct", "nMdRaw", "nSdPct", "nSdRaw", "nDamPct", "nDamRaw",
                "nDamAddMin", "nDamAddMax", "damPct", "damRaw", "damAddMin", "damAddMax",
                "rMdPct", "rMdRaw", "rSdPct", "rDamPct", "rDamRaw", "rDamAddMin", "rDamAddMax",
                "spPct1Final", "spPct2Final", "spPct3Final", "spPct4Final", "healPct", "kb",
                "weakenEnemy", "slowEnemy", "rDefPct");
    }
}