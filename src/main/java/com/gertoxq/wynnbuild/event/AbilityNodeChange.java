package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.google.common.collect.Sets;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.models.abilitytree.AbilityTreeModel;
import com.wynntils.models.abilitytree.type.AbilityTreeNodeState;
import com.wynntils.models.abilitytree.type.AbilityTreeNodeType;
import com.wynntils.models.containers.containers.AbilityTreeContainer;
import com.wynntils.utils.mc.LoreUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static com.gertoxq.wynnbuild.WynnBuild.AbilityTree;

public class AbilityNodeChange {

    public static Pattern ABILITY_UNDO_PATTERN = Pattern.compile(".*?\\QRight-Click to undo\\E");

    @SubscribeEvent
    public void clickNode(ContainerClickEvent event) {

        if (!(Models.Container.getCurrentContainer() instanceof AbilityTreeContainer)) return;

        ItemStack clicked = event.getItemStack();

        if (!AbilityTreeModel.ABILITY_TREE_PARSER.isNodeItem(clicked, event.getSlotNum())) return;

        Optional<Ability> nodeAbility = Ability.getFromNodeAt(clicked, event.getSlotNum());

        if (nodeAbility.isEmpty()) {                                                // not stripping unlock text doesn't matter
            WynnBuild.warn("Couldn't find ability for clicked node {} on slot {}", clicked.getName().getString(), event.getSlotNum());
            return;
        }

        Ability ability = nodeAbility.get();

        AbilityTreeNodeState nodeState = AbilityTreeNodeType.fromItemStack(clicked).getState();

        if (nodeState == AbilityTreeNodeState.LOCKED || nodeState == AbilityTreeNodeState.BLOCKED) {
            return;
        }
        if (nodeState == AbilityTreeNodeState.UNLOCKABLE) {
            WynnBuild.AbilityTree.addAbility(ability);
            AbilityTree.saveCache();
            WynnBuild.info("Added ability {} with id {} to atreeState", ability.displayName(), ability.id());
            return;
        }
        // Clicked unlocked node
        if (event.getMouseButton() == 1 && event.getClickType() != SlotActionType.QUICK_MOVE) { // Right click to remove

            boolean canUndo = ABILITY_UNDO_PATTERN.matcher(LoreUtils.getLore(clicked).getLast().getString()).matches();
            if (!canUndo) return;

            // find children that have only this as their parent or have no connection to the root without this
            // or just encode without it and decode so unconnected nodes will be removed

            Set<Integer> pre = Set.copyOf(AbilityTree.getState());

            AbilityTree.removeAbility(ability);
            String encoded = AbilityTree.encode();
            AbilityTree.setFromCode(encoded);
            AbilityTree.saveCache();

            WynnBuild.info("Removed an ability {} with id {}, removed ids: {}", ability.displayName(), ability.id(), Sets.difference(pre, AbilityTree.getState()));

        }
    }
}
