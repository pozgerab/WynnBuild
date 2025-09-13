package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.bitcodemaps.BaseEncoding;
import com.gertoxq.wynnbuild.base.custom.CustomCoder;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.base.util.EncodingBitVector;
import com.gertoxq.wynnbuild.build.Aspect;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.util.WynnData;
import com.wynntils.core.components.Models;
import com.wynntils.models.elements.type.Powder;
import com.wynntils.models.items.items.game.CraftedGearItem;
import com.wynntils.models.items.items.game.GearItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.gertoxq.wynnbuild.WynnBuild.WYNN_VERSION_ID;
import static com.gertoxq.wynnbuild.base.PowderUtil.MAX_POWDER_LEVEL;
import static com.gertoxq.wynnbuild.util.Utils.mod;


public class EncodeDecode {

    public static final Map<Integer, Integer> POWDERABLES = Map.of(0, 0, 1, 1, 2, 2, 3, 3, 8, 4);
    public static final BaseEncoding ENC = new BaseEncoding();
    static final byte VECTOR_FLAG = 0xC;
    static final int VERSION_BITLEN = 10;
    static final int CUSTOM_STR_LENGTH_BITLEN = 12;

    public static EncodingBitVector encodeHeader(int wynnVersionId) {
        EncodingBitVector headerVec = new EncodingBitVector(0, 0);

        headerVec.append(VECTOR_FLAG, 6);
        headerVec.append(wynnVersionId, VERSION_BITLEN);
        return headerVec;
    }

    public static Map<Powder, Integer> collectPowders(List<Powder> powders) {

        Map<Powder, Integer> countingMap = new HashMap<>();
        for (Powder powder : powders) {
            countingMap.putIfAbsent(powder, 1);
            countingMap.put(powder, countingMap.get(powder) + 1);
        }
        return countingMap;
    }

    public static EncodingBitVector encodePowders(List<Powder> powders) {
        EncodingBitVector powderVec = new EncodingBitVector(0, 0);

        if (powders.isEmpty()) {
            powderVec.appendFlag(ENC.EQUIPMENT_POWDERS_FLAG(), ENC.EQUIPMENT_POWDERS_FLAG().NO_POWDERS);
            return powderVec;
        }

        Map<Powder, Integer> collectedPowders = collectPowders(powders);

        powderVec.appendFlag(ENC.EQUIPMENT_POWDERS_FLAG(), ENC.EQUIPMENT_POWDERS_FLAG().HAS_POWDERS);

        AtomicReference<Integer> previousPowder = new AtomicReference<>(-1);
        collectedPowders.forEach((powder, amount) -> {
            int powderId = PowderUtil.getId(powder, MAX_POWDER_LEVEL);
            if (previousPowder.get() != -1) {
                powderVec.appendFlag(ENC.POWDER_REPEAT_OP(), ENC.POWDER_REPEAT_OP().NO_REPEAT);
                if (powderId % MAX_POWDER_LEVEL == previousPowder.get() % MAX_POWDER_LEVEL) {
                    powderVec.appendFlag(ENC.POWDER_REPEAT_TIER_OP(), ENC.POWDER_REPEAT_TIER_OP().REPEAT_TIER);
                    int elementAmount = ENC.POWDER_ELEMENTS().size();
                    var elementWrapper = mod(PowderUtil.getPowder(powderId).getElement().ordinal() - PowderUtil.getPowder(previousPowder.get()).getElement().ordinal(), elementAmount) - 1;
                    powderVec.append(elementWrapper, ENC.POWDER_WRAPPER_BITLEN());
                } else {
                    powderVec.appendFlag(ENC.POWDER_REPEAT_TIER_OP(), ENC.POWDER_REPEAT_TIER_OP().CHANGE_POWDER);
                    powderVec.appendFlag(ENC.POWDER_CHANGE_OP(), ENC.POWDER_CHANGE_OP().NEW_POWDER);
                    powderVec.append(powderId, ENC.POWDER_ID_BITLEN());
                }
            } else {
                powderVec.append(powderId, ENC.POWDER_ID_BITLEN());
            }
            for (int i = 1; i < amount - 1; i++) {
                powderVec.appendFlag(ENC.POWDER_REPEAT_OP(), ENC.POWDER_REPEAT_OP().REPEAT);
            }
            previousPowder.set(powderId);
        });
        powderVec.appendFlag(ENC.POWDER_REPEAT_OP(), ENC.POWDER_REPEAT_OP().NO_REPEAT);
        powderVec.appendFlag(ENC.POWDER_REPEAT_TIER_OP(), ENC.POWDER_REPEAT_TIER_OP().CHANGE_POWDER);
        powderVec.appendFlag(ENC.POWDER_CHANGE_OP(), ENC.POWDER_CHANGE_OP().NEW_ITEM);

        return powderVec;
    }

    public static EncodingBitVector encodeEquipment(List<ItemStack> equipment, List<List<Powder>> powders, boolean precise) {
        EncodingBitVector equipmentVec = new EncodingBitVector(0, 0);

        for (int idx = 0; idx < equipment.size(); idx++) {

            Optional<GearItem> gearOpt = Models.Item.asWynnItem(equipment.get(idx), GearItem.class);
            GearItem gear = gearOpt.orElse(null);

            Optional<CraftedGearItem> craftedOpt = Models.Item.asWynnItem(equipment.get(idx), CraftedGearItem.class);
            CraftedGearItem crafted = craftedOpt.orElse(null);

            int equipmentKind = crafted != null || (gear != null && precise) ? ENC.EQUIPMENT_KIND().CUSTOM : ENC.EQUIPMENT_KIND().NORMAL;
            equipmentVec.append(equipmentKind, ENC.EQUIPMENT_KIND().BITLEN());

            switch (equipmentKind) {
                case 0 -> {
                    Integer id = 0;
                    if (gear != null) {
                        id = WynnData.getIdMap().get(gear.getName());
                        if (id == null) {
                            WynnBuild.warn("Unknown item: {}", gear.getName());
                            id = 0;
                        } else {
                            id++;
                        }
                    }
                    equipmentVec.append(id, ENC.ITEM_ID_BITLEN());
                }
                case 2 -> {
                    String hash;
                    if (crafted != null) {
                        hash = CustomCoder.encode(crafted).toB64();
                    } else {
                         hash = CustomCoder.encode(gear).toB64();
                    }
                    equipmentVec.append(hash.length(), CUSTOM_STR_LENGTH_BITLEN);
                    equipmentVec.appendB64(hash);
                }
            }

            if (POWDERABLES.containsKey(idx)) {
                equipmentVec.merge(Arrays.asList(new EncodingBitVector[]{encodePowders(powders.get(POWDERABLES.get(idx)))}));
            }
        }

        return equipmentVec;
    }

    public static EncodingBitVector encodeSp(List<Integer> finalSp, List<Integer> assigned) {
        EncodingBitVector spVec = new EncodingBitVector(0, 0);

        if (assigned.stream().allMatch(x -> x == 0)) {
            spVec.appendFlag(ENC.SP_FLAG(), ENC.SP_FLAG().AUTOMATIC);
        } else {
            spVec.appendFlag(ENC.SP_FLAG(), ENC.SP_FLAG().ASSIGNED);

            for (int i = 0; i < finalSp.size(); i++) {
                int sp = finalSp.get(i);

                if (assigned.get(i) == 0) {
                    spVec.appendFlag(ENC.SP_ELEMENT_FLAG(), ENC.SP_ELEMENT_FLAG().ELEMENT_UNASSIGNED);
                } else {
                    spVec.appendFlag(ENC.SP_ELEMENT_FLAG(), ENC.SP_ELEMENT_FLAG().ELEMENT_ASSIGNED);
                    int truncSp = sp & ((1 << ENC.MAX_SP_BITLEN()) - 1);
                    spVec.append(truncSp, ENC.MAX_SP_BITLEN());
                }
            }
        }
        return spVec;
    }

    public static EncodingBitVector encodeLevel(int level) {
        EncodingBitVector levelVec = new EncodingBitVector(0, 0);
        if (level == ENC.MAX_LEVEL()) {
            levelVec.appendFlag(ENC.LEVEL_FLAG(), ENC.LEVEL_FLAG().MAX);
        } else {
            levelVec.appendFlag(ENC.LEVEL_FLAG(), ENC.LEVEL_FLAG().OTHER);
            levelVec.append(level, ENC.LEVEL_BITLEN());
        }
        return levelVec;
    }

    public static EncodingBitVector encodeTomes(List<Integer> tomes) {
        EncodingBitVector tomesVec = new EncodingBitVector(0, 0);
        if (tomes.stream().allMatch(integer -> integer == 0)) {
            tomesVec.appendFlag(ENC.TOMES_FLAG(), ENC.TOMES_FLAG().NO_TOMES);
        } else {
            tomesVec.appendFlag(ENC.TOMES_FLAG(), ENC.TOMES_FLAG().HAS_TOMES);
            for (Integer tomeId : tomes) {
                if (tomeId == null) {
                    tomesVec.appendFlag(ENC.TOME_SLOT_FLAG(), ENC.TOME_SLOT_FLAG().UNUSED);
                } else {
                    tomesVec.appendFlag(ENC.TOME_SLOT_FLAG(), ENC.TOME_SLOT_FLAG().USED);
                    tomesVec.append(tomeId, ENC.TOME_ID_BITLEN());
                }
            }
        }
        return tomesVec;
    }

    public static EncodingBitVector encodeAspects(List<Aspect> aspects) {

        EncodingBitVector aspectVec = new EncodingBitVector(0, 0);

        if (aspects.stream().allMatch(aspect -> aspect.id < 0)) {
            aspectVec.appendFlag(ENC.ASPECTS_FLAG(), ENC.ASPECTS_FLAG().NO_ASPECTS);
        } else {
            aspectVec.appendFlag(ENC.ASPECTS_FLAG(), ENC.ASPECTS_FLAG().HAS_ASPECTS);
            for (Aspect aspect : aspects) {
                if (aspect.id < 0) {
                    aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().UNUSED);
                } else {
                    aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().USED);
                    aspectVec.append(aspect.id, ENC.ASPECT_ID_BITLEN());
                    aspectVec.append(aspect.tier - 1, ENC.ASPECT_TIER_BITLEN());
                }
            }
        }
        return aspectVec;
    }

    public static EncodingBitVector encodeBuild(boolean precise, Build build, List<Integer> finalSkillPoints, List<Integer> assignedSkillpoints, List<Aspect> aspects, Set<Integer> atreeState) {

        EncodingBitVector finalVec = new EncodingBitVector(0, 0);

        List<List<Powder>> powderSet = POWDERABLES.keySet().stream().sorted().map(integer -> {
            ItemStack itemStack = build.equipment.get(integer);
            Optional<GearItem> gearOpt = Models.Item.asWynnItem(itemStack, GearItem.class);
            if (gearOpt.isPresent()) {
                return gearOpt.get().getPowders();
            }
            Optional<CraftedGearItem> craftedOpt = Models.Item.asWynnItem(itemStack, CraftedGearItem.class);
            if (craftedOpt.isPresent()) {
                return craftedOpt.get().getPowders();
            }
            return List.<Powder>of();
        }).toList();

        //  TODO    Tome types dont fit in 4 bits, third-party bug, wait for fix

        BitVector[] vectors = {
                encodeHeader(WYNN_VERSION_ID),
                encodeEquipment(build.equipment, powderSet, precise),
                encodeTomes(build.tomeIDs),
                encodeSp(finalSkillPoints, assignedSkillpoints),
                encodeLevel(build.wynnLevel),
                encodeAspects(aspects),
                AtreeCoder.getAtreeCoder(build.cast).encode_atree(atreeState)
        };

        finalVec.merge(java.util.Arrays.asList(vectors));
        return finalVec;
    }
}
