package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.base.bitcodemaps.BaseEncoding;
import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.custom.CustomUtil;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.sp.SkillpointList;
import com.gertoxq.wynnbuild.build.Aspect;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery;
import com.gertoxq.wynnbuild.screens.charinfo.CharInfoQuery;
import com.gertoxq.wynnbuild.screens.tome.TomeScreenHandler;
import com.gertoxq.wynnbuild.util.Utils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WynnBuild implements ModInitializer {
    public static final String DOMAIN = "https://wynnbuilder.github.io/";
    public static final String BUILDER_DOMAIN = DOMAIN + "builder/#";
    public static final String WYNNCUSTOM_DOMAIN = DOMAIN + "custom/#";
    public static final BaseEncoding ENC = new BaseEncoding();
    public static final int WYNN_VERSION_ID = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger("wynnbuild");
    public static SkillpointList stats = SkillpointList.empty();
    public static List<Aspect> aspects = new ArrayList<>();
    public static String atreeSuffix;
    public static MinecraftClient client;
    public static List<Integer> tomeIds = TomeScreenHandler.EMPTY_IDS;
    public static int wynnLevel;
    public static Manager configManager;
    public static Set<Integer> atreeState = new HashSet<>();
    public static Cast cast = Cast.Warrior;

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
        List<Custom> equipment = getPlayerEquipment();
        if (equipment == null) return;

        if (atreeState.isEmpty() || forceRefetchAtree) {
            if (atreeState.isEmpty())
                WynnBuild.message(Text.literal("No ability tree found, fetching...").styled(style -> style.withColor(Formatting.RED)));
            new AbilityTreeQuery().queryTree(() -> CharInfoQuery.fetchStatsBeforeBuild(
                    () -> new Build(equipment, precise, stats, wynnLevel, tomeIds, atreeState, aspects).display()));
        } else {
            CharInfoQuery.fetchStatsBeforeBuild(
                    () -> new Build(equipment, precise, stats, wynnLevel, tomeIds, atreeState, aspects).display());
        }
    }

    public static void build() {
        buildWithArgs(getConfigManager().getConfig().getPrecision() == 1, false);
    }

    public static void displayErr(String errorMessage) {
        WynnBuild.message(Text.literal(errorMessage).styled(style -> style.withColor(Formatting.RED)));
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F));
    }

    public static AtreeCoder getAtreeCoder() {
        return AtreeCoder.getAtreeCoder(cast);
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

    public static void message(Text text) {
        assert client.player != null : "Cannot send message, there is no player";
        client.player.sendMessage(text, false);
    }

    @Override
    public void onInitialize() {

    }
}
