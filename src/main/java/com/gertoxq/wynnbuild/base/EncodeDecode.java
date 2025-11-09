package com.gertoxq.wynnbuild.base;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.bitcodemaps.BaseEncoding;
import com.gertoxq.wynnbuild.base.custom.CustomCoder;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.base.util.EncodingBitVector;
import com.gertoxq.wynnbuild.build.AtreeCoder;
import com.gertoxq.wynnbuild.build.Build;
import com.gertoxq.wynnbuild.screens.aspect.AspectInfo;
import com.gertoxq.wynnbuild.webquery.Providers;
import com.wynntils.core.components.Models;
import com.wynntils.models.elements.type.Powder;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.models.items.items.game.AspectItem;
import com.wynntils.models.items.items.game.CraftedGearItem;
import com.wynntils.models.items.items.game.GearItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.gertoxq.wynnbuild.WynnBuild.WYNN_VERSION_ID;
import static com.gertoxq.wynnbuild.base.PowderUtil.MAX_POWDER_LEVEL;
import static com.gertoxq.wynnbuild.util.Utils.mod;
import static com.gertoxq.wynnbuild.webquery.BuilderDataManager.WYNN_VERSION_ID;


public class EncodeDecode {

    public static final Map<Integer, Integer> POWDERABLES = Map.of(0, 0, 1, 1, 2, 2, 3, 3, 8, 4);
    public static final List<GearType> EQUIPMENT_ORDER = List.of(GearType.HELMET, GearType.CHESTPLATE, GearType.LEGGINGS, GearType.BOOTS, GearType.RING, GearType.RING, GearType.BRACELET, GearType.NECKLACE);
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
            int powderId = PowderUtil.getId(powder, WynnBuild.getConfig().getDefaultPowderLevel());
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

            int equipmentKind;
            if (gearOpt.isPresent() && gearOpt.get().getItemInfo().metaInfo().preIdentified()) {
                equipmentKind = ENC.EQUIPMENT_KIND().NORMAL;
            } else if (craftedOpt.isPresent() || (gearOpt.isPresent() && precise)) {
                equipmentKind = ENC.EQUIPMENT_KIND().CUSTOM;
            } else {
                equipmentKind = ENC.EQUIPMENT_KIND().NORMAL;
            }
            equipmentVec.append(equipmentKind, ENC.EQUIPMENT_KIND().BITLEN());

            switch (equipmentKind) {
                case 0 -> {
                    Integer id = 0;
                    if (gear != null) {
                        id = Providers.Items.data().get(gear.getName());
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
                    GearType safeType = idx < 8 ? EQUIPMENT_ORDER.get(idx) : null;
                    if (crafted != null) {
                        hash = CustomCoder.encode(crafted, safeType).toB64();
                    } else {
                        hash = CustomCoder.encode(gear, safeType).toB64();
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

    public static EncodingBitVector encodeTomes(@Nullable List<@NotNull Integer> tomes) {
        EncodingBitVector tomesVec = new EncodingBitVector(0, 0);
        if (tomes == null) {
            tomesVec.appendFlag(ENC.TOMES_FLAG(), ENC.TOMES_FLAG().NO_TOMES);
        } else {
            tomesVec.appendFlag(ENC.TOMES_FLAG(), ENC.TOMES_FLAG().HAS_TOMES);
            for (Integer tomeId : tomes) {
                if (tomeId == -1) {
                    tomesVec.appendFlag(ENC.TOME_SLOT_FLAG(), ENC.TOME_SLOT_FLAG().UNUSED);
                } else {
                    tomesVec.appendFlag(ENC.TOME_SLOT_FLAG(), ENC.TOME_SLOT_FLAG().USED);
                    tomesVec.append(tomeId, ENC.TOME_ID_BITLEN());
                }
            }
        }
        return tomesVec;
    }

    public static EncodingBitVector encodeAspects(@Nullable List<@Nullable AspectItem> aspects) {

        EncodingBitVector aspectVec = new EncodingBitVector(0, 0);

        if (aspects == null) {
            aspectVec.appendFlag(ENC.ASPECTS_FLAG(), ENC.ASPECTS_FLAG().NO_ASPECTS);
        } else {
            aspectVec.appendFlag(ENC.ASPECTS_FLAG(), ENC.ASPECTS_FLAG().HAS_ASPECTS);
            for (AspectItem aspect : aspects) {
                if (aspect == null) {
                    aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().UNUSED);
                } else {
                    Integer id = AspectInfo.aspectMap.get(aspect.getName());
                    if (id == null) {
                        WynnBuild.warn("Unknown aspect: {}", aspect.getName());
                        aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().UNUSED);
                        continue;
                    }
                    aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().USED);
                    aspectVec.append(id, ENC.ASPECT_ID_BITLEN());
                    aspectVec.append(aspect.getTier() - 1, ENC.ASPECT_TIER_BITLEN());
                }
            }
        }
        return aspectVec;
    }

    public static EncodingBitVector encodeBuild(boolean precise, Build build, List<Integer> finalSkillPoints, List<Integer> assignedSkillpoints, List<AspectItem> aspects, Set<Integer> atreeState) {

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
