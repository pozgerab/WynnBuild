package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.Base64;
import com.gertoxq.wynnbuild.*;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.custom.AllIDs;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.custom.ID;
import com.gertoxq.wynnbuild.screens.*;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.gertoxq.wynnbuild.custom.CustomItem.getItem;
import static com.gertoxq.wynnbuild.custom.CustomItem.removeTilFormat;

public class WynnBuildClient implements ClientModInitializer {
    public static final String DOMAIN = "https://hppeng-wynn.github.io/builder/#";
    public static final Integer BUILDER_VERSION = 9;
    public static final String WYNNCUSTOM_DOMAIN = "https://hppeng-wynn.github.io/custom/#";
    public final static List<String> emptyEquipmentPrefix = List.of("G", "H", "I", "J", "K", "L", "M", "N");
    public static Map<String, Integer> tomeMap = new HashMap<>();
    public static Map<String, JsonElement> dupeMap;
    public static Map<String, JsonElement> currentDupeMap;
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static Cast cast = Cast.Warrior;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Set<Integer> atreeState = new HashSet<>();
    public static Map<SP, Integer> stats = SP.createStatMap();
    public static String atreeSuffix;
    public static MinecraftClient client;
    public static Clickable BUTTON;
    public static Clickable PRESETBUTTON;
    public static Clickable UI = new Clickable(() -> true);
    public static List<Integer> tomeIds = Collections.nCopies(8, null);
    public static List<ID.ItemType> types = List.of(ID.ItemType.Helmet, ID.ItemType.Chestplate, ID.ItemType.Leggings, ID.ItemType.Boots, ID.ItemType.Ring, ID.ItemType.Ring, ID.ItemType.Bracelet, ID.ItemType.Necklace);
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
            List<Text> lore = Utils.getLore(itemStack);
            if (lore == null || lore.isEmpty()) {
                powders.add(List.of());
                return;
            }
            for (Text text : lore) {

                List<Integer> powder = Powder.getPowderFromString(Utils.removeFormat(text.getString()));
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
            String stackName = removeTilFormat(Utils.removeFormat(itemStack.getName().getString()));
            eqipmentNames.add(stackName);
            int id = WynnData.getIdMap().getOrDefault(stackName, -1);
            if (id != -1) {
                ids.add(id);
                continue;
            }
            if (itemStack.getName().getString().startsWith(ID.Tier.Crafted.color)) {
                craftedHashes.set(i, CustomItem.getItemHash(itemStack, i != 8 ? types.get(i) : cast.weapon));
                ids.add(-2);
                continue;
            }
            ids.add(id);
        }
    }

    public static void buildCraftedMainHand() {
        buildCraftedItem(client.player.getMainHandStack());
    }

    public static void buildCraftedItem(ItemStack itemStack) {

        CustomItem item = getItem(itemStack);
        String customHash = item == null ? "" : item.encodeCustom(true);

        if (customHash.isEmpty()) {
            client.player.sendMessage(Text.literal("Couldn't encode this item"));
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
            return;
        }
        String url = WYNNCUSTOM_DOMAIN + customHash;
        String fullHash = "CI-" + customHash;

        client.player.sendMessage(Utils.getItemPrintTemplate(item, fullHash, url));

    }

    /**
     * Generates a player build URL based on provided item IDs, ability tree code, and other parameters.
     * The generated URL can be shared or utilized for further processing.
     *
     * @param ids       List of item IDs (base IDs as string, custom crafted hash or "-1" for empty equipment).
     * @param atreeCode A string representing the ability tree code to include in the build.
     * @param emptySafe A boolean indicating whether to include skill point values in the URL or use defaults.
     */
    public static void buildWithArgs(List<String> ids, String atreeCode, boolean emptySafe) {
        if (client.player == null) return;
        ClientPlayerEntity player = client.player;

        //  Base URL
        StringBuilder url = new StringBuilder(DOMAIN)
                .append(BUILDER_VERSION)
                .append("_");
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        var tempPowders = new ArrayList<>(powders);
        for (int i = 0; i < 9; i++) {
            try {
                if (Objects.equals(ids.get(i), "-1")) {     //  IF ID == -1 -> EMPTY
                    url.append("2S").append(emptyEquipmentPrefix.get(i));
                    tempPowders.set(i, List.of());
                } else {                                        //  NOT EMPTY -> PARSE TO INT, IF FAILS IT IS CUSTOM
                    int baseId = Integer.parseInt(ids.get(i));
                    url.append(Base64.fromIntN(baseId, 3));
                }
            } catch (Exception ignored) {                       //  FAILED INT PARSE -> CUSTOM
                String craftedCode = "CI-" + ids.get(i);      //  Combine with hash
                url.append(Base64.fromIntN(craftedCode.length(), 3)) //  Length of full hash encoded
                        .append(craftedCode);                           //  full crafted hash
            }
        }
        if (stats.values().stream().allMatch(i -> i == 0)) {
            //  If all stats are 0, possibly the data isn't fetched
            player.sendMessage(Text.literal("Open your character menu while holding your weapon to fetch information for your build").formatted(Formatting.RED));
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
            return;
        }
        List.of(SP.values()).forEach(id -> url.append(Base64.fromIntN(emptySafe ? stats.get(id) : 0, 2))); // sp
        url.append(Base64.fromIntN(wynnLevel, 2)) // wynn level
                .append(Powder.getPowderString(tempPowders)); // powders
        for (int i = 0; i < 8; i++) {
            Integer tomeId = tomeIds.get(i);
            if (tomeId == null) {
                url.append(Base64.fromIntN(TomeScreen.EMPTY_IDS.get(i), 2));
            } else {
                url.append(Base64.fromIntN(tomeId, 2));
            }
        } // tomes
        url.append(atreeCode); // atree

        //  Send copyable build to client
        player.sendMessage(Utils.getBuildTemplate(url.toString()));
        if (!emptySafe)
            client.player.sendMessage(Text.literal("Note that not using the EMPTY SAFE option generates urls without skill points").styled(style -> style.withColor(Formatting.RED)));
    }

    public static int build() {
        if (client.player == null) return 0;
        ClientPlayerEntity player = client.player;

        saveArmor();

        if (ids.get(8) == -1) {
            player.sendMessage(Text.literal("Hold a weapon!").styled(style -> style.withColor(Formatting.RED)));
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
            return 0;
        }
        // Adds equipment or empty value except for weapon (Each has to be 3 chars)
        List<String> finalIds = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int precision = WynnBuildClient.getConfigManager().getConfig().getPrecision();
            int id = 0;
            String customStr = "";
            boolean custom = false;
            if (ids.get(i) == -1) {
                finalIds.add("-1");        //   EMPTY -> ADD EMPTY
                continue;
            }
            CustomItem item;
            if (ids.get(i) == -2) {
                item = CustomItem.getCustomFromHash(craftedHashes.get(i));
                customStr = item.encodeCustom(true);
                custom = true;
            } else {
                item = getItem(items.get(i), i < 8 ? types.get(i) : null);
            }
            if (precision == 0) { //    OFF
                id = ids.get(i);
            } else if (precision == 1) {    //  NEVER
                if (item.getBaseItemId() != null) {
                    id = item.getBaseItemId();
                } else {
                    customStr = item.encodeCustom(true);
                    custom = true;
                }
            } else if (precision == 2) {    //  ON
                if (custom) {
                    customStr = item.encodeCustom(true);
                } else {
                    id = ids.get(i);
                }
            } else {    //  FORCE
                customStr = item.encodeCustom(true);
                custom = true;
            }

            if (custom) {
                finalIds.add(customStr);
            } else {
                finalIds.add(String.valueOf(id));
            }
        }
        buildWithArgs(finalIds, atreeSuffix, true);
        return 1;
    }

    @Override
    public void onInitializeClient() {

        client = MinecraftClient.getInstance();
        Task.init();

        AllIDs.load();
        WynnData.load();

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
                    BUTTON.addTo(charInfoScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> build());
                } else if (Objects.equals(titleCodes.getLast(), "\\ue000")) {
                    //     \udaff \udfea \ue000
                    //System.out.println("atreee");
                    try {
                        var atreeScreen = new AtreeScreen(containerScreen);
                        if (!readAtree) {
                            this.startAtreead(atreeScreen);
                        }
                        BUTTON.addTo(atreeScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("Read"), button -> this.startAtreead(atreeScreen));
                    } catch (ExceptionInInitializerError error) {
                        error.printStackTrace();
                    }
                } else if (Objects.equals(titleCodes.getLast(), "\\ue005")) {
                    //System.out.println("tome");
                    //     \udaff \udfdb \ue005
                    var tomeScreen = new TomeScreen(containerScreen);
                    new Task(() -> saveTomeInfo(tomeScreen), 2);
                    BUTTON.addTo(tomeScreen.getScreen(), AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("Read"), button -> this.saveTomeInfo(tomeScreen));
                }
            } else if (screen instanceof InventoryScreen screen1) {
                BUTTON.addTo(screen1, AXISPOS.END, AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> build());
            }
        });

        CommandReg.init(client);
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
}
