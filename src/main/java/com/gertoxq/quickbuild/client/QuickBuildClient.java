package com.gertoxq.quickbuild.client;

import com.gertoxq.quickbuild.Base64;
import com.gertoxq.quickbuild.*;
import com.gertoxq.quickbuild.config.ConfigScreen;
import com.gertoxq.quickbuild.config.Manager;
import com.gertoxq.quickbuild.screens.AtreeScreen;
import com.gertoxq.quickbuild.screens.CharacterInfoScreen;
import com.gertoxq.quickbuild.screens.ImportAtreeScreen;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class QuickBuildClient implements ClientModInitializer {
    public static Map<String, JsonElement> idMap;
    public static Map<String, JsonElement> dupeMap;
    public static Map<String, JsonElement> currentDupeMap;
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static Cast cast;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Set<Integer> atreeState = new HashSet<>();
    Map<IDS, Integer> stats = IDS.createStatMap();
    public static String atreeSuffix;
    public static MinecraftClient client;
    public final int ATREE_IDLE = 3;
    public boolean readAtree = false;
    public final Integer WYNNBUILDER_VERSION = 8;
    private static Manager configManager;

    @Override
    public void onInitializeClient() {

        client = MinecraftClient.getInstance();
        Task.init();

        InputStream inputStream = QuickBuild.class.getResourceAsStream("/" + "idMap.json");
        InputStream dupeStream = QuickBuild.class.getResourceAsStream("/" + "dupes.json");
        InputStream atreeStream = QuickBuild.class.getResourceAsStream("/" + "atree.json");

        try {
            assert inputStream != null;
            idMap = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))).asMap();
            assert dupeStream != null;
            dupeMap = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(dupeStream, StandardCharsets.UTF_8))).asMap();
            assert atreeStream != null;
            fullatree = ((JsonObject) JsonParser.parseReader(
                    new InputStreamReader(atreeStream, StandardCharsets.UTF_8))).asMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        configManager = new Manager();
        configManager.loadConfig();
        if (!configManager.getConfig().getAtreeEncoding().isEmpty()) {
            readAtree = true;
        }

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof GenericContainerScreen containerScreen) {
                String title = containerScreen.getTitle().getString();
                if (title.equals("Character Info")) {
                    var charInfoScreen = new CharacterInfoScreen(containerScreen);
                    new Task(() -> this.saveCharInfo(charInfoScreen), 2);
                    ClickButton.addToRightBottom(charInfoScreen.getScreen(), 100, 20, 0, -20, Text.literal("Read"), button -> client.execute(() -> this.saveCharInfo(charInfoScreen)));
                    ClickButton.addToRightBottom(charInfoScreen.getScreen(), 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> this.build());
                }
                else if (title.contains(" Abilities")) {
                    var atreeScreen = new AtreeScreen(containerScreen);
                    if (!readAtree) {
                        this.startAtreead(atreeScreen);
                    }
                    ClickButton.addToRightBottom(atreeScreen.getScreen(), 100, 20, Text.literal("Read"), button -> this.startAtreead(atreeScreen));
                    atreeScreen.renderSaveButtons();
                }
            } else if (screen instanceof InventoryScreen screen1) {
                ClickButton.addToRightBottom(screen1, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> this.build());
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
            new Task(() -> clicker.scrollAtree(1), i*ATREE_IDLE+pages*2+4);
            new Task(() -> saveATree(screen), i*ATREE_IDLE+ATREE_IDLE-1+pages*2+4);
        }
        new Task(() -> {
            allowClick.set(true);
            BitVector encodedTree = AtreeCoder.encode_atree(atreeState);
            atreeSuffix = encodedTree.toB64();
            configManager.getConfig().setAtreeEncoding(atreeSuffix);
            configManager.saveConfig();
        }, pages*ATREE_IDLE+5+pages*2);
        readAtree = true;
    }

    private void saveCharInfo(CharacterInfoScreen infoScreen) {
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
        List<Integer> ids = items.stream().map(itemStack -> idMap.containsKey(itemStack.getName().getString().replaceAll("§[0-9a-fA-Fklmnor]", "")) ? idMap.get(itemStack.getName().getString().replaceAll("§[0-9a-fA-Fklmnor]", "")).getAsInt() : -1).toList();
        if (ids.get(8) == -1) {
            player.sendMessage(Text.literal("Hold a weapon!").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        try {
            atreeSuffix = configManager.getConfig().getAtreeEncoding();
            //System.out.println("ENCODED  " + atreeSuffix);
        } catch (Exception exception) {
            player.sendMessage(Text.literal("Atree not configured correctly, try opening and reading your Ability Tree").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        //  Suffixes of empty equipment slots
        final var emptyEquipmentPrefix = new String[]{"G","H","I","J","K","L","M","N"};

        //  Base URL
        StringBuilder url = new StringBuilder("https://wynnbuilder.github.io/builder/#")
                .append(WYNNBUILDER_VERSION)
                .append("_");
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        for (int i = 0; i < 8; i++) {
            url.append(ids.get(i) == -1 ? "2S"+emptyEquipmentPrefix[i] : Base64.fromInt3(ids.get(i)));
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

    public static Manager getConfigManager() {
        return configManager;
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

    public static String removeFormat(String str) {
        return str.replaceAll("§[0-9a-fA-Fklmnor]", "");
    }

    public static String removeNum(String str) {
        var rep = List.of(" 1"," 2"," 3"," III", " II", " I");
        AtomicReference<String> news = new AtomicReference<>(str);
        rep.forEach(s -> news.set(news.get().replace(s, "")));
        return news.get();
    }
}
