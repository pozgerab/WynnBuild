package com.gertoxq.wynnbuild.screens.atree;

import com.gertoxq.wynnbuild.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class AtreeNode {

    public final Integer id;
    final Slot slot;
    final ItemStack stack;
    final String name;


    public AtreeNode(Slot slot) {
        this.slot = slot;
        this.stack = slot.getStack();
        this.name = Utils.removeNum(Utils.removeFormat(stack.getName().getString()
                .replace("Unlock ", "")
                .replace(" ability", "").trim()));
        this.id = Ability.getIdByNameAndSlot(name, slot.getIndex()).orElse(null);
    }

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }

    public Slot getSlot() {
        return slot;
    }

    public ItemStack getStack() {
        return stack;
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
