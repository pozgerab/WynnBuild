package com.gertoxq.quickbuild.client;

import com.gertoxq.quickbuild.Base64;
import com.gertoxq.quickbuild.*;
import com.gertoxq.quickbuild.config.ConfigScreen;
import com.gertoxq.quickbuild.config.Manager;
import com.gertoxq.quickbuild.custom.CustomItem;
import com.gertoxq.quickbuild.custom.IDS;
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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
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

import static com.gertoxq.quickbuild.custom.CustomItem.getItem;
import static com.gertoxq.quickbuild.custom.CustomItem.removeTilFormat;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class QuickBuildClient implements ClientModInitializer {
    public static final String DOMAIN = "https://hppeng-wynn.github.io/builder/#";
    public static final Integer BUILDER_VERSION = 9;
    public static final String WYNNCUSTOM_DOMAIN = "https://hppeng-wynn.github.io/custom/#";
    public final static List<String> emptyEquipmentPrefix = List.of("G", "H", "I", "J", "K", "L", "M", "N");
    public static Map<String, Integer> idMap = new HashMap<>();
    public static Map<String, Integer> tomeMap = new HashMap<>();
    public static Map<String, JsonElement> dupeMap;
    public static Map<String, JsonElement> currentDupeMap;
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static Cast cast = Cast.WARRIOR;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Set<Integer> atreeState = new HashSet<>();
    public static Map<SP, Integer> stats = SP.createStatMap();
    public static String atreeSuffix;
    public static MinecraftClient client;
    public static Clickable BUTTON;
    public static Clickable PRESETBUTTON;
    public static Clickable UI = new Clickable(() -> true);
    public static List<Integer> tomeIds = Collections.nCopies(8, null);
    public static List<IDS.ItemType> types = List.of(IDS.ItemType.Helmet, IDS.ItemType.Chestplate, IDS.ItemType.Leggings, IDS.ItemType.Boots, IDS.ItemType.Ring, IDS.ItemType.Ring, IDS.ItemType.Bracelet, IDS.ItemType.Necklace);
    public static int REFETCH_DELAY = 40;
    public static Manager configManager;
    public static int ATREE_IDLE; // How many ticks is elapsed before turning page while reading atree
    public static List<String> craftedHashes = new ArrayList<>(Collections.nCopies(9, ""));
    public static List<Integer> ids = new ArrayList<>();
    public static List<String> eqipmentNames = new ArrayList<>();
    public static List<List<Integer>> powders = new ArrayList<>();
    public static List<ItemStack> items;
    private static int wynnLevel;
    public boolean readAtree = false;
    private int failures = 0;

    public static Manager getConfigManager() {
        return configManager;
    }

    public static @Nullable List<Text> getLoreFromItemStack(@NotNull ItemStack itemStack) {
        LoreComponent loreComp = itemStack.get(DataComponentTypes.LORE);
        if (loreComp == null) return null;
        //System.out.println(loreComp.lines());
        return loreComp.lines();
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

    public static IDS.ItemType getType(ItemStack stack) {
        IDS.ItemType iType = null;
        for (IDS.ItemType type : types) {
            if (stack.getItem().toString().contains(type.name().toLowerCase())) {
                iType = type;
            }
        }
        if (iType == null) {
            iType = getCastType();
        }
        return iType;
    }

    public static IDS.ItemType getCastType() {
        return Arrays.stream(IDS.ItemType.values()).filter(type -> type.getCast() == cast).findAny().orElse(IDS.ItemType.Dagger);
    }

    public static void saveArmor() {
        ClientPlayerEntity player = client.player;
        ItemStack mainHandStack = player.getMainHandStack();
        //  Armor list BOOTS -> HELM
        items = new ArrayList<>(player.getInventory().armor);
        //  Reverse: HELM -> BOOTS
        Collections.reverse(items);

        List<ItemStack> powderable = new ArrayList<>(items); // item that can hold powders

        //  Add equipment: Slots 9 to 12
        for (int i = 9; i < 13; i++) {
            items.add(player.getInventory().main.get(i));
        }
        powderable.add(mainHandStack); // powderable weapon
        items.add(mainHandStack); // Add weapon

        powders = new ArrayList<>();

        powderable.forEach(itemStack -> {
            List<Text> lore = getLoreFromItemStack(itemStack);
            if (lore == null || lore.isEmpty()) {
                powders.add(List.of());
                return;
            }
            for (Text text : lore) {

                List<Integer> powder = Powder.getPowderFromString(removeFormat(text.getString()));
                if (powder == null || powder.isEmpty()) continue;
                powders.add(powder);
                return;
            }
            powders.add(List.of());
        });

        //  Fetches armorIds of full equipment and removes formatting, if not found -> id = -1, if crafted = -2
        craftedHashes = new ArrayList<>(Collections.nCopies(9, ""));
        ids = new ArrayList<>();
        eqipmentNames = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            String stackName = removeTilFormat(removeFormat(itemStack.getName().getString()));
            eqipmentNames.add(stackName);
            int id = idMap.getOrDefault(stackName, -1);
            if (id != -1) {
                ids.add(id);
                continue;
            }
            if (itemStack.getName().getString().startsWith(IDS.Tier.Crafted.color)) {
                craftedHashes.set(i, CustomItem.getItemHash(itemStack, types.get(i)));
                ids.add(-2);
            }
        }
    }

    public static int buildWithArgs(List<String> ids, String atreeCode) {
        if (client.player == null) return 0;
        ClientPlayerEntity player = client.player;

        //  Base URL
        StringBuilder url = new StringBuilder(DOMAIN)
                .append(BUILDER_VERSION)
                .append("_");
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        for (int i = 0; i < 9; i++) {
            try {
                int baseId = Integer.parseInt(ids.get(i));
                url.append(Base64.fromIntN(baseId, 3));
            } catch (Exception ignored) {
                String craftedCode = "CI-" + ids.get(i);      //  Combine with hash
                url.append(Base64.fromIntN(craftedCode.length(), 3)) //  Length of full hash encoded
                        .append(craftedCode);                           //  full crafted hash
            }
        }
        if (stats.values().stream().allMatch(i -> i == 0)) {
            //  If all stats are 0, possibly the data isn't fetched
            player.sendMessage(Text.literal("Open your menu while holding your weapon to fetch information for your build").formatted(Formatting.RED));
            return 0;
        }
        List.of(SP.values()).forEach(id -> url.append(Base64.fromIntN(stats.get(id), 2))); // sp
        url.append(Base64.fromIntN(wynnLevel, 2)) // wynn level
                .append(Powder.getPowderString(powders)); // powders
        tomeIds.forEach(id -> url.append(Base64.fromIntN(id, 2))); // tomes
        url.append(atreeCode); // atree

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
            getConfigManager().loadConfig();
            if (screen instanceof GenericContainerScreen containerScreen) {
                String title = containerScreen.getTitle().getString();
                List<String> titleCodes = new ArrayList<>();
                for (int i = 0; i < title.length(); i++) {
                    char ch = title.charAt(i);
                    titleCodes.add(String.format("\\u%04x", (int) ch));
                }
                //System.out.println(titleCodes.getLast());
                if (Objects.equals(titleCodes.getLast(), "\\ue003")) {
                    //     \udaff \udfdc \ue003
                    //System.out.println("charinfo");
                    var charInfoScreen = new CharacterInfoScreen(containerScreen);
                    new Task(() -> this.saveCharInfo(charInfoScreen), 2);
                    BUTTON.addTo(charInfoScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, 0, -20, Text.literal("Read"), button -> client.execute(() -> this.saveCharInfo(charInfoScreen)));
                    BUTTON.addTo(charInfoScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> this.build());
                } else if (Objects.equals(titleCodes.getLast(), "\\ue000")) {
                    //     \udaff \udfea \ue000
                    //System.out.println("atreee");
                    var atreeScreen = new AtreeScreen(containerScreen);
                    if (!readAtree) {
                        this.startAtreead(atreeScreen);
                    }
                    BUTTON.addTo(atreeScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("Read"), button -> this.startAtreead(atreeScreen));
                } else if (Objects.equals(titleCodes.getLast(), "\\ue005")) {
                    //System.out.println("tome");
                    //     \udaff \udfdb \ue005
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
                client.send(() -> {
                    client.player.sendMessage(Text.literal("NOT IMPLEMENTED IN 1.21 YET").styled(style -> style.withColor(Formatting.RED).withBold(true)));
                    //client.setScreen(new ImportAtreeScreen(client.currentScreen));
                });
                return 1;
            })));
            dispatcher.register(literal("saveatree").executes(context -> {
                client.send(() -> client.setScreen(new ImportAtreeScreen(client.currentScreen)));
                return 1;
            }));
            dispatcher.register(literal("buildcustomitem").executes(context -> {
                buildCrafted();
                return 1;
            }));
            dispatcher.register(literal("build").then(literal("buildcustomitem").executes(context -> {
                buildCrafted();
                return 1;
            })));
            dispatcher.register(literal("build").then(literal("saveditems").executes(context -> {
                client.send(() -> {
                    client.player.sendMessage(Text.literal("NOT IMPLEMENTED IN 1.21 YET").styled(style -> style.withColor(Formatting.RED).withBold(true)));
                    //client.setScreen(new SavedItemsScreen(client.currentScreen));
                });
                return 1;
            })));
            dispatcher.register(literal("build").then(literal("builder").executes(context -> {
                client.send(() -> {
                    client.player.sendMessage(Text.literal("NOT IMPLEMENTED IN 1.21 YET").styled(style -> style.withColor(Formatting.RED).withBold(true)));
                    //client.setScreen(new BuildScreen());
                });
                return 1;
            })));
        });
    }

    public CustomItem buildCraftedItem() {
        ItemStack hand = client.player.getMainHandStack();
        return getItem(hand);
    }

    private void buildCrafted() {

        CustomItem item = buildCraftedItem();
        String customHash = item == null ? "" : item.encodeCustom(true);

        if (customHash.isEmpty()) {
            client.player.sendMessage(Text.literal("Couldn't encode this item"));
            return;
        }
        StringBuilder url = new StringBuilder(WYNNCUSTOM_DOMAIN).append(customHash);
        String fullHash = "CI-" + customHash;

        client.player.sendMessage(Text.literal("\nItem is generated   ").styled(style -> style.withColor(Formatting.DARK_AQUA))
                .append(Text.literal(item.getName()).styled(style -> style.withColor(item.getTier().format)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("COPY").styled(style -> style.withColor(Formatting.GREEN)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(url.toString())))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url.toString()))
                        .withUnderline(true)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("OPEN").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.toString()))
                        .withUnderline(true)
                        .withColor(Formatting.RED)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("COPY HASH").styled(style -> style.withColor(Formatting.YELLOW)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(fullHash)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fullHash))
                        .withUnderline(true)))
                .append(Text.literal("\n\n - ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal("SAVE").styled(style -> style.withColor(Formatting.GOLD)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Clicking this will open a menu where you can save items allowing you to use it in later builds")))
                        .withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/build saveditems"))))
                .append("\n").styled(style -> style.withBold(true)));

    }

    private void startAtreead(AtreeScreen screen) {
        if (castTreeObj == null) {
            assert client.player != null;
            client.player.sendMessage(Text.literal("First read character info").styled(style -> style.withColor(Formatting.RED)));
            return;
        }
        AtomicBoolean allowClick = new AtomicBoolean(false);
        ScreenMouseEvents.allowMouseClick(screen.getScreen()).register((screen1, mouseX, mouseY, button) -> allowClick.get());
        final int pages = 9;
        var clicker = screen.getClicker();
        AtreeScreen.resetData();
        clicker.scrollAtree(-pages);            // 21
        new Task(() -> saveATree(screen), pages * 4 + 20);
        for (int i = 0; i < pages; i++) {
            new Task(() -> clicker.scrollAtree(1), i * ATREE_IDLE + pages * 4 + 24); //  28   38   34
            new Task(() -> saveATree(screen), i * ATREE_IDLE + ATREE_IDLE / 2 + pages * 4 + 24); //  30  25  28... 51
        }
        new Task(() -> {
            allowClick.set(true);
            BitVector encodedTree = AtreeCoder.encode_atree(atreeState);
            atreeSuffix = encodedTree.toB64();
            configManager.getConfig().setAtreeEncoding(atreeSuffix);
            configManager.saveConfig();
        }, pages * ATREE_IDLE + 8 + pages * 4 + 20);     // 45
        readAtree = true;
    }

    private void saveTomeInfo(@NotNull TomeScreen tomeScreen) {
        catchNotLoaded(() -> {
            tomeIds = tomeScreen.getIds();
            configManager.getConfig().setTomeIds(tomeIds);
            configManager.saveConfig();
        });
    }

    private void saveCharInfo(@NotNull CharacterInfoScreen infoScreen) {
        catchNotLoaded(() -> {
            wynnLevel = infoScreen.getLevel();
            stats = infoScreen.getStats();
            cast = infoScreen.getCast();
            configManager.getConfig().setCast(cast.name);
            configManager.saveConfig();
            currentDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
            castTreeObj = fullatree.get(cast.name).getAsJsonObject();
        });
    }

    /**
     * Wraps a method in a try block to catch errors at screen reading
     *
     * @param method
     */
    private void catchNotLoaded(Runnable method) {
        try {
            method.run();
            failures = 0;
        } catch (Exception e) {
            failures++;
            client.player.sendMessage(Text.literal("Fetching failed! Press the READ button to fetch manually")
                    .styled(style -> style.withColor(Formatting.RED)));
            if (failures < 2) {
                new Task(() -> catchNotLoaded(method), REFETCH_DELAY);
            }
            e.printStackTrace();
        }
    }

    public void saveATree(AtreeScreen screen) {
        if (castTreeObj == null) {
            assert client.player != null;
            client.player.sendMessage(Text.literal("First read the Character Info data").styled(style -> style.withColor(Formatting.RED)));
            return;
        }
        //screen.getUnlockedNames().forEach(System.out::println);
        Set<Integer> unlockedIds = screen.getUnlockedIds();
        //System.out.println("Unlocked "+ Arrays.toString(unlockedIds.toArray()));
        unlockedAbilIds.addAll(unlockedIds);
        atreeState.addAll(unlockedAbilIds);
        //System.out.println("Unlocked "+ Arrays.toString(unlockedAbilIds.toArray()));
    }

    private int build() {
        if (client.player == null) return 0;
        ClientPlayerEntity player = client.player;

        saveArmor();

        if (ids.get(8) == -1) {
            player.sendMessage(Text.literal("Hold a weapon!").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        //  Base URL
        StringBuilder url = new StringBuilder(DOMAIN)
                .append(BUILDER_VERSION)
                .append("_");
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        for (int i = 0; i < 9; i++) {
            if (ids.get(i) == -2) {
                String craftedCode = "CI-" + craftedHashes.get(i);      //  Combine with hash
                url.append(Base64.fromIntN(craftedCode.length(), 3)) //  Length of full hash encoded
                        .append(craftedCode);                           //  full crafted hash
                continue;
            }
            if (i == 8) {
                url.append(Base64.fromIntN(ids.get(8), 3)); // Add main hand
            } else url.append(ids.get(i) == -1 ? "2S" + emptyEquipmentPrefix.get(i) : Base64.fromIntN(ids.get(i), 3));
        }
        if (stats.values().stream().allMatch(i -> i == 0)) {
            //  If all stats are 0, possibly the data isn't fetched
            player.sendMessage(Text.literal("Open your menu while holding your weapon to fetch information for your build").formatted(Formatting.RED));
            return 0;
        }
        List.of(SP.values()).forEach(id -> url.append(Base64.fromIntN(stats.get(id), 2))); // sp
        url.append(Base64.fromIntN(wynnLevel, 2)) // wynn level
                .append(Powder.getPowderString(powders)); // powders
        tomeIds.forEach(id -> url.append(Base64.fromIntN(id, 2))); // tomes
        url.append(atreeSuffix); // atree

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
