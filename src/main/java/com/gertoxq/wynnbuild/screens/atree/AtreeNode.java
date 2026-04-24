package com.gertoxq.wynnbuild.screens.atree;

import com.wynntils.core.text.type.StyleType;
import com.wynntils.utils.wynn.ItemUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Optional;

import static com.gertoxq.wynnbuild.util.Utils.between;

public class AtreeNode {

    final ItemStack stack;
    final String name;
    final Ability ability;

    public AtreeNode(ItemStack stack, int page, int slot) {
        this.stack = stack;
        this.ability = Ability.getByPageAndSlot(page, slot).orElse(null);
        if (ability == null) {
            throw new RuntimeException("Couldn't find ability on page " + page + " and slot " + slot);
        }
        this.name = ability.displayName();
    }

    public AtreeNode(ItemStack stack, Ability ability) {
        this.stack = stack;
        this.ability = ability;
        this.name = ability.displayName();
    }

    public AtreeNode(ItemStack itemStack, int slot) {
        this.stack = itemStack;
        this.name = ItemUtils.getItemName(itemStack).getString(StyleType.NONE)
                .replace("Unlock ", "")
                .replace(" ability", "");
        this.ability = Ability.getByNameSlot(name, slot).orElse(null);
    }

    public static boolean isValidNode(ItemStack stack, int slot) {
        if (stack.isEmpty()) return false;
        if (!stack.getComponents().contains(DataComponentTypes.CUSTOM_MODEL_DATA)
                || stack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA).floats().isEmpty()) return false;
        float customModelData = stack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA).floats().getFirst();
        return stack.getItem() == Items.POTION
                && (between(158, 192, (int) customModelData) || between(218, 272, (int) customModelData))
                && slot < 54;
    }

    public int customModelData() {
        if (stack.isEmpty()) throw new RuntimeException("Node is empty");
        if (!stack.getComponents().contains(DataComponentTypes.CUSTOM_MODEL_DATA)
                || stack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA).floats().isEmpty()) {
            throw new RuntimeException("Node does not have custom model data");
        }
        return stack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA).floats().getFirst().intValue();
    }

    public Optional<Integer> getId() {
        return Optional.of(ability.id());
    }

    public Optional<Ability> getAbility() {
        return Optional.ofNullable(ability);
    }

    public String getName() {
        return name;
    }

    public AbilityNodeState getState() {
        int customModelData = customModelData();

        return AbilityNodeState.getType(customModelData);
    }

    public AbilityNodeType getType() {
        int customModelData = customModelData();

        return AbilityNodeType.getType(customModelData);
    }
}
