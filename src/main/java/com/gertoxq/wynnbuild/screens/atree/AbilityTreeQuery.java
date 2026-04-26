package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.handlers.container.type.ContainerContentChangeType;
import com.wynntils.models.containers.containers.AbilityTreeContainer;
import com.wynntils.models.containers.containers.CharacterInfoContainer;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gertoxq.wynnbuild.WynnBuild.atreeState;

public class AbilityTreeQuery {

    public static final int PREVIOUS_PAGE_SLOT = 57;
    public static final int NEXT_PAGE_SLOT = 59;
    public static final int ABILITY_TREE_SLOT = 9;
    static final StyledText NEXT_PAGE_ITEM_NAME = StyledText.fromString("§7Next Page");
    static final StyledText PREVIOUS_PAGE_ITEM_NAME = StyledText.fromString("§7Previous Page");
    private int pageCount;

    public AbilityTreeQuery() {
        atreeState = new HashSet<>();
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
        ScriptedContainerQuery query = ScriptedContainerQuery.builder("wynnbuild.treequery")
                .onError(err -> WynntilsMod.warn("wynnbuild.treequery: " + err))
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                        .expectContainer(CharacterInfoContainer.class))
                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT)
                        .expectContainer(AbilityTreeContainer.class))
                .execute(() -> this.pageCount = 0)
                .repeat(
                        c -> ScriptedContainerQuery.containerHasSlot(
                                c, PREVIOUS_PAGE_SLOT, Items.POTION, PREVIOUS_PAGE_ITEM_NAME),
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT)
                                .expectContainer(AbilityTreeContainer.class).processIncomingContainer(c -> this.pageCount++)
                                .verifyContentChange(processor::verifyChangeOnReverse))
                .reprocess(processor::processPage)
                .repeat(
                        c -> ScriptedContainerQuery.containerHasSlot(
                                c, NEXT_PAGE_SLOT, Items.POTION, NEXT_PAGE_ITEM_NAME) && processor.doContinue(),
                        QueryStep.clickOnSlot(NEXT_PAGE_SLOT)
                                .expectContainer(AbilityTreeContainer.class).verifyContentChange((processor::verifyChange))
                                .processIncomingContainer(processor::processPage))
                .execute(() -> {
                    McUtils.sendMessageToClient(Text.literal("Ability tree fetched").styled(style -> style.withColor(Formatting.GRAY)));
                    WynnBuild.saveAtreeCache();
                })
                .execute(after)
                .build();

        query.executeQuery();
    }

    public static class AtreeFetcher {
        private int page = 1;
        private boolean doContinue = true;
        private List<Ability> pageAbilities = Ability.getPage(page);
        private List<Integer> requiredSlots = pageAbilities.stream().map(Ability::slot).toList();

        Set<Integer> changedSlots = new HashSet<>();

        protected void processPage(ContainerContent content, int page) {

            changedSlots = new HashSet<>();

            List<ItemStack> items = content.items();
            Set<Integer> unlockedIds = new HashSet<>();

            WynnBuild.debug("Processing ability tree page {}", page);
            WynnBuild.debug("Existing nodes {}", Arrays.toString(atreeState.toArray()));

            for (Ability ability : pageAbilities) {

                int slot = ability.slot();

                ItemStack stack = items.get(slot);
                if (!AtreeNode.isValidNode(stack, slot)) {
                    throw new RuntimeException("Expected ability node at slot " + slot + " on page " + page + ", but found invalid item: " + stack.getName().getString());
                }

                AtreeNode node = new AtreeNode(stack, ability);
                WynnBuild.debug("Found node at {}", String.valueOf(slot));
                WynnBuild.debug("Processing node {}", node.getName());

                if (node.getState() != AbilityNodeState.UNLOCKED) {
                    WynnBuild.debug("Skipping not unlocked");
                    continue;
                }
                unlockedIds.add(node.getId().get());
            }
            if (unlockedIds.isEmpty()) doContinue = false;
            WynnBuild.debug("Unlocked nodes on page {}", Arrays.toString(unlockedIds.toArray()));
            atreeState.addAll(unlockedIds);
            WynnBuild.debug("Nodes at page end {}", Arrays.toString(atreeState.toArray()));
        }

        public boolean doContinue() {
            return doContinue;
        }

        protected void processPage(ContainerContent content) {
            processPage(content, page);
            page++;

            pageAbilities = Ability.getPage(page);

            requiredSlots = pageAbilities.stream().map(Ability::slot).toList();
        }

        /*
         *   ORDER: (this is bs)
         *   1. Next Page on slot 59
         *   2. Abilities in order
         *   3. Previous Page on slot 57
         *   4. Ability points on slot 58
         *   5. Next Page AGAIN on slot 59
         *   6. Archetypes in order: 74, 76, 78
         */
        public boolean verifyChange(ContainerContent content, Int2ObjectMap<ItemStack> changes, ContainerContentChangeType containerContentChangeType) {

            WynnBuild.debug("Change incoming: {}", containerContentChangeType.name());

            if (containerContentChangeType != ContainerContentChangeType.SET_SLOT) return false;

            changedSlots.addAll(changes.keySet());
            WynnBuild.debug("Changed slot: {}, {}", changes.keySet(), changes.values());

            return !content.items().get(NEXT_PAGE_SLOT).isEmpty() && changedSlots.containsAll(requiredSlots);
        }

        public boolean verifyChangeOnReverse(ContainerContent content, Int2ObjectMap<ItemStack> changes, ContainerContentChangeType containerContentChangeType) {
            WynnBuild.debug("Query backwards change: {}", containerContentChangeType);

            boolean hasPrevPage = !content.items().get(PREVIOUS_PAGE_SLOT).isEmpty();
            boolean hasNextPage = !content.items().get(NEXT_PAGE_SLOT).isEmpty();
            boolean loadedReqSlots = requiredSlots.stream().allMatch(slot -> AtreeNode.isValidNode(content.items().get(slot), slot));

            WynnBuild.debug("hasPrevPage = {}, hasNextPage = {}, loadedReqSlots = {}", hasPrevPage, hasNextPage, loadedReqSlots);

            // IMPORTANT    hasNext is required bc the item is only loaded once and if we skip that we won't be
            //              able to navigate back. hasPrev is also required to be able to navigate, the only
            //              time we do not is when we're on the first page, so the loadedSlots is fulfilled
            boolean pass = hasNextPage && (hasPrevPage || loadedReqSlots);

            WynnBuild.debug("pass = {}", pass);
            return pass;
        }
    }
}
