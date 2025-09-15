package com.gertoxq.wynnbuild.screens.aspect;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.handlers.container.type.ContainerContentChangeType;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.utils.wynn.InventoryUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery.ABILITY_TREE_SLOT;

public class AspectInfo {

    public static final Pattern ASPECT_TITLE_PATTERN = Pattern.compile("\udaff\udfea\ue002");
    private static final List<Integer> ASPECT_SLOTS = List.of(18, 11, 4, 15, 26);
    private static final Pattern EMPTY_ASPECT_PATTERN = Pattern.compile("^(?:§.)*Empty Aspect Socket$");
    private static final Pattern LOCKED_ASPECT_PATTERN = Pattern.compile("^(?:§.)*Locked Aspect Socket$");
    public static Map<String, Integer> aspectMap;

    public void queryAspectInfo() {
        ScriptedContainerQuery query = ScriptedContainerQuery.builder("wynnbuild.fetchaspect")
                .onError(string -> WynnBuild.warn("Error querying aspect info: {}", Utils.escapeToUnicode(string)))
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM).expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME))
                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT).expectContainerTitle(ContainerModel.ABILITY_TREE_PATTERN.pattern()))
                .then(QueryStep.clickOnSlot(86)
                        .expectContainerTitle(ContainerModel.ABILITY_TREE_PATTERN.pattern()))
                .then(QueryStep.clickOnSlot(0).verifyContentChange(this::verifyChange)
                        .expectContainerTitle(ASPECT_TITLE_PATTERN.pattern())
                        .processIncomingContainer(this::processAspects))
                .execute(() -> WynnBuild.info("Fetched Aspects: " + WynnBuild.aspects.stream().map(AspectItem::getName).collect(Collectors.joining(", "))))
                .build();
        query.executeQuery();
    }

    private boolean verifyChange(
            ContainerContent content,
            Int2ObjectFunction<ItemStack> changes,
            ContainerContentChangeType changeType) {
        return changes.containsKey(54);
    }

    public void processAspects(ContainerContent content) {
        List<AspectItem> aspects = new ArrayList<>();
        ASPECT_SLOTS.forEach(slot -> {
            Optional<AspectItem> aspectOpt = Models.Item.asWynnItem(content.items().get(slot), AspectItem.class);
            aspectOpt.ifPresentOrElse(foundAspect -> {
                Integer aspectId = aspectMap.get(foundAspect.getName());
                if (aspectId == null) {
                    WynnBuild.warn("Could not find aspect id for {} {}", foundAspect.getRequiredClass().getName(), foundAspect.getName());
                    return;
                }
                aspects.add(aspectOpt.get());
            }, () -> aspects.add(null));
        });
        WynnBuild.aspects = aspects;
    }

    public boolean verifyValidAspectContainer(ContainerContent content) {
        return ASPECT_SLOTS.stream().allMatch(slot ->
                Models.Item.asWynnItem(content.items().get(slot), AspectItem.class).isPresent()
                        || EMPTY_ASPECT_PATTERN.matcher(content.items().get(slot).getName().getString()).matches()
                        || LOCKED_ASPECT_PATTERN.matcher(content.items().get(slot).getName().getString()).matches());
    }
}
