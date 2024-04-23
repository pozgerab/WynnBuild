package com.gertoxq.quickbuild.client;

import com.gertoxq.quickbuild.Base64;
import com.gertoxq.quickbuild.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class QuickBuildClient implements ClientModInitializer {
    public static Map<String, JsonElement> idMap;
    public static Map<String, JsonElement> dupeMap;
    public static Map<String, JsonElement> currentDupeMap;
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static String cast;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Set<Integer> atreeState = new HashSet<>();
    Map<IDS, Integer> stats = IDS.createStatMap();
    public static String atreeSuffix;
    @Override
    public void onInitializeClient() {

        InputStream inputStream = QuickBuild.class.getResourceAsStream("/" + "idMap.json");
        InputStream dupeStream = QuickBuild.class.getResourceAsStream("/" + "dupes.json");

        try {
            idMap = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))).asMap();
            dupeMap = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dupeStream, StandardCharsets.UTF_8))).asMap();
        } catch (Exception ignored) {

        }

        InputStream atreeStream = QuickBuild.class.getResourceAsStream("/" + "atree.json");

        try {
            fullatree = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(atreeStream, StandardCharsets.UTF_8))).asMap();
        } catch (Exception ignored) {

        }

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof GenericContainerScreen containerScreen) {
                String title = containerScreen.getTitle().getString();
                if (title.equals("Character Info")) {
                    Screens.getButtons(containerScreen).add(new ReadBtn(width/4-50, height/2-10, 100, 20, Text.literal("Read"), button -> {
                        MinecraftClient.getInstance().execute(() -> this.saveCharInfo(containerScreen));
                    }, Text.literal("Read Character Info data")));
                    Screens.getButtons(containerScreen).add(new ReadBtn(width-100, height-20, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> this.build(), Text.literal("Generate your build url")));
                }
                else if (title.contains(" Abilities")) {
                    Screens.getButtons(containerScreen).add(new ReadBtn(width/4-50, height/2-10, 100, 20, Text.literal("Read"), button -> {
                        this.saveATree(containerScreen);
                    }, Text.literal("Read current ability tree page data")));
                }
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("build")
                .executes(context -> this.build()).then(ClientCommandManager.literal("help").executes(context -> {
                    var p = context.getSource().getClient().player;
                    if (p == null) return 0;
                    p.sendMessage(Text.literal("Welcome to QuickBuild").styled(style -> style.withBold(true).withColor(Formatting.GOLD)).append(
                            Text.literal("\n\nThis is a mod for quickly exporting your build with the use of wynnbuilder." +
                                    " As you run the '/build' command or click the build button on the right left side of your screen," +
                                    " this mod will generate you a wynnbuilder link that you can copy or share")
                                    ).styled(style -> style.withColor(Formatting.GOLD)));
                    return 1;
                }))));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("resetatreecache")
                .executes(context -> {
                    unlockedAbilIds.clear();
                    return 1;
                })));
    }

    private void saveCharInfo(GenericContainerScreen handledScreen) {
        var slots = handledScreen.getScreenHandler().slots;
        IDS.getStatContainerMap().forEach((slot, id) -> {
            if (slots.get(slot).getStack() == null) return;
            var idVal = getLoreFromItemStack(slots.get(slot).getStack());
            if (idVal == null) return;
            System.out.println(Arrays.toString(idVal.toArray()));
            int point = 0;
            try {                                       //Bc lore is longer on intel
                point = Integer.parseInt(removeFormat(idVal.get(id == IDS.INTELLIGENCE ? 4 : 3).getSiblings().get(1).getString().replace(" points", "")));
            } catch (Exception ignored) {
                System.out.println("ERR while parsing stat point");
            }
            stats.put(id, point);
        });
        for (Slot slot: slots) {
            if (slot.getIndex() == 7) {
                var lore = getLoreFromItemStack(slot.getStack());
                if (lore == null) continue;
                System.out.println(Arrays.toString(lore.toArray()));
                String className = lore.get(4).getSiblings().get(1).getString();
                System.out.println("Class: "+className);
                cast = className;
                currentDupeMap = dupeMap.get(cast).getAsJsonObject().asMap();
                castTreeObj = fullatree.get(cast).getAsJsonObject();
            }
            if (slot.getIndex() == 26) {
                break;
            }
        }
    }

    private void saveATree(GenericContainerScreen handledScreen) {
        if (castTreeObj == null) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("First read the Character Info data").styled(style -> style.withColor(Formatting.RED)));
            return;
        }
        var slots = handledScreen.getScreenHandler().slots;
        AtomicBoolean stop = new AtomicBoolean(false);
        var unlocked = slots.stream().filter(slot -> {
            if (stop.get()) return false;
            var lore = getLoreFromItemStack(slot.getStack());
            if (lore == null) return false;
            if (slot.getIndex() == 53) {
                stop.set(true);
            }
            return removeFormat(lore.get(lore.size() - 1).getString()).equals("You already unlocked this ability");
        }).toList();
        var unlockedNames = unlocked.stream().map(slot -> removeNum(removeFormat(slot.getStack().getName().getString()))).toList();
        var names = slots.stream().map(slot -> {
            var name = removeFormat(slot.getStack().getName().getString());
            name = name.replace("Unlock ", "");
            name = name.replace(" ability", "");
            name = removeNum(name);
            return name;
        }).toList();
        unlockedNames.forEach(System.out::println);
        var unlockedIds = unlockedNames.stream().map(name -> {
            try {
                return Integer.parseInt(castTreeObj.entrySet().stream().filter(entry -> Objects.equals(name, removeNum(entry.getValue().getAsJsonObject().get("display_name").getAsString()))).findAny().orElse(null).getKey());
            } catch (NullPointerException e) {
                return 0;
            }
        }).collect(Collectors.toSet());
        var ids = names.stream().map(name -> {
            try {
                return Integer.parseInt(castTreeObj.entrySet().stream().filter(entry -> Objects.equals(name, removeNum(entry.getValue().getAsJsonObject().get("display_name").getAsString()))).findAny().orElse(null).getKey());
            } catch (NullPointerException e) {
                //System.out.println("ITEM NOT FOUND");
                return 0;
            }
        }).collect(Collectors.toSet());
        //System.out.println("Unlocked "+ Arrays.toString(unlockedIds.toArray()));
        for (int i = 0; i < 3; i++) { // So tier 2 abilities have a chance to upgrade to tier 3
            Map<Integer, Integer> toReplace = new HashMap<>();
            unlockedIds.forEach(id -> {
                if (currentDupeMap.containsKey(id.toString()) && ids.contains(currentDupeMap.get(id.toString()).getAsJsonArray().get(0).getAsInt())) {
                    toReplace.put(id, currentDupeMap.get(id.toString()).getAsJsonArray().get(1).getAsInt());
                }
            });
            toReplace.forEach((integer, integer2) -> {
                unlockedIds.remove(integer);
                unlockedIds.add(integer2);
            });
        }
        unlockedAbilIds.addAll(unlockedIds);
        atreeState.addAll(unlockedAbilIds);
        //System.out.println("Unlocked "+ Arrays.toString(unlockedAbilIds.toArray()));
    }
    private int build() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return 0;
        ClientPlayerEntity player = client.player;
        //  WYNN == EXP level
        int wynnLevel = client.player.experienceLevel;

        ItemStack mainHandStack = player.getMainHandStack();
        //  Armor list BOOTS -> HELM
        List<ItemStack> items = new ArrayList<>(player.getInventory().armor);
        //  Reverse: HELM -> BOOTS
        Collections.reverse(items);
        //  Add equipment: Slots 9 to 12
        for (int i = 9; i < 13; i++) {
            items.add(player.getInventory().main.get(i));
        }
        items.add(mainHandStack); // Add weapon

        if (mainHandStack.isEmpty()) { // Can't build without weapon
            player.sendMessage(Text.literal("Hold your weapon!").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        };

        //  Fetches ids of full equipment and removes formatting, if not found -> id = -1
        List<Integer> ids = items.stream().map(itemStack -> idMap.containsKey(itemStack.getName().getString().replaceAll("§[0-9a-fA-Fklmnor]", "")) ? idMap.get(itemStack.getName().getString().replaceAll("§[0-9a-fA-Fklmnor]", "")).getAsInt() : -1).toList();
        if (ids.get(8) == -1) {
            player.sendMessage(Text.literal("Hold a weapon!").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        try {
            BitVector encodedTree = EncodeATree.encode_atree(atreeState);
            atreeSuffix = encodedTree.toB64();
            //System.out.println("ENCODED  " + atreeSuffix);
        } catch (Exception exception) {
            player.sendMessage(Text.literal("Atree not configured correctly, try opening and reading your Ability Tree").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        //  Suffixes of empty equipment slots
        var emptyEquipment = new String[]{"G","H","I","J","K","L","M","N"};

        //  Base URL
        StringBuilder url = new StringBuilder("https://wynnbuilder.github.io/builder/#8_");
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        for (int i = 0; i < 8; i++) {
            url.append(ids.get(i) == -1 ? "2S"+emptyEquipment[i] : Base64.fromInt3(ids.get(i)));
        }
        url.append(Base64.fromInt3(ids.get(8))); // Add main hand
        if (stats.values().stream().allMatch(i -> i == 0)) {
            //  If all stats are 0, possibly the data isn't fetched
            player.sendMessage(Text.literal("Open your menu while holding your weapon to fetch information for your build").formatted(Formatting.RED));
            return 0;
        }
        //  Add all stats to URL
        stats.values().forEach(integer -> url.append(Base64.fromInt2(integer)));
        //  IDK and then atree
        url.append(Base64.fromInt2(wynnLevel)).append("000000z0z0+0+0+0+0-").append(atreeSuffix);

        //  Send copyable build to client
        player.sendMessage(Text.literal("\n    Your build is generated   ").styled(style -> style.withColor(Formatting.GOLD))
                .append(Text.literal("COPY").styled(style -> style.withColor(Formatting.GREEN)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(url.toString())))
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url.toString()))
                .withUnderline(true)))
                .append("  ")
                .append(Text.literal("OPEN").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.toString()))
                .withUnderline(true)
                .withColor(Formatting.RED)))
                .append("\n").styled(style -> style.withBold(true)));

        return 1;

    }

    public static List<Text> getLoreFromItemStack(ItemStack itemStack) {
        List<Text> loreList = new ArrayList<>();
        if (!itemStack.hasNbt()) return null;
        NbtCompound tag = itemStack.getNbt();
        if (tag == null || !tag.contains("display", NbtElement.COMPOUND_TYPE)) return null;
        NbtCompound display = tag.getCompound("display");
        if (!display.contains("Lore", NbtElement.LIST_TYPE)) return null;
        NbtList lore = display.getList("Lore", NbtElement.STRING_TYPE);
        for (int i = 0; i < lore.size(); i++) {
            Text textComponent = Text.Serializer.fromJson(lore.getString(i));
            if (textComponent != null) {
                loreList.add(textComponent);
            }
        }
        return loreList;
    }

    private String removeFormat(String str) {
        return str.replaceAll("§[0-9a-fA-Fklmnor]", "");
    }
    private String replaceRoman(String str) {
        AtomicReference<String> news = new AtomicReference<>(str);
        HashMap<String, Integer> replacement = new HashMap<>();
        replacement.put(" I", 1);
        replacement.put(" II", 2);
        replacement.put(" III", 3);
        replacement.put(" IV", 4);
        replacement.forEach((s, integer) -> {
            if (news.get().endsWith(s)) {
                 news.set(news.get().replace(s, " "+ integer));
            }
        });
        return news.get();
    }
    private String removeNum(String str) {
        var rep = List.of(" 1"," 2"," 3"," III", " II", " I");
        AtomicReference<String> news = new AtomicReference<>(str);
        rep.forEach(s -> news.set(news.get().replace(s, "")));
        return news.get();
    }
}
