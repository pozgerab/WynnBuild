package com.gertoxq.wynnbuild.build;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.EncodeDecode;
import com.wynntils.core.components.Models;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.models.items.items.game.CraftedGearItem;
import com.wynntils.models.items.items.game.GearItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

public class Build {

    public static final List<String> PRECISION_OPTIONS = List.of("OFF", "ON");
    public static final List<Text> PRECISION_TOOLTIPS = List.of(
            Text.literal("The item is passed as a default item unless it's a crafted or custom (average rolls always)"),
            Text.literal("The item is passed as a custom item (uses your exact rolls, most precision)"));

    public final List<ItemStack> equipment;
    public final List<Integer> totalSkillpoints;
    public final List<Integer> assignedSkillpoints;
    public final List<Integer> tomeIDs;
    public final int wynnLevel;
    public final List<AspectItem> aspects;
    public final ClassType cast;
    final boolean precise;
    final Set<Integer> atreeState;

    public Build(List<ItemStack> equipment, boolean precise, List<Integer> totalSp, List<Integer> assignedSkillpoints, int wynnLevel, List<Integer> tomeIDs, Set<Integer> atreeState, List<AspectItem> aspects) {

        this.equipment = equipment;
        this.precise = precise;
        this.wynnLevel = wynnLevel;
        this.totalSkillpoints = totalSp;
        this.assignedSkillpoints = assignedSkillpoints;
        this.tomeIDs = tomeIDs;
        this.atreeState = atreeState;
        this.aspects = aspects;
        this.cast = Models.Character.getClassType();
    }

    public String generateUrl() {

        String hash = EncodeDecode.encodeBuild(precise, this, totalSkillpoints, assignedSkillpoints, aspects, atreeState).toB64();

        return WynnBuild.BUILDER_DOMAIN + hash;
    }

    public void display() {
        WynnBuild.message(createBuild().finalText());
    }

    public BuildState createBuild() {

        return new BuildState(
                generateUrl(),
                WynnBuild.atreeState.size(),
                !WynnBuild.atreeState.isEmpty(),
                equipment.stream().map(itemStack ->
                        Models.Item.asWynnItem(itemStack, GearItem.class).isPresent()
                                || Models.Item.asWynnItem(itemStack, CraftedGearItem.class).isPresent()).toList()
        );
    }

}
