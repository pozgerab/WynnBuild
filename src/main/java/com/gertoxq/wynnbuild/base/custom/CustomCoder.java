package com.gertoxq.wynnbuild.base.custom;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.util.BootstringEncoder;
import com.gertoxq.wynnbuild.base.util.EncodingBitVector;
import com.gertoxq.wynnbuild.identifications.Data;
import com.gertoxq.wynnbuild.util.WynnData;
import com.gertoxq.wynnbuild.webquery.Providers;
import com.wynntils.core.components.Models;
import com.wynntils.models.elements.type.Element;
import com.wynntils.models.gear.type.*;
import com.wynntils.models.items.items.game.CraftedGearItem;
import com.wynntils.models.items.items.game.GameItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.items.properties.GearTierItemProperty;
import com.wynntils.models.items.properties.GearTypeItemProperty;
import com.wynntils.models.items.properties.NamedItemProperty;
import com.wynntils.models.items.properties.PowderedItemProperty;
import com.wynntils.models.stats.type.DamageType;
import com.wynntils.models.stats.type.StatActualValue;
import com.wynntils.utils.type.Pair;
import com.wynntils.utils.type.RangedValue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gertoxq.wynnbuild.util.Utils.log2;

public class CustomCoder {

    public static final CustomEnc CUSTOM_ENC = new CustomEnc();
    static final BootstringEncoder bootstringEncoder = new BootstringEncoder(0, 1, 52, 104, 700, 38, '-');


    public static <T extends GameItem
            & GearTierItemProperty & GearTypeItemProperty
            & PowderedItemProperty & NamedItemProperty>
    EncodingBitVector encode(
            GearType safeType,
            List<StatActualValue> identifications,
            List<Pair<DamageType, RangedValue>> damages,
            List<Pair<Element, Integer>> defences,
            GearRequirements requirements, int hp, GearAttackSpeed attackSpeed, GearTier tier, T item
    ) {

        EncodingBitVector customVec = new EncodingBitVector(0, 0);
        customVec.append(0, 1);

        customVec.append(CUSTOM_ENC.CUSTOM_ENCODING_VERSION(), CUSTOM_ENC.CUSTOM_VERSION_BITLEN());

        customVec.appendFlag(CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG(), CUSTOM_ENC.CUSTOM_FIXED_IDS_FLAG().FIXED);

        identifications.forEach(statActualValue -> {
            String apiKey = statActualValue.statType().getApiName();
            String builderKey = WynnData.getApiBuilderMap().get(apiKey);
            if (builderKey == null) {
                WynnBuild.warn("Skipping unknown rolled stat: {}", apiKey);
                return;
            }

            int i = Data.ci_save_order.indexOf(builderKey);
            int value = statActualValue.value();

            if (Data.rolledIDs.contains(builderKey)) {
                appendRolled(customVec, statActualValue.statType().calculateAsInverted() ? -value : value, i);
            } else {
                appendInt(customVec, value, i);
                if (!List.of("str", "dex", "int", "def", "agi").contains(builderKey)) {
                    WynnBuild.warn("builderKey {}, apiKey {} is not rolled", builderKey, apiKey);
                }
            }
        });

        appendInt(customVec, item.getPowderSlots(), "slots");
        appendInt(customVec, hp, "hp");
        appendInt(customVec, requirements.level(), "lvl");

        // Defences
        defences.forEach(pair ->
                appendInt(customVec, pair.b(), pair.key().name().toLowerCase().charAt(0) + "Def"));

        // Requirements
        requirements.skills().forEach(pair ->
                appendInt(customVec, pair.b(), pair.key().getApiName().substring(0, 3) + "Req"));

        damages.forEach(pair ->
                appendString(customVec, pair.b().asString(), pair.a().name().toLowerCase().charAt(0) + "Dam"));

        String gearName = item.getName();
        appendString(customVec, gearName, "name");

        int tierBit = Data.gearTiers.indexOf(tier);
        appendIdIdx(customVec, "tier");
        customVec.append(tierBit, CUSTOM_ENC.ITEM_TIER_BITLEN());

        int typeBit = Data.gearTypes.indexOf(item.getGearType());
        appendIdIdx(customVec, "type");
        if (safeType == null) {
            if (item.getGearType().isWeapon()) {
                typeBit = Data.gearTypes.indexOf(GearType.fromClassType(Models.Character.getClassType()));
            }
        } else {
            typeBit = Data.gearTypes.indexOf(safeType);
        }
        customVec.append(typeBit, CUSTOM_ENC.ITEM_TYPE_BITLEN());

        if (attackSpeed != null) {
            int atkSpdBit = 6 - attackSpeed.getEncodingId();
            appendIdIdx(customVec, "atkSpd");
            customVec.append(atkSpdBit, CUSTOM_ENC.ITEM_ATK_SPD_BITLEN());
        }

        requirements.classType().ifPresent(classType -> {
            int classReqBit = Data.classTypes.indexOf(classType);
            appendIdIdx(customVec, "classReq");
            customVec.append(classReqBit, CUSTOM_ENC.ITEM_CLASS_REQ_BITLEN());
        });

        requirements.quest().ifPresent(string -> appendString(customVec, string, "quest"));

        return customVec;
    }

    public static EncodingBitVector encode(GearItem gearItem, GearType safeType) {

        Optional<GearInstance> gearOptional = gearItem.getItemInstance();
        if (gearOptional.isEmpty()) throw new RuntimeException("Somehow does not have instance");
        GearInstance gearInstance = gearOptional.get();

        EncodingBitVector customVec = encode(
                safeType,
                gearInstance.identifications(),
                gearItem.getItemInfo().fixedStats().damages(),
                gearItem.getItemInfo().fixedStats().defences(),
                gearItem.getItemInfo().requirements(),
                gearItem.getItemInfo().fixedStats().healthBuff(),
                gearItem.getItemInfo().fixedStats().attackSpeed().orElse(null),
                gearItem.getGearTier(),
                gearItem
        );

        gearItem.getItemInfo().fixedStats().majorIds().ifPresent(gearMajorId -> {
            String majorId = Providers.MajorIds.data().get(gearMajorId.name());
            if (majorId == null) {
                WynnBuild.warn("Skipping unknown majorId: {}", gearMajorId.name());
            } else {
                appendString(customVec, majorId, "majorIds");
            }
        });

        return customVec;
    }

    public static EncodingBitVector encode(CraftedGearItem craftedItem, GearType safeType) {

        return encode(
                safeType,
                craftedItem.getIdentifications(),
                craftedItem.getDamages(),
                craftedItem.getDefences(),
                craftedItem.getRequirements(),
                craftedItem.getHealth(),
                craftedItem.getAttackSpeed().orElse(null),
                GearTier.NORMAL,
                craftedItem
        );
    }

    private static void appendRolled(EncodingBitVector customVec, int value, int i) {

        if (value == 0) return;
        customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());

        int len = (int) Math.max(1, Math.floor(log2(Math.abs(value))) + 2);
        int idLen = Math.clamp(len, len, 32);
        long mask = (1L << idLen) - 1;
        customVec.append(idLen - 1, CUSTOM_ENC.ID_LENGTH_BITLEN());
        customVec.append(value & mask, idLen);
    }

    private static void appendInt(EncodingBitVector customVec, int value, int i) {
        if (Objects.equals(value, 0)) return;
        customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
        int len = (int) Math.min(32, Math.floor(log2(Math.abs(value))) + 2);
        long mask = (1L << len) - 1;
        customVec.append(len - 1, CUSTOM_ENC.ID_LENGTH_BITLEN());
        customVec.append(value & mask, len);
    }

    private static void appendInt(EncodingBitVector customVec, int value, String builderKey) {
        if (Objects.equals(value, 0)) return;
        int i = Data.ci_save_order.indexOf(builderKey);
        if (i == -1) {
            WynnBuild.warn("Skipping unknown int stat: {}", builderKey);
            return;
        }
        appendInt(customVec, value, i);
    }

    private static void appendString(EncodingBitVector customVec, String value, String builderKey) {
        int i = Data.ci_save_order.indexOf(builderKey);
        if (i == -1) {
            WynnBuild.warn("Skipping unknown string stat: {}", builderKey);
            return;
        }
        if (value.isEmpty()) return;
        if (Data.damages.contains(builderKey) && value.equals("0-0")) return;
        customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
        long lenMask = (1L << CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN()) - 1;
        String encodedText = bootstringEncoder.encode(value);
        customVec.append(encodedText.length() & lenMask, CUSTOM_ENC.TEXT_CHAR_LENGTH_BITLEN());
        customVec.appendB64(encodedText);
    }

    private static void appendIdIdx(EncodingBitVector customVec, String builderKey) {
        int i = Data.ci_save_order.indexOf(builderKey);
        if (i == -1) {
            WynnBuild.warn("Skipping unknown id stat: {}", builderKey);
            return;
        }
        customVec.append(i, CUSTOM_ENC.ID_IDX_BITLEN());
    }
}
