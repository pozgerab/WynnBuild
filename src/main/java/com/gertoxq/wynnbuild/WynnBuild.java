package com.gertoxq.wynnbuild;

import com.gertoxq.wynnbuild.base.custom.CustomCoder;
import com.gertoxq.wynnbuild.config.ConfigType;
import com.gertoxq.wynnbuild.config.Manager;
import com.gertoxq.wynnbuild.screens.QueryStack;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.items.game.CraftedGearItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.utils.mc.McUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WynnBuild implements ModInitializer {
    public static final String REPO = "wynnbuilder";
    public static final String DOMAIN = "https://" + REPO + ".github.io/";
    public static final String BUILDER_DOMAIN = DOMAIN + "builder/#";
    public static final String WYNNCUSTOM_DOMAIN = DOMAIN + "custom/#";
    public static final String MOD_ID = "wynnbuild";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Manager configManager;
    private static boolean debug = false;

    public static Manager getConfigManager() {
        return configManager;
    }

    public static ConfigType getConfig() {
        return getConfigManager().getConfig();
    }

    public static void buildMainHand() {
        buildItemStackCustom(McUtils.player().getStackInHand(Hand.MAIN_HAND));
    }

    public static void buildItemStackCustom(ItemStack itemStack) {

        String customHash;
        GearTier tier;
        String name;
        Optional<GearItem> gearItemOpt = Models.Item.asWynnItem(itemStack, GearItem.class);
        if (gearItemOpt.isPresent()) {
            customHash = CustomCoder.encode(gearItemOpt.get(), null).toB64();
            tier = gearItemOpt.get().getGearTier();
            name = gearItemOpt.get().getName();
        } else {
            Optional<CraftedGearItem> craftedGearItemOpt = Models.Item.asWynnItem(itemStack, CraftedGearItem.class);
            if (craftedGearItemOpt.isEmpty()) return;
            customHash = CustomCoder.encode(craftedGearItemOpt.get(), null).toB64();
            tier = craftedGearItemOpt.get().getGearTier();
            name = craftedGearItemOpt.get().getName();
        }

        String url = WYNNCUSTOM_DOMAIN + customHash;
        String fullHash = "CI-" + customHash;

        WynnBuild.message(Utils.getItemPrintTemplate(name, tier, fullHash, url));

    }

    public static void buildWithArgs(boolean forceRefetchAtree) {

        Optional<GearItem> weapon = Models.Item.asWynnItem(McUtils.player().getStackInHand(Hand.MAIN_HAND), GearItem.class);
        if (weapon.isEmpty()) {
            WynnBuild.displayErr("Hold a weapon");
            return;
        }

        QueryStack.Builder query = QueryStack.builder().next(QueryStack.ContainerType.SKILLPOINTS);

        if (Models.AbilityTree.getUnlockedAbilities().isEmpty() || forceRefetchAtree) {
            if (Models.AbilityTree.getUnlockedAbilities().isEmpty()) {
                WynnBuild.message(Text.literal("Querying ability tree...").styled(style -> style.withColor(Formatting.GRAY)));
            }
            query.next(QueryStack.ContainerType.ATREE);
        }
        query.next(QueryStack.ContainerType.BUILD).runQuery();
    }

    public static void build() {
        buildWithArgs(false);
    }

    public static void displayErr(String errorMessage) {
        WynnBuild.message(Text.literal(errorMessage).styled(style -> style.withColor(Formatting.RED)));
        McUtils.mc().getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_ANVIL_LAND));
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
            message(Text.literal("[DEBUG] ").styled(style -> style.withBold(true).withColor(Formatting.GOLD)).append(Text.literal(message).styled(style -> style.withBold(false).withColor(Formatting.WHITE))));
        }
    }

    public static void toggleDebug() {
        debug = !debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void message(Text text) {
        assert McUtils.player() != null : "Cannot send message, there is no player";
        McUtils.player().sendMessage(text, false);
    }

    @Override
    public void onInitialize() {

    }
}
