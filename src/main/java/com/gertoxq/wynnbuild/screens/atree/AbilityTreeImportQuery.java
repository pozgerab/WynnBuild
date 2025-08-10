package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.atreeimport.Atrouter;
import com.wynntils.core.WynntilsMod;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.handlers.container.type.ContainerContentVerification;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.utils.wynn.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.*;

import static com.gertoxq.wynnbuild.screens.atree.AbilityTreeQuery.*;

public class AbilityTreeImportQuery {

    private final Set<Integer> applyingIds;
    private int pageCount;

    public AbilityTreeImportQuery(Set<Integer> applyingIds) {
        this.applyingIds = applyingIds;
    }

    public void importTree() {
        AtreeImporter importer = new AtreeImporter(applyingIds);
        ScriptedContainerQuery query = ScriptedContainerQuery.builder("wynnbuild.treeimport")
                .onError(err -> WynntilsMod.warn("wynnbuild.treeimport: " + err))
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                        .expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME))
                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT)
                        .expectContainerTitle(ContainerModel.ABILITY_TREE_PATTERN.pattern()))
                .execute(() -> this.pageCount = 0)
                .repeat(
                        c -> ScriptedContainerQuery.containerHasSlot(
                                c, PREVIOUS_PAGE_SLOT, Items.POTION, PREVIOUS_PAGE_ITEM_NAME),
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT).processIncomingContainer(c -> this.pageCount++))

                .reprocess(importer::populateOrStop)
                .then(QueryStep.clickOnSlot(4).verifyContentChange(importer.verifyQueueUpdate()))
                .repeat(containerContent -> importer.goOn,
                        QueryStep.clickOnSlot(importer.poll())
                                .processIncomingContainer(importer::populateOrStop)
                                .verifyContentChange(importer.verifyQueueUpdate()))

                .execute(() -> WynnBuild.message(Text.literal("Did it work?")))
                .build();
        query.executeQuery();
    }

    public static class AtreeImporter {
        final ArrayDeque<Integer> idQueue = new ArrayDeque<>();
        private final Set<Integer> allApplyingIds;
        private boolean goOn = true;


        public AtreeImporter(Set<Integer> allApplyingIds) {
            this.allApplyingIds = allApplyingIds;
            this.allApplyingIds.remove(0);
        }

        public int poll() {
            if (idQueue.isEmpty()) {
                WynnBuild.error("quene is empty");
                throw new RuntimeException("queue is empty");
            }
            int clicked = idQueue.poll();
            allApplyingIds.remove(clicked);
            return clicked;
        }

        public ContainerContentVerification verifyQueueUpdate() {
            return (containerContent, int2ObjectMap, containerContentChangeType)
                    -> (goOn && !idQueue.isEmpty()) || (!goOn && idQueue.isEmpty());
        }

        public void populateOrStop(ContainerContent content) {
            goOn = populateQueue(content);
        }

        protected boolean populateQueue(ContainerContent content) {
            if (allApplyingIds.isEmpty()) {
                WynnBuild.info("ran out of all ids");
                return false;
            }
            if (!idQueue.isEmpty()) {
                WynnBuild.info("queue is not empty, continuing");
                return true;
            }
            WynnBuild.info("populating queue for page");
            idQueue.addAll(new Atrouter(
                    new HashSet<>(getSlots(content).stream()
                            .filter(node -> allApplyingIds.contains(node.id))
                            .map(value -> value.id).toList()),
                    allApplyingIds).findRoute());
            WynnBuild.info("populated queue for page");
            if (ScriptedContainerQuery.containerHasSlot(content, NEXT_PAGE_SLOT, Items.POTION, NEXT_PAGE_ITEM_NAME)) {
                WynnBuild.info("added next page slot to queue");
                idQueue.add(NEXT_PAGE_SLOT);
            }
            return true;
        }

        public List<AtreeNode> getSlots(ContainerContent content) {
            List<AtreeNode> nodes = new ArrayList<>();
            for (int idx = 0; idx < content.items().size(); idx++) {
                ItemStack nodeStack = content.items().get(idx);
                AtreeNode node = new AtreeNode(nodeStack, idx);
                if (node.getId().isPresent()) {
                    nodes.add(node);
                }
            }
            return nodes;
        }
    }

}
