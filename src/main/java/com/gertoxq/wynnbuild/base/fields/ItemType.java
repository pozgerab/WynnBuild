package com.gertoxq.wynnbuild.base.fields;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.util.Range;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.models.character.type.ClassType;

import java.util.List;

public enum ItemType {
    Helmet,
    Chestplate,
    Leggings,
    Boots,
    Ring,
    Bracelet,
    Necklace,
    Wand,
    Spear,
    Bow,
    Dagger,
    Relik,
    Potion,
    Scroll,
    Food,
    Tome;

    public static final List<ItemType> ARMORS = List.of(Helmet, Chestplate, Leggings, Boots);
    public static final List<ItemType> WEAPONS = List.of(Wand, Spear, Bow, Dagger, Relik);
    public static final List<ItemType> ACCESSORIES = List.of(Ring, Bracelet, Necklace);
    public static final List<ItemType> CONSUMABLES = List.of(Potion, Scroll, Food);
    public static final List<ItemType> BUILD_ORDER = List.of(Helmet, Chestplate, Leggings, Boots, Ring, Ring, Bracelet, Necklace);

    public static ItemType getFromCustomModelData(int customModelData) {
        for (Range range : WynnData.getModelToType().keySet()) {
            if (range.contains(customModelData)) {
                return WynnData.getModelToType().get(range);
            }
        }
        WynnBuild.warn("This custom model data does not match any type: " + customModelData);
        return null;
    }

    public ClassType getCast() {
        return Cast.findByWeapon(this);
    }

    public boolean isWeapon() {
        return WEAPONS.contains(this);
    }

    public boolean isArmor() {
        return ARMORS.contains(this);
    }

    public boolean isAccessory() {
        return ACCESSORIES.contains(this);
    }

    public boolean isConsumable() {
        return CONSUMABLES.contains(this);
    }
}
