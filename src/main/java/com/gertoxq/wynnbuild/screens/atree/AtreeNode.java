package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.util.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Optional;

public class AtreeNode {

    final ItemStack stack;
    final String name;
    final Ability ability;

    public AtreeNode(ItemStack itemStack, int index, int page) {
        this.stack = itemStack;
        this.name = Utils.removeNum(Utils.removeFormat(itemStack.getName().getString()
                .replace("Unlock ", "")
                .replace(" ability", "").trim()));
        this.ability = Ability.getAbilityByPageSlot(page, index).orElse(null);
    }

    public AtreeNode(ItemStack itemStack, int slot) {
        this.stack = itemStack;
        this.name = Utils.removeNum(Utils.removeFormat(itemStack.getName().getString()
                .replace("Unlock ", "")
                .replace(" ability", "").trim()));
        this.ability = Ability.getByNameSlot(name, slot).orElse(null);
    }

    public static boolean isValidNode(ItemStack stack, int slot) {
        if (stack.isEmpty()) return false;
        return stack.getItem() == Items.POTION && slot < 54;
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
        float customModelDataFloat = stack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA).floats().getFirst();
        int customModelData = (int) customModelDataFloat;

        return AbilityNodeState.getType(customModelData);
    }
}
