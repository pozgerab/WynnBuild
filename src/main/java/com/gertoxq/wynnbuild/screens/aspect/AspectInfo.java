package com.gertoxq.wynnbuild.screens.aspect;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.build.Aspect;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.utils.wynn.InventoryUtils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AspectInfo {

    public static final Pattern ASPECT_TITLE_PATTERN = Pattern.compile("\udaff\udfea\ue002");
    private static final List<Integer> aspectSlots = List.of(18, 11, 4, 15, 26);
    static final int ABILITY_TREE_SLOT = 9;

    public static void queryAspectInfo() {
        ScriptedContainerQuery query = ScriptedContainerQuery.builder("wynnbuild.fetchaspect")
                .onError(string -> {
                    WynnBuild.warn("Error querying aspect info: {}", string);
                })
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM).expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME))
                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT).expectContainerTitle(ContainerModel.ABILITY_TREE_PATTERN.pattern()))
                .then(QueryStep.clickOnSlot(86).expectContainerTitle(ASPECT_TITLE_PATTERN.pattern())
                        .processIncomingContainer(containerContent -> {
                            WynnBuild.aspects = getAspects(containerContent.items());
                        }))
                .build();
        query.executeQuery();
    }

    public static List<Aspect> getAspects(List<ItemStack> content) {
         List<Aspect> aspects = new ArrayList<>();
         aspectSlots.forEach(integer -> {
             Optional<AspectItem> aspectItem = Models.Item.asWynnItem(content.get(integer), AspectItem.class);
             aspectItem.ifPresentOrElse(foundAspect -> {
                 Cast aspectCast = Cast.valueOf(foundAspect.getRequiredClass().getName());
                 Optional<Integer> optionalId = WynnData.getAspectId(aspectCast, foundAspect.getName());
                 if (optionalId.isEmpty()) {
                     WynnBuild.warn("Could not find aspect id for {} {}", foundAspect.getRequiredClass().getName(), foundAspect.getName());
                     return;
                 }
                 Aspect aspect = new Aspect(
                         aspectCast,
                         foundAspect.getName(),
                         foundAspect.getTier(),
                         optionalId.get());
                 aspects.add(aspect);
             }, () -> aspects.add(new Aspect(Cast.Warrior, "", 0, -1)));
         });
         return aspects;
    }
}
