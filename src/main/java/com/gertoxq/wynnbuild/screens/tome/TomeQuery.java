package com.gertoxq.wynnbuild.screens.tome;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.handlers.container.type.ContainerContentChangeType;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.models.items.items.game.TomeItem;
import com.wynntils.utils.mc.LoreUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TomeQuery {

    public static final List<Integer> EMPTY_IDS = Collections.nCopies(14, -1);
    public static final List<Integer> TOME_SLOTS = List.of(11, 19, 22, 30, 31, 32, 4, 49, 15, 25, 28, 38, 34, 42);
    private static final int TOME_SLOT = 8;
    private static final int TOME_MENU_CONTENT_BOOK_SLOT = 89;

    public void queryTomeInfo() {
        ScriptedContainerQuery tomeQuery = ScriptedContainerQuery.builder("wynnbuild.tomequery")
                .onError(err -> System.out.println("wynnbuild.tomequery: " + err))
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                        .expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME)
                        .processIncomingContainer(this::processTomeUnlocked))
                .conditionalThen(
                        this::checkTomesUnlocked,
                        QueryStep.clickOnSlot(TOME_SLOT)
                                .expectContainerTitle(ContainerModel.MASTERY_TOMES_NAME)
                                .verifyContentChange(this::verifyChange)
                                .processIncomingContainer(this::processTomes))
                .execute(() -> WynnBuild.info("Read tomes: {}", WynnBuild.tomeIds))
                .build();
        tomeQuery.executeQuery();
    }

    public void processTomes(ContainerContent content) {
        WynnBuild.tomeIds = getTomeIds(content);
    }

    private List<Integer> getTomeIds(ContainerContent content) {
        return TOME_SLOTS.stream().map(slot -> {
            Optional<TomeItem> tomeOptional = Models.Item.asWynnItem(content.items().get(slot), TomeItem.class);
            if (tomeOptional.isEmpty()) {
                return -1;
            }
            Optional<Integer> optionalId = Optional.ofNullable(WynnData.getTomeMap().get(tomeOptional.get().getName()));
            if (optionalId.isEmpty()) {
                WynnBuild.warn("Unknown tome: name={}", tomeOptional.get().getName());
            }
            return optionalId.orElse(-1);
        }).toList();
    }

    private boolean verifyChange(
            ContainerContent content,
            Int2ObjectFunction<ItemStack> changes,
            ContainerContentChangeType changeType) {
        return changeType == ContainerContentChangeType.SET_CONTENT
                && changes.containsKey(TomeQuery.TOME_MENU_CONTENT_BOOK_SLOT)
                && (content.items().get(TomeQuery.TOME_MENU_CONTENT_BOOK_SLOT).getItem() == Items.POTION);
    }

    private boolean checkTomesUnlocked(ContainerContent content) {
        return LoreUtils.getStringLore(content.items().get(TOME_SLOT)).contains("✔");
    }

    public void processTomeUnlocked(ContainerContent content) {
        if (!checkTomesUnlocked(content)) {
            WynnBuild.tomeIds = EMPTY_IDS;
        }
    }
}
