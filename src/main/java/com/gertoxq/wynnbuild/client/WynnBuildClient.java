package com.gertoxq.wynnbuild.client;

import com.gertoxq.wynnbuild.Cast;
import com.gertoxq.wynnbuild.SP;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.build.Gear;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.custom.AllIDs;
import com.gertoxq.wynnbuild.custom.CustomItem;
import com.gertoxq.wynnbuild.custom.ID;
import com.gertoxq.wynnbuild.screens.Clickable;
import com.gertoxq.wynnbuild.screens.ScreenManager;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.util.Task;
import com.gertoxq.wynnbuild.util.Utils;
import com.gertoxq.wynnbuild.util.WynnData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static com.gertoxq.wynnbuild.custom.CustomItem.getItem;

public class WynnBuildClient implements ClientModInitializer {
    public static final String DOMAIN = "https://wynnbuilder.github.io/builder/#";
    public static final Integer BUILDER_VERSION = 9;
    public static final String WYNNCUSTOM_DOMAIN = "https://wynnbuilder.github.io/custom/#";
    public static Map<String, Integer> tomeMap = new HashMap<>();
    public static Map<String, JsonElement> fullatree;
    public static JsonObject castTreeObj;
    public static Cast cast = Cast.Warrior;
    public static Set<Integer> unlockedAbilIds = new HashSet<>();
    public static Set<Integer> atreeState = new HashSet<>();
    public static Map<SP, Integer> stats = SP.createStatMap();
    public static String atreeSuffix;
    public static MinecraftClient client;
    public static Clickable BUTTON;
    public static Clickable UI = new Clickable(() -> true);
    public static List<Integer> tomeIds = Collections.nCopies(8, null);
    public static List<ID.ItemType> types = List.of(ID.ItemType.Helmet, ID.ItemType.Chestplate, ID.ItemType.Leggings, ID.ItemType.Boots, ID.ItemType.Ring, ID.ItemType.Ring, ID.ItemType.Bracelet, ID.ItemType.Necklace);
    public static int REFETCH_DELAY = 40;
    public static int ATREE_IDLE; // How many ticks is elapsed before turning page while reading atree
    public static List<String> craftedHashes = new ArrayList<>(Collections.nCopies(9, ""));
    public static List<Integer> ids = new ArrayList<>();
    public static int wynnLevel;
    public static boolean readAtree = false;
    private static Manager configManager;

    public static Manager getConfigManager() {
        return configManager;
    }

    public static List<Gear> getCurrentEquipment() {
        ClientPlayerEntity player = client.player;
        Gear mainHand = new Gear(player.getMainHandStack());
        //  Armor list BOOTS -> HELM
        List<ItemStack> items = new ArrayList<>(player.getInventory().armor);
        //  Reverse: HELM -> BOOTS
        Collections.reverse(items);
        List<Gear> equipment = new ArrayList<>(items.stream().map(Gear::new).toList());

        //  Add equipment: Slots 9 to 12

        for (int i = 9; i < 13; i++) {
            equipment.add(new Gear(player.getInventory().main.get(i), ID.ItemType.values()[i - 5]));
        }

        equipment.add(mainHand);

        return equipment;
    }

    public static void buildCraftedMainHand() {
        buildCraftedItem(client.player.getMainHandStack());
    }

    public static void buildCraftedItem(ItemStack itemStack) {

        CustomItem item = getItem(itemStack);
        String customHash = item == null ? "" : item.encodeCustom(true);

        if (customHash.isEmpty()) {
            client.player.sendMessage(Text.literal("Couldn't encode this item"), false);
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
            return;
        }
        String url = WYNNCUSTOM_DOMAIN + customHash;
        String fullHash = "CI-" + customHash;

        client.player.sendMessage(Utils.getItemPrintTemplate(item, fullHash, url), false);

    }

    /**
     * Generates a player build URL based on provided item IDs, ability tree code, and other parameters.
     *
     * @param atreeCode A string representing the ability tree code to include in the build.
     */
    public static void buildWithArgs(String atreeCode, boolean precise) {
        if (client.player == null) return;
        List<Gear> equipment = getCurrentEquipment();

        Build build = new Build(equipment, precise, wynnLevel, stats, tomeIds, atreeCode);
        boolean success = build.generateUrl();
        if (!success) {
            return;
        }

        build.display();
    }

    public static int build() {
        buildWithArgs(atreeSuffix, WynnBuildClient.getConfigManager().getConfig().getPrecision() == 1);
        return 1;
    }

    public static void displayErr(String errorMessage) {
        client.player.sendMessage(Text.literal(errorMessage).styled(style -> style.withColor(Formatting.RED)), false);
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
    }

    @Override
    public void onInitializeClient() {

        client = MinecraftClient.getInstance();
        Task.init();
        AllIDs.load();
        WynnData.load();
        ScreenManager.register();

        configManager = new Manager();
        configManager.loadConfig();
        readAtree = !configManager.getConfig().getAtreeEncoding().isEmpty();

        Ability.refreshTree();

        BUTTON = new Clickable(() -> configManager.getConfig().isShowButtons());

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen screen1) {
                BUTTON.addTo(screen1, Clickable.AXISPOS.END, Clickable.AXISPOS.END, 100, 20, Text.literal("BUILD").styled(style -> style.withBold(true).withColor(Formatting.GREEN)), button -> build());
            }
        });

        CommandRegistry.init(client);
    }
}
