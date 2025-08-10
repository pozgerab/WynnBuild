package com.gertoxq.wynnbuild.screens.charinfo;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.fields.Cast;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.base.sp.SkillpointList;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.items.items.gui.SkillPointItem;
import com.wynntils.utils.wynn.InventoryUtils;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.gertoxq.wynnbuild.WynnBuild.client;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.castTreeObj;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.fullatree;
import static com.gertoxq.wynnbuild.util.Utils.getLore;
import static com.gertoxq.wynnbuild.util.Utils.removeFormat;

public class CharInfoQuery {

    private static final Pattern LEVEL_PATTERN = Pattern.compile("^Combat Lv: (\\d+)$");
    private static final Pattern CLASS_PATTERN =
            Pattern.compile(Arrays.stream(Cast.values()).map(cast -> Pattern.quote(cast.name()) + "|" + Pattern.quote(cast.alias)).collect(Collectors.joining("|", "^Class: (", ")$")));

    public static void fetchStatsBeforeBuild(Runnable after) {
        ScriptedContainerQuery query = ScriptedContainerQuery.builder("wynnbuild.fetchstats")
                .onError(string -> {
                    WynnBuild.warn("Error occured: {}", string);
                })
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM).expectContainerTitle("\uDAFF\uDFDC\uE003").processIncomingContainer(containerContent -> {

                    WynnBuild.stats = getSp(containerContent);
                    ItemStack profileStack = containerContent.items().get(7);

                    Matcher lvlMatcher = LEVEL_PATTERN.matcher(removeFormat(getLore(profileStack).get(2).getString()));
                    if (lvlMatcher.matches()) {
                        WynnBuild.wynnLevel = Integer.parseInt(lvlMatcher.group(1));
                    }

                    Cast prevCast = WynnBuild.cast;
                    Matcher classMatcher = CLASS_PATTERN.matcher(removeFormat(getLore(profileStack).get(3).getString()));
                    if (classMatcher.matches()) {
                        WynnBuild.cast = Cast.find(classMatcher.group(1)).get();
                    }

                    if (!WynnBuild.cast.equals(prevCast)) {
                        castTreeObj = fullatree.get(WynnBuild.cast.name()).getAsJsonObject();
                        if (WynnBuild.cast.equals(prevCast)) {
                            Ability.refreshTree();
                        }
                    }

                }))
                .execute(after)
                .build();
        client.execute(query::executeQuery);
    }

    private static SkillpointList getSp(ContainerContent container) {
        SkillpointList stats = SkillpointList.empty();
        SP.getStatContainerMap().forEach((slot, id) -> {
            Optional<SkillPointItem> spItem = Models.Item.asWynnItem(container.items().get(slot), SkillPointItem.class);
            spItem.ifPresent(skillPointItem -> stats.set(id.ordinal(), skillPointItem.getSkillPoints()));
        });
        return stats;
    }
}
