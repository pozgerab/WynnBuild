package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
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

    public Optional<Integer> getId() {
        return Optional.of(ability.id());
    }

    public String getName() {
        return name;
    }

    public boolean isUnlockedOrUnreachable() {

        Text nameText = stack.getName();
        String name = Utils.removeFormat(nameText.getString());
        List<Text> lore = Utils.getLore(stack);

        if (stack.isEmpty()) return false;
        if (lore == null || lore.isEmpty()) return false;
        if (name.startsWith("Unlock ")) return false;
        String lastStr = Utils.removeFormat(lore.getLast().getString());
        return !lastStr.contains("You do not meet the requirements") && !lastStr.contains("Blocked by another ability");
    }
}
