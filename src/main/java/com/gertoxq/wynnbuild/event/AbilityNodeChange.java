package com.gertoxq.wynnbuild.event;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.screens.atree.AbilityNodeState;
import com.gertoxq.wynnbuild.screens.atree.AtreeNode;
import com.google.common.collect.Sets;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.models.containers.containers.AbilityTreeContainer;
import com.wynntils.utils.mc.LoreUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class AbilityNodeChange {

    public static Pattern ABILITY_UNDO_PATTERN = Pattern.compile(".*?\\QRight-Click to undo\\E");

    @SubscribeEvent
    public void clickNode(ContainerClickEvent event) {

        if (Models.Container.getCurrentContainer() instanceof AbilityTreeContainer) {

            ItemStack clicked = event.getItemStack();
            if (!AtreeNode.isValidNode(clicked, event.getSlotNum())) return;

            AtreeNode clickedNode = new AtreeNode(clicked, event.getSlotNum());

            Optional<Ability> optionalAbility = clickedNode.getAbility();

            if (optionalAbility.isEmpty()) {
                WynnBuild.warn("Couldn't find ability for clicked node {} on slot {}", clickedNode.getName(), event.getSlotNum());
                return;
            }

            Ability ability = optionalAbility.get();

            AbilityNodeState state = clickedNode.getState();

            if (state == AbilityNodeState.LOCKED || state == AbilityNodeState.BLOCKED) {
                return;
            }
            if (state == AbilityNodeState.UNLOCKABLE) {
                WynnBuild.atreeState.add(ability.id());
                WynnBuild.debug("Added ability {} with id {} to atreeState", ability.displayName(), ability.id());
            } else { // Clicked unlocked node
                if (event.getMouseButton() == 1 && event.getClickType() != SlotActionType.QUICK_MOVE) { // Right click to remove

                    boolean canUndo = ABILITY_UNDO_PATTERN.matcher(LoreUtils.getLore(clicked).getLast().getString()).matches();
                    if (canUndo) {
                        // find children that have only this as their parent or have no connection to the root without this
                        // or just encode without it and decode so unconnected nodes will be removed

                        Set<Integer> pre = Set.copyOf(WynnBuild.atreeState);

                        WynnBuild.atreeState.remove(ability.id());
                        String encoded = WynnBuild.getAtreeCoder().encode_atree(WynnBuild.atreeState).toB64();
                        WynnBuild.atreeState = WynnBuild.getAtreeCoder().decode_atree(encoded);

                        WynnBuild.debug("Removed an ability {} with id {}, removed ids: {}", ability.displayName(), ability.id(), Sets.difference(WynnBuild.atreeState, pre));

                    }
                }
            }
        }
    }
}
