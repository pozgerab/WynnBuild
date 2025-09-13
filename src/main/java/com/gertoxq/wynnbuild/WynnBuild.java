package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.custom.CustomUtil;
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
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    public static boolean debug = true;
    public static Integer dataReads = null;
    public static Boolean currentPrecision = null;
    public static Boolean currentForceRefetchAtree = null;

    public static Manager getConfigManager() {
        return configManager;
    }

    public static List<Custom> getPlayerEquipment() {
        assert client.player != null : "Player is null";
        ClientPlayerEntity player = client.player;
        Custom mainHand = CustomUtil.getFromStack(player.getMainHandStack());
        if (mainHand.isNone() || !mainHand.getType().isWeapon()) {
            displayErr("Hold a weapon");
            return null;
        }
        //  Armor list BOOTS -> HELM
        List<ItemStack> items = new ArrayList<>(player.getInventory().armor);
        //  Reverse: HELM -> BOOTS
        Collections.reverse(items);
        List<Custom> equipment = new ArrayList<>(items.stream().map(CustomUtil::getFromStack).toList());

        //  Add equipment: Slots 9 to 12

        for (int i = 9; i < 13; i++) {
            equipment.add(CustomUtil.getFromStack(player.getInventory().main.get(i)));
        }

        equipment.add(mainHand);

        return equipment;
    }

    public static void buildMainHand() {
        buildItemStackCustom(client.player.getMainHandStack());
    }

    public static void buildItemStackCustom(ItemStack itemStack) {

        Custom item = CustomUtil.getFromStack(itemStack);
        if (item.isNone()) {
            displayErr("Couldn't encode this item");
            return;
        }

        String customHash = item.encodeCustom(true).toB64();

        String url = WYNNCUSTOM_DOMAIN + customHash;
        String fullHash = "CI-" + customHash;

        WynnBuild.message(Utils.getItemPrintTemplate(item, fullHash, url));

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

        List<Custom> equipment = getPlayerEquipment();
        if (equipment == null) return;
        new Build(equipment, currentPrecision, totalSp, manualPoints, Models.CharacterStats.getLevel(), tomeIds, atreeState, List.of()).display();
        WynnBuild.currentPrecision = null;
    }

    public static int build() {
        buildWithArgs(getConfigManager().getConfig().getPrecision() == 1, false);
        return 1;
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
