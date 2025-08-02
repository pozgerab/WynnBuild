package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.util.Utils;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.abilitytree.AbilityTreeModel;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gertoxq.wynnbuild.WynnBuild.atreeState;
import static com.gertoxq.wynnbuild.WynnBuild.atreeSuffix;

public class AbilityTreeQuery {

    static final int ABILITY_TREE_SLOT = 9;
    static final int PREVIOUS_PAGE_SLOT = 57;
    static final int NEXT_PAGE_SLOT = 59;
    static final StyledText NEXT_PAGE_ITEM_NAME = StyledText.fromString("§7Next Page");
    static final StyledText PREVIOUS_PAGE_ITEM_NAME = StyledText.fromString("§7Previous Page");
    private int pageCount;

    public AbilityTreeQuery() {
        atreeState = new HashSet<>();
    }

    public void queryTree() {
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
                                c, NEXT_PAGE_SLOT, Items.POTION, NEXT_PAGE_ITEM_NAME),
                        QueryStep.clickOnSlot(NEXT_PAGE_SLOT).processIncomingContainer(processor::processPage))
                .repeat(
                        c -> {
                            this.pageCount++;
                            return this.pageCount != AbilityTreeModel.ABILITY_TREE_PAGES;
                        },
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT))
                .execute(() -> {
                    BitVector encodedTree = WynnBuild.getAtreeCoder().encode_atree(atreeState);
                    atreeSuffix = encodedTree.toB64();
                    McUtils.sendMessageToClient(Text.literal("Ability tree fetched, click to save ")
                            .append(Text.literal(atreeSuffix).styled(style -> style.withColor(Formatting.DARK_GRAY).withItalic(true)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to save")))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/build saveatree " + atreeSuffix)))).styled(style -> style.withColor(Formatting.GOLD)));
                    WynnBuild.getConfigManager().getConfig().setAtreeEncoding(atreeSuffix);
                })
                .build();

        query.executeQuery();
    }

    public static class AtreeFetcher {
        private int page = 1;

        protected void processPage(ContainerContent content, int page) {
            List<ItemStack> items = content.items();
            Set<AtreeNode> allPageNodes = new HashSet<>();

            for (int i = 0; i < 54; i++) {
                ItemStack stack = items.get(i);
                if (stack.isEmpty()) continue;
                if (Utils.getLore(stack) == null || Utils.getLore(stack).isEmpty()) continue;

                AtreeNode node = new AtreeNode(stack, i);
                if (node.getId().isEmpty()) {
                    continue;
                }
                allPageNodes.add(node);
            }

            allPageNodes.stream().sorted(Comparator.comparingInt(o -> o.id)).forEach(node -> {
                if (!node.isUnlockedOrUnreachable()) return;
                int id = node.id;

                if (id == 0) {
                    atreeState.add(id);
                }

                List<Integer> parents = Ability.getById(id).parents();
                if (parents.stream().anyMatch(atreeState::contains)) {
                    atreeState.add(id);
                }
            });
        }

        protected void processPage(ContainerContent content) {
            processPage(content, page);
            page++;
        }
    }
}
