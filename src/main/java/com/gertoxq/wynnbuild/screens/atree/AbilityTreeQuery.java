package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
                        .expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME))
                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT)
                        .expectContainerTitle(ContainerModel.ABILITY_TREE_PATTERN.pattern()))
                .execute(() -> this.pageCount = 0)
                .repeat(
                        c -> ScriptedContainerQuery.containerHasSlot(
                                c, PREVIOUS_PAGE_SLOT, Items.POTION, PREVIOUS_PAGE_ITEM_NAME),
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT).processIncomingContainer(c -> this.pageCount++))
                .reprocess(processor::processPage)
                .repeat(
                        c -> ScriptedContainerQuery.containerHasSlot(
                                c, NEXT_PAGE_SLOT, Items.POTION, NEXT_PAGE_ITEM_NAME) && processor.doContinue(),
                        QueryStep.clickOnSlot(NEXT_PAGE_SLOT).processIncomingContainer(processor::processPage))
                .execute(() ->
                        McUtils.sendMessageToClient(Text.literal("Ability tree fetched").styled(style -> style.withColor(Formatting.GRAY))))
                .execute(after)
                .build();

        query.executeQuery();
    }

    public static class AtreeFetcher {
        private int page = 1;
        private boolean doContinue = true;

        protected void processPage(ContainerContent content, int page) {
            List<ItemStack> items = content.items();
            Set<AtreeNode> allPossibleNodes = new HashSet<>();

            WynnBuild.debug("Processing ability tree page {}", page);
            WynnBuild.debug("Existing nodes {}", Arrays.toString(atreeState.toArray()));

            for (AtomicInteger i = new AtomicInteger(0); i.get() < 54; i.getAndIncrement()) {
                ItemStack stack = items.get(i.get());
                if (stack.isEmpty()) continue;
                if (Utils.getLore(stack) == null || Utils.getLore(stack).isEmpty()) continue;


                AtreeNode node = new AtreeNode(stack, i.get(), page);
                WynnBuild.debug("Found node at {}", String.valueOf(i.get()));
                WynnBuild.debug("Processing node {}", node.getName());
                if (node.getId().isEmpty()) {
                    WynnBuild.debug("No ability id found for node {}", i.get() + ":" + node.getName());
                    continue;
                }
                if (!node.isUnlockedOrUnreachable()) {
                    WynnBuild.debug("Unreachable node");
                    continue;
                }
                allPossibleNodes.add(node);
            }
            Set<Integer> pageUnlocked = findUnlocked(allPossibleNodes.stream().map(atreeNode -> atreeNode.ability.id()).collect(Collectors.toSet()));
            if (pageUnlocked.isEmpty()) doContinue = false;
            WynnBuild.debug("Unlocked nodes on page {}", Arrays.toString(pageUnlocked.toArray()));
            atreeState.addAll(pageUnlocked);
            WynnBuild.debug("Nodes at page end {}", Arrays.toString(atreeState.toArray()));
        }

        public boolean doContinue() {
            return doContinue;
        }

        private Set<Integer> roots(Set<Integer> all) {
            return all.stream().filter(id -> {
                if (id == 0) return true;
                List<Integer> parents = Ability.getById(id).parents();
                return parents.stream().anyMatch(atreeState::contains);
            }).collect(Collectors.toSet());
        }

        private Set<Integer> findUnlocked(Set<Integer> all) {
            Set<Integer> roots = roots(all);

            Set<Integer> unlocked = new HashSet<>(roots);
            roots.forEach(root -> recurseNodes(root, all, unlocked));

            return unlocked;
        }

        private void recurseNodes(int id, Set<Integer> allNodes, Set<Integer> unlocked) {
            Ability.getById(id).children().stream()
                    .filter(child -> allNodes.contains(child) && !unlocked.contains(child))
                    .forEach(child -> {
                        unlocked.add(child);
                        recurseNodes(child, allNodes, unlocked);
                    });
        }

        protected void processPage(ContainerContent content) {
            processPage(content, page);
            page++;
        }
    }
}
