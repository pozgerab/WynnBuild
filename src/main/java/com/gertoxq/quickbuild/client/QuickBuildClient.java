package com.gertoxq.quickbuild.client;

import com.gertoxq.quickbuild.Base64;
import com.gertoxq.quickbuild.*;
import com.gertoxq.quickbuild.config.ConfigScreen;
import com.gertoxq.quickbuild.config.Manager;
import com.gertoxq.quickbuild.screens.*;
import com.gertoxq.quickbuild.util.Task;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class QuickBuildClient implements ClientModInitializer {
    public static Map<String, Integer> idMap = new HashMap<>();
    public static Map<String, Integer> tomeMap = new HashMap<>();
    public static Map<String, JsonElement> dupeMap;
    public static Map<String, JsonElement> currentDupeMap;
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static Cast cast;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Set<Integer> atreeState = new HashSet<>();
    public static Map<IDS, Integer> stats = IDS.createStatMap();
    public static String atreeSuffix;
    public static MinecraftClient client;
    public static Clickable BUTTON;
    public static Clickable PRESETBUTTON;
    public static Clickable UI = new Clickable(() -> true);
    public static List<Integer> tomeIds = Collections.nCopies(8, null);
    private static Manager configManager;
    public final int ATREE_IDLE = 3; // How many ticks is elapsed before turning page while reading atree
    public final Integer WYNNBUILDER_VERSION = 9;
    public boolean readAtree = false;

    public static Manager getConfigManager() {
        return configManager;
    }

    public static @Nullable List<Text> getLoreFromItemStack(@NotNull ItemStack itemStack) {
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

    public static String removeFormat(@NotNull String str) {
        return str.replaceAll("§[0-9a-fA-Fklmnor]", "");
    }

    public static String removeNum(String str) {
        var rep = List.of(" 1", " 2", " 3", " III", " II", " I");
        AtomicReference<String> news = new AtomicReference<>(str);
        rep.forEach(s -> news.set(news.get().replace(s, "")));
        return news.get();
    }

    @Override
    public void onInitializeClient() {

        client = MinecraftClient.getInstance();
        Task.init();

        InputStream inputStream = QuickBuild.class.getResourceAsStream("/" + "idMap.json");
        InputStream dupeStream = QuickBuild.class.getResourceAsStream("/" + "dupes.json");
        InputStream atreeStream = QuickBuild.class.getResourceAsStream("/" + "atree.json");
        InputStream tomeStream = QuickBuild.class.getResourceAsStream("/" + "tomeIdMap.json");

        try {
            assert inputStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> idMap.put(s, jsonElement.getAsInt()));
            assert dupeStream != null;
            dupeMap = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dupeStream, StandardCharsets.UTF_8))).asMap();
            assert atreeStream != null;
            fullatree = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(atreeStream, StandardCharsets.UTF_8))).asMap();
            assert tomeStream != null;
            ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(tomeStream, StandardCharsets.UTF_8))).asMap().forEach((s, jsonElement) -> tomeMap.put(s, jsonElement.getAsInt()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        configManager = new Manager();
        configManager.loadConfig();
        if (!configManager.getConfig().getAtreeEncoding().isEmpty()) {
            readAtree = true;
        }
        BUTTON = new Clickable(() -> configManager.getConfig().isShowButtons());
        PRESETBUTTON = new Clickable(() -> configManager.getConfig().isShowTreeLoader());

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof GenericContainerScreen containerScreen) {
                String title = containerScreen.getTitle().getString();
                if (title.equals("Character Info")) {
                    var charInfoScreen = new CharacterInfoScreen(containerScreen);
                    new Task(() -> this.saveCharInfo(charInfoScreen), 2);
                    BUTTON.addTo(charInfoScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, 0, -20, Text.literal("Read"), button -> client.execute(() -> this.saveCharInfo(charInfoScreen)));
                    BUTTON.addTo(charInfoScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> this.build());
                } else if (title.contains(" Abilities")) {
                    var atreeScreen = new AtreeScreen(containerScreen);
                    if (!readAtree) {
                        this.startAtreead(atreeScreen);
                    }
                    BUTTON.addTo(atreeScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("Read"), button -> this.startAtreead(atreeScreen));
                } else if (title.equals("Mastery Tomes")) {
                    var tomeScreen = new TomeScreen(containerScreen);
                    new Task(() -> saveTomeInfo(tomeScreen), 2);
                    BUTTON.addTo(tomeScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("Read"), button -> this.saveTomeInfo(tomeScreen));
                }
            } else if (screen instanceof InventoryScreen screen1) {
                BUTTON.addTo(screen1, AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> this.build());
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("build")
                    .executes(context -> this.build()).then(literal("help").executes(context -> {
                        var p = context.getSource().getClient().player;
                        if (p == null) return 0;
                        p.sendMessage(Text.literal("Welcome to QuickBuild").styled(style -> style.withColor(Formatting.GOLD)).append(
                                Text.literal("""

                                        This is a mod for quickly exporting your build with the use of wynnbuilder. As you run the '/build' command or click the build button on the right left side of your screen, this mod will generate you a wynnbuilder link that you can copy or share.
                                        You can configure the mod with /build config""")
                        ).styled(style -> style.withColor(Formatting.GOLD)));
                        return 1;
                    })));
            dispatcher.register(literal("build").then(literal("config").executes(context -> {
                client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen)));
                return 1;
            })));
            dispatcher.register(literal("build").then(literal("saveatree").executes(context -> {
                client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                return 1;
            })));
            dispatcher.register(literal("saveatree").executes(context -> {
                client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                return 1;
            }));
        });
    }

    private void startAtreead(AtreeScreen screen) {
        if (castTreeObj == null) {
            assert client.player != null;
            client.player.sendMessage(Text.literal("First read character info").styled(style -> style.withColor(Formatting.RED)));
            return;
        }
        AtomicBoolean allowClick = new AtomicBoolean(false);
        ScreenMouseEvents.allowMouseClick(screen.getScreen()).register((screen1, mouseX, mouseY, button) -> allowClick.get());
        final int pages = 8;
        var clicker = screen.getClicker();
        clicker.scrollAtree(-pages);
        for (int i = 0; i < pages; i++) {
            new Task(() -> clicker.scrollAtree(1), i * ATREE_IDLE + pages * 2 + 4);
            new Task(() -> saveATree(screen), i * ATREE_IDLE + ATREE_IDLE - 1 + pages * 2 + 4);
        }
        new Task(() -> {
            allowClick.set(true);
            BitVector encodedTree = AtreeCoder.encode_atree(atreeState);
            atreeSuffix = encodedTree.toB64();
            configManager.getConfig().setAtreeEncoding(atreeSuffix);
            configManager.saveConfig();
        }, pages * ATREE_IDLE + 5 + pages * 2);
        readAtree = true;
    }

    private void saveTomeInfo(@NotNull TomeScreen tomeScreen) {
        tomeIds = tomeScreen.getIds();
        configManager.getConfig().setTomeIds(tomeIds);
        configManager.saveConfig();
    }

    private void saveCharInfo(@NotNull CharacterInfoScreen infoScreen) {
        stats = infoScreen.getStats();
        cast = infoScreen.getCast();
        configManager.getConfig().setCast(cast.name);
        configManager.saveConfig();
        currentDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
        castTreeObj = fullatree.get(cast.name).getAsJsonObject();
    }

    public void saveATree(AtreeScreen screen) {
        if (castTreeObj == null) {
            assert client.player != null;
            client.player.sendMessage(Text.literal("First read the Character Info data").styled(style -> style.withColor(Formatting.RED)));
            return;
        }
        //unlockedNames.forEach(System.out::println);
        var unlockedIds = screen.getUpgradedUnlockedIds();
        //System.out.println("Unlocked "+ Arrays.toString(unlockedIds.toArray()));
        unlockedAbilIds.addAll(unlockedIds);
        atreeState.addAll(unlockedAbilIds);
        //System.out.println("Unlocked "+ Arrays.toString(unlockedAbilIds.toArray()));
    }

    private int build() {
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
        }

        //  Fetches ids of full equipment and removes formatting, if not found -> id = -1
        List<Integer> ids = items.stream().map(itemStack -> idMap.getOrDefault(itemStack.getName().getString().replaceAll("§[0-9a-fA-Fklmnor]", ""), -1)).toList();
        if (ids.get(8) == -1) {
            player.sendMessage(Text.literal("Hold a weapon!").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        //  Suffixes of empty equipment slots
        final var emptyEquipmentPrefix = List.of("G", "H", "I", "J", "K", "L", "M", "N");

        //  Base URL
        StringBuilder url = new StringBuilder("https://hppeng-wynn.github.io/builder/#")
                .append(WYNNBUILDER_VERSION)
                .append("_");
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        for (int i = 0; i < 8; i++) {
            url.append(ids.get(i) == -1 ? "2S" + emptyEquipmentPrefix.get(i) : Base64.fromIntN(ids.get(i), 3));
        }
        url.append(Base64.fromIntN(ids.get(8), 3)); // Add main hand
        if (stats.values().stream().allMatch(i -> i == 0)) {
            //  If all stats are 0, possibly the data isn't fetched
            player.sendMessage(Text.literal("Open your menu while holding your weapon to fetch information for your build").formatted(Formatting.RED));
            return 0;
        }
        List.of(IDS.values()).forEach(id -> url.append(Base64.fromIntN(stats.get(id), 2))); // sp
        url.append(Base64.fromIntN(wynnLevel, 2)).append("00000"); // wynn level + powders ig, not implemented YET
        tomeIds.forEach(id -> url.append(Base64.fromIntN(id, 2))); // tomes
        url.append(atreeSuffix);

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
}
