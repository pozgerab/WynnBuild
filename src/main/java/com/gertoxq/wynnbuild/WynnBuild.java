package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.base.custom.CustomCoder;
import com.gertoxq.wynnbuild.base.sp.Skillpoint;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery;
import com.gertoxq.wynnbuild.screens.tome.TomeQuery;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.models.elements.type.Skill;
import com.wynntils.models.inventory.type.InventoryAccessory;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.utils.mc.McUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WynnBuild implements ModInitializer {
    public static final String DOMAIN = "https://wynnbuilder.github.io/";
    public static final String BUILDER_DOMAIN = DOMAIN + "builder/#";
    public static final String WYNNCUSTOM_DOMAIN = DOMAIN + "custom/#";
    public static final int WYNN_VERSION_ID = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger("wynnbuild");
    public static MinecraftClient client;
    public static List<Integer> tomeIds = TomeQuery.EMPTY_IDS;
    public static Manager configManager;
    public static Set<Integer> atreeState = new HashSet<>();
    public static final boolean debug = false;
    public static Integer dataReads = null;
    public static Boolean currentPrecision = null;
    public static Boolean currentForceRefetchAtree = null;

    public static Manager getConfigManager() {
        return configManager;
    }

    public static List<ItemStack> getPlayerEquipment() {
        List<ItemStack> equipment = new ArrayList<>(McUtils.inventory().armor);
        Collections.reverse(equipment);

        for (int i : InventoryAccessory.getSlots()) {
            int baseSize = 0;
            if (McUtils.player().shouldCloseHandledScreenOnRespawn()) {
                baseSize = McUtils.player().currentScreenHandler.getStacks().size();
            }
            equipment.add(McUtils.inventory().getStack(i + baseSize));
        }

        Optional<GearItem> handItemOpt = Models.Item.asWynnItem(McUtils.player().getStackInHand(Hand.MAIN_HAND), GearItem.class);
        if (handItemOpt.isPresent() && handItemOpt.get().meetsActualRequirements() && handItemOpt.get().getGearType().isWeapon()) {
            equipment.add(McUtils.player().getStackInHand(Hand.MAIN_HAND));
        } else {
            WynnBuild.displayErr("Hold a weapon");
            return null;
        }

        return equipment;
    }

    public static void buildMainHand() {
        buildItemStackCustom(client.player.getMainHandStack());
    }

    public static void buildItemStackCustom(ItemStack itemStack) {

        Optional<GearItem> gearItemOpt = Models.Item.asWynnItem(itemStack, GearItem.class);
        if (gearItemOpt.isEmpty()) return;
        String customHash = CustomCoder.encode(gearItemOpt.get()).toB64();

        String url = WYNNCUSTOM_DOMAIN + customHash;
        String fullHash = "CI-" + customHash;

        WynnBuild.message(Utils.getItemPrintTemplate(gearItemOpt.get(), fullHash, url));

    }

    public static void buildWithArgs(boolean precise, boolean forceRefetchAtree) {
        if (client.player == null) return;
        currentPrecision = precise;
        currentForceRefetchAtree = forceRefetchAtree;
        if (atreeState.isEmpty() || currentForceRefetchAtree) {
            dataReads = 0;
            currentForceRefetchAtree = null;
            if (atreeState.isEmpty())
                WynnBuild.message(Text.literal("No ability tree found, fetching...").styled(style -> style.withColor(Formatting.RED)));
            Managers.TickScheduler.scheduleNextTick(() -> new AbilityTreeQuery().queryTree());
        } else {
            dataReads = 1;
            Managers.TickScheduler.scheduleNextTick(() -> new TomeQuery().queryTomeInfo());
        }
    }

    public static void buildAfterSp() {
        List<Integer> totalSp = Arrays.stream(Skill.values()).map(Models.SkillPoint::getTotalSkillPoints).toList();
        List<Integer> manualPoints = Arrays.stream(Skill.values()).map(Skillpoint::getManualPoints).toList();

        List<ItemStack> equipment = getPlayerEquipment();
        if (equipment == null) return;
        new Build(equipment, currentPrecision, totalSp, manualPoints, Models.CharacterStats.getLevel(), tomeIds, atreeState, List.of()).display();
        WynnBuild.currentPrecision = null;
    }

    public static void build() {
        buildWithArgs(getConfigManager().getConfig().getPrecision() == 1, false);
    }

    public static void displayErr(String errorMessage) {
        WynnBuild.message(Text.literal(errorMessage).styled(style -> style.withColor(Formatting.RED)));
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
    }

    public static AtreeCoder getAtreeCoder() {
        return AtreeCoder.getAtreeCoder(Models.Character.getClassType());
    }

    public static String getAtreeSuffix() {
        return getAtreeCoder().encode_atree(atreeState).toB64();
    }

    public static void warn(String format, Object... args) {
        LOGGER.warn(format, args);
    }

    public static void info(String format, Object... args) {
        LOGGER.info(format, args);
    }

    public static void error(String format, Object... args) {
        LOGGER.error(format, args);
    }

    public static void debug(String format, Object... args) {
        if (debug) {
            info(format, args);
        }
    }

    public static void debugClient(String message) {
        if (debug) {
            message(Text.literal("[DEBUG] ").styled(style -> style.withBold(true).withColor(Formatting.GOLD)).append(Text.literal(message).styled(style -> Style.EMPTY)));
        }
    }

    public static void message(Text text) {
        assert client.player != null : "Cannot send message, there is no player";
        client.player.sendMessage(text, false);
    }

    @Override
    public void onInitialize() {

    }
}
