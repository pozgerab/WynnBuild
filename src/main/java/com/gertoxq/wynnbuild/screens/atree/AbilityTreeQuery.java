package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.container.scriptedquery.QueryBuilder;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.abilitytree.AbilityTreeModel;
import com.wynntils.models.abilitytree.parser.UnprocessedAbilityTreeInfo;
import com.wynntils.models.abilitytree.type.AbilityTreeNodeState;
import com.wynntils.models.abilitytree.type.AbilityTreeSkillNode;
import com.wynntils.models.containers.containers.AbilityTreeContainer;
import com.wynntils.models.containers.containers.CharacterInfoContainer;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.type.Pair;
import com.wynntils.utils.wynn.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AbilityTreeQuery {

    public static final int PREVIOUS_PAGE_SLOT = 57;
    public static final int NEXT_PAGE_SLOT = 59;
    public static final int ABILITY_TREE_SLOT = 9;
    static final StyledText PREVIOUS_PAGE_ITEM_NAME = StyledText.fromString("§7Previous Page");

    public AbilityTreeQuery() {
        WynnBuild.AbilityTree.clear();
    }

    public void queryTree() {
        queryTree(() -> {
        });
    }

    public void queryTree(Runnable after) {

        if (Ability.getAbilityMap().isEmpty()) {
            WynnBuild.message(Text.literal("Ability data is still loading, please wait a moment and try again."));
            return;
        }

        AtreeFetcher processor = new AtreeFetcher();

        QueryBuilder builder = ScriptedContainerQuery.builder("wynnbuild.treequery")
                .onError(err -> WynntilsMod.warn("wynnbuild.treequery: " + err))

                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                        .expectContainer(CharacterInfoContainer.class))

                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT)
                        .expectContainer(AbilityTreeContainer.class))

                .repeat(
                        c -> ScriptedContainerQuery.containerHasSlot(
                                c, PREVIOUS_PAGE_SLOT, Items.POTION, PREVIOUS_PAGE_ITEM_NAME),
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT)
                                .expectContainer(AbilityTreeContainer.class)
                                .accumulateSetSlotChanges(2))
                .reprocess(processor::processPage);


        for (int page = 2; page <= AbilityTreeModel.ABILITY_TREE_PAGES; page++) {
            builder.then(QueryStep.clickOnSlot(NEXT_PAGE_SLOT)
                            .expectContainer(AbilityTreeContainer.class)
                            .accumulateSetSlotChanges(2))
                    .reprocess(processor::processPage);
        }

        builder.execute(() -> {
            processor.saveEncoded();
            WynnBuild.AbilityTree.saveCache();
            McUtils.sendMessageToClient(Text.literal("Ability tree fetched").styled(style -> style.withColor(Formatting.GRAY)));
        }).execute(after);


        builder.build().executeQuery();
    }

    public static class AtreeFetcher {
        private int page = 1;
        private List<Ability> pageAbilities = Ability.getPage(page);
        private final UnprocessedAbilityTreeInfo treeInfo = new UnprocessedAbilityTreeInfo();

        Set<Integer> changedSlots = new HashSet<>();

        protected void processPage(ContainerContent content, int page) {

            List<Pair<AbilityTreeSkillNode, AbilityTreeNodeState>> pageNodes = new ArrayList<>();

            changedSlots = new HashSet<>();

            List<ItemStack> items = content.items();

            for (Ability ability : pageAbilities) {

                int slot = ability.slot();

                ItemStack stack = items.get(slot);

                WynnBuild.debug("Processing node {}, {}", stack.getName().getString(), slot);
                WynnBuild.debug("Expected: {}, slot: {}", ability.displayName(), ability.slot());

                treeInfo.processItem(stack, page, slot, false);

                Pair<AbilityTreeSkillNode, AbilityTreeNodeState> pair = AbilityTreeModel.ABILITY_TREE_PARSER.parseNodeFromItem(stack, page, slot, 0);
                pageNodes.add(pair);
            }

            Set<String> unlocked = pageNodes.stream().filter(node -> node.b() == AbilityTreeNodeState.UNLOCKED)
                    .map(node -> node.a().name()).collect(Collectors.toSet());

            WynnBuild.debug("Unlocked page abilities: {}", unlocked);
        }

        protected void saveEncoded() {
            WynnBuild.AbilityTree.setFromAbilityTreeInfo(treeInfo.getProcesssed());
        }

        protected void processPage(ContainerContent content) {
            processPage(content, page);
            page++;

            pageAbilities = Ability.getPage(page);
        }

    }
}
