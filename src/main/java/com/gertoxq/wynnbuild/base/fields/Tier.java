package com.gertoxq.wynnbuild.base.fields;

import net.minecraft.util.Formatting;

public enum Tier {
    //  ORDER MATTERS
    Normal("§f", Formatting.WHITE),
    Unique("§e", Formatting.YELLOW),
    Rare("§d", Formatting.LIGHT_PURPLE),
    Legendary("§b", Formatting.AQUA),
    Fabled("§c", Formatting.RED),
    Mythic("§5", Formatting.DARK_PURPLE),
    Set("§a", Formatting.GREEN),
    Crafted("§3", Formatting.DARK_AQUA);
    public final String color;
    public final Formatting format;

    Tier(String color, Formatting format) {
        this.color = color;
        this.format = format;
    }
}
