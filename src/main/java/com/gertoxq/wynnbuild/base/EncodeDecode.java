package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.base.custom.Custom;
import com.gertoxq.wynnbuild.base.fields.ItemType;
import com.gertoxq.wynnbuild.base.fields.Tier;
import com.gertoxq.wynnbuild.base.sp.SP;
import com.gertoxq.wynnbuild.base.sp.SkillpointList;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.base.util.EncodingBitVector;
import com.gertoxq.wynnbuild.build.Aspect;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.identifications.IDs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.gertoxq.wynnbuild.WynnBuild.ENC;
import static com.gertoxq.wynnbuild.WynnBuild.WYNN_VERSION_ID;
import static com.gertoxq.wynnbuild.base.Powder.MAX_POWDER_LEVEL;
import static com.gertoxq.wynnbuild.util.Utils.mod;
import static com.gertoxq.wynnbuild.util.Utils.zip2;


public class EncodeDecode {

    public static final Map<Integer, Integer> POWDERABLES = Map.of(0, 0, 1, 1, 2, 2, 3, 3, 8, 4);
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

        AtomicReference<Powder> previousPowder = new AtomicReference<>(Powder.EMPTY_POWDER);
        collectedPowders.forEach((powder, amount) -> {
            if (previousPowder.get() != Powder.EMPTY_POWDER) {
                powderVec.appendFlag(ENC.POWDER_REPEAT_OP(), ENC.POWDER_REPEAT_OP().NO_REPEAT);
                if (powder.id % MAX_POWDER_LEVEL == previousPowder.get().id % MAX_POWDER_LEVEL) {
                    powderVec.appendFlag(ENC.POWDER_REPEAT_TIER_OP(), ENC.POWDER_REPEAT_TIER_OP().REPEAT_TIER);
                    int elementAmount = ENC.POWDER_ELEMENTS().size();
                    var elementWrapper = mod(powder.element.ordinal() - previousPowder.get().element.ordinal(), elementAmount) - 1;
                    powderVec.append(elementWrapper, ENC.POWDER_WRAPPER_BITLEN());
                } else {
                    powderVec.appendFlag(ENC.POWDER_REPEAT_TIER_OP(), ENC.POWDER_REPEAT_TIER_OP().CHANGE_POWDER);
                    powderVec.appendFlag(ENC.POWDER_CHANGE_OP(), ENC.POWDER_CHANGE_OP().NEW_POWDER);
                    powderVec.append(powder.id, ENC.POWDER_ID_BITLEN());
                }
            } else {
                powderVec.append(powder.id, ENC.POWDER_ID_BITLEN());
            }
            for (int i = 1; i < amount - 1; i++) {
                powderVec.appendFlag(ENC.POWDER_REPEAT_OP(), ENC.POWDER_REPEAT_OP().REPEAT);
            }
            previousPowder.set(powder);
        });
        powderVec.appendFlag(ENC.POWDER_REPEAT_OP(), ENC.POWDER_REPEAT_OP().NO_REPEAT);
        powderVec.appendFlag(ENC.POWDER_REPEAT_TIER_OP(), ENC.POWDER_REPEAT_TIER_OP().CHANGE_POWDER);
        powderVec.appendFlag(ENC.POWDER_CHANGE_OP(), ENC.POWDER_CHANGE_OP().NEW_ITEM);

        return powderVec;
    }

    public static EncodingBitVector encodeEquipment(List<Custom> equipment, List<List<Powder>> powders, boolean precise) {
        EncodingBitVector equipmentVec = new EncodingBitVector(0, 0);

        for (int idx = 0; idx < equipment.size(); idx++) {

            Custom eq = equipment.get(idx);
            if (idx < 8) {
                eq.statMap.set(IDs.TYPE, ItemType.BUILD_ORDER.get(idx));
            }

            if (eq.statMap.get(IDs.TIER) == Tier.Crafted) {
                eq.statMap.set(IDs.TIER, Tier.Normal); // wynnbuilder does not support custom crafted items
            }

            int equipmentKind = !eq.statMap.get(IDs.NONE) && (eq.statMap.get(IDs.CUSTOM) || eq.statMap.get(IDs.CRAFTED) || precise) ? ENC.EQUIPMENT_KIND().CUSTOM : ENC.EQUIPMENT_KIND().NORMAL;
            equipmentVec.append(equipmentKind, ENC.EQUIPMENT_KIND().BITLEN());

            switch (equipmentKind) {
                case 0 -> {
                    int id = 0;
                    if (!eq.statMap.get(IDs.NONE)) {
                        assert eq.getBaseItemId().isPresent() && eq.getBaseItemId().get() > 0 : "Base item ID is not present for non-custom item";
                        id = eq.getBaseItemId().get() + 1;
                    }
                    equipmentVec.append(id, ENC.ITEM_ID_BITLEN());
                }
                case 2 -> {
                    String hash = eq.encodeCustom(true).toB64();
                    equipmentVec.append(hash.length(), CUSTOM_STR_LENGTH_BITLEN);
                    equipmentVec.appendB64(hash);
                }
            }

            if (POWDERABLES.containsKey(idx)) {
                equipmentVec.merge(java.util.Arrays.asList(new EncodingBitVector[]{encodePowders(equipmentKind == 0 ? powders.get(POWDERABLES.get(idx)) : List.of())}));
            }
        }

        return equipmentVec;
    }

    public static EncodingBitVector encodeSp(List<Integer> finalSp, List<Integer> originalSp) {
        List<Integer> spDeltas = zip2(finalSp, originalSp).stream().map(pair -> pair.getKey() - pair.getValue()).toList();
        EncodingBitVector spVec = new EncodingBitVector(0, 0);

        if (spDeltas.stream().allMatch(x -> x == 0)) {
            spVec.appendFlag(ENC.SP_FLAG(), ENC.SP_FLAG().AUTOMATIC);
        } else {
            spVec.appendFlag(ENC.SP_FLAG(), ENC.SP_FLAG().ASSIGNED);

            for (int i = 0; i < finalSp.size(); i++) {
                int sp = finalSp.get(i);

                if (spDeltas.get(i) == 0) {
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

    public static EncodingBitVector encodeTomes(List<Custom> tomes) {
        EncodingBitVector tomesVec = new EncodingBitVector(0, 0);
        if (tomes.stream().allMatch(tome -> tome.statMap.hasId(IDs.NONE))) {
            tomesVec.appendFlag(ENC.TOMES_FLAG(), ENC.TOMES_FLAG().NO_TOMES);
        } else {
            tomesVec.appendFlag(ENC.TOMES_FLAG(), ENC.TOMES_FLAG().HAS_TOMES);
            for (Custom tome : tomes) {
                if (tome.statMap.get(IDs.NONE)) {
                    tomesVec.appendFlag(ENC.TOME_SLOT_FLAG(), ENC.TOME_SLOT_FLAG().UNUSED);
                } else {
                    tomesVec.appendFlag(ENC.TOME_SLOT_FLAG(), ENC.TOME_SLOT_FLAG().USED);
                    tomesVec.append(tome.statMap.get(IDs.ID), ENC.TOME_ID_BITLEN());
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

    public static EncodingBitVector encodeBuild(boolean precise, Build build, SkillpointList skillpoints, List<Aspect> aspects, Set<Integer> atreeState) {

        EncodingBitVector finalVec = new EncodingBitVector(0, 0);

        List<List<Powder>> powderSet = POWDERABLES.keySet().stream().sorted().map(integer -> build.equipment.get(integer).getPowders()).toList();

        //  TODO    Tome types dont fit in 4 bits, third-party bug, wait for fix
        List<Custom> tomes = build.tomeIDs.stream().map(integer -> {
            StatMap statMap = new StatMap();
            if (integer < 0) {
                statMap.set(IDs.NONE, true);
            } else {
                statMap.set(IDs.ID, integer);
            }
            return new Custom(statMap);
        }).toList();

        BitVector[] vectors = {
                encodeHeader(WYNN_VERSION_ID),
                encodeEquipment(build.equipment, powderSet, precise),
                encodeTomes(tomes),
                encodeSp(skillpoints, SP.calculateFinalSp(build.equipment)),
                encodeLevel(build.wynnLevel),
                encodeAspects(aspects),
                AtreeCoder.getAtreeCoder(build.cast).encode_atree(atreeState)
        };

        finalVec.merge(java.util.Arrays.asList(vectors));
        return finalVec;
    }
}
