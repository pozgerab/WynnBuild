package com.gertoxq.wynnbuild.build;

import com.gertoxq.wynnbuild.WynnBuild;
import com.gertoxq.wynnbuild.base.PowderUtil;
import com.gertoxq.wynnbuild.base.bitcodemaps.BaseEncoding;
import com.gertoxq.wynnbuild.base.custom.CustomCoder;
import com.gertoxq.wynnbuild.base.sp.Skillpoint;
import com.gertoxq.wynnbuild.base.util.BitVector;
import com.gertoxq.wynnbuild.base.util.BitVectorCursor;
import com.gertoxq.wynnbuild.base.util.EncodingBitVector;
import com.gertoxq.wynnbuild.screens.atree.Ability;
import com.gertoxq.wynnbuild.webquery.Providers;
import com.wynntils.core.components.Models;
import com.wynntils.models.abilitytree.type.SavableAbilityTree;
import com.wynntils.models.aspects.type.SavableAspectSet;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.character.type.SavableGear;
import com.wynntils.models.character.type.SavableTome;
import com.wynntils.models.character.type.SavableTomeSet;
import com.wynntils.models.containers.containers.AspectsContainer;
import com.wynntils.models.elements.type.Powder;
import com.wynntils.models.elements.type.Skill;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.models.items.WynnItem;
import com.wynntils.models.items.items.game.CraftedGearItem;
import com.wynntils.models.items.items.game.GearItem;
import com.wynntils.models.items.properties.LeveledItemProperty;
import com.wynntils.models.rewards.type.TomeType;
import com.wynntils.models.stats.type.*;
import com.wynntils.services.loadout.type.Loadout;
import com.wynntils.utils.EncodedByteBuffer;
import com.wynntils.utils.type.ErrorOr;
import com.wynntils.utils.type.Pair;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.gertoxq.wynnbuild.WynnBuild.*;
import static com.gertoxq.wynnbuild.base.PowderUtil.MAX_POWDER_LEVEL;
import static com.gertoxq.wynnbuild.util.Utils.mod;
import static com.gertoxq.wynnbuild.webquery.BuilderDataManager.WYNN_VERSION_ID;

import java.util.List;

public class Build {

    public static final List<String> PRECISION_OPTIONS = List.of("OFF", "ON");
    public static final List<Text> PRECISION_TOOLTIPS = List.of(
            Text.literal("The item is passed as a default item unless it's a crafted or custom (average rolls always)"),
            Text.literal("The item is passed as a custom item (uses your exact rolls, most precision)"));
    public static final Map<Integer, Integer> POWDERABLES = Map.of(0, 0, 1, 1, 2, 2, 3, 3, 8, 4);
    public static final List<GearType> EQUIPMENT_ORDER = List.of(GearType.HELMET, GearType.CHESTPLATE, GearType.LEGGINGS, GearType.BOOTS, GearType.RING, GearType.RING, GearType.BRACELET, GearType.NECKLACE);
    public static final BaseEncoding ENC = new BaseEncoding();
    static final byte VECTOR_FLAG = 0xC;
    static final int VERSION_BITLEN = 10;
    static final int CUSTOM_STR_LENGTH_BITLEN = 12;

    public static final EquipmentSegment Equipment = new EquipmentSegment();
    public static final SkillPointsSegment SkillPoints = new SkillPointsSegment();
    public static final TomeSegment Tome = new TomeSegment();
    public static final LevelSegment Level = new LevelSegment();
    public static final AspectSegment Aspect = new AspectSegment();
    public static final VersionSegment Version = new VersionSegment();
    public static final AbilityTreeSegment AbilityTree = new AbilityTreeSegment();

    private final List<WynnItem> equipment;
    private final SavableTomeSet tomes;
    private final int level;
    private final SavableAspectSet aspects;
    private final SavableAbilityTree abilityTree;
    private final List<Integer> assigned;
    private final List<Integer> finalSp;
    private final boolean precise;

    private Build(List<WynnItem> equipment, boolean precise, List<Integer> finalSp, List<Integer> assignedSp, int level, SavableTomeSet tomes, SavableAbilityTree abilityTree, SavableAspectSet aspects) {
        this.equipment = equipment;
        this.level = level;
        this.tomes = tomes;
        this.abilityTree = abilityTree;
        this.aspects = aspects;
        this.assigned = assignedSp;
        this.finalSp = finalSp;
        this.precise = precise;
    }

    public static Build current() {

        return new Build(
                Models.Inventory.getEquippedItems().stream().map(itemStack -> Models.Item.getWynnItem(itemStack).orElse(null)).toList(),
                getConfig().getPrecision() == 1,
                Arrays.stream(Skill.values()).map(Skillpoint::getTotalSkillpoints).toList(),
                Arrays.stream(Skill.values()).map(Models.SkillPoint::getAssignedSkillPoints).toList(),
                Models.CharacterStats.getLevel(),
                getConfig().isIncludeTomes() ? Models.Character.getCurrentTomeSet() : new SavableTomeSet(new ArrayList<>()),
                new SavableAbilityTree(Models.AbilityTree.getUnlockedAbilities(), Models.Character.getClassType()),
                getConfig().isIncludeAspects() ? Aspect.getAspects() : new SavableAspectSet(new ArrayList<>(), Models.Character.getClassType())
        );
    }

    public static Build fromLoadout(Loadout loadout, boolean precise) {

        List<SavableGear> gears = new ArrayList<>();
        gears.addAll(loadout.skillPoints().armourNames());
        gears.addAll(loadout.skillPoints().accessoryNames());
        gears.add(loadout.skillPoints().weapon());
        List<WynnItem> items = gears.stream().map(savableGear -> EquipmentSegment.fromSavable(savableGear).orElse(null)).toList();

        List<Integer> assigned = Arrays.stream(loadout.skillPoints().getSkillPointsAsArray()).boxed().toList();
        EnumMap<Skill, Integer> gearSkillPoints = new EnumMap<>(Skill.class);

        items.forEach(wynnItem -> {
            List<StatActualValue> identifications;
            if (wynnItem instanceof GearItem gear) {
                identifications = gear.getIdentifications();
            } else if (wynnItem instanceof CraftedGearItem crafted) {
                identifications = crafted.getIdentifications();
            } else return;

            identifications.forEach(statValue -> {
                if (!(statValue.statType() instanceof SkillStatType statType)) return;
                gearSkillPoints.merge(statType.getSkill(), statValue.value(), Integer::sum);
            });
        });

        List<Integer> totalSp = new ArrayList<>();
        for (Skill skill : Skill.values()) {
            totalSp.add(assigned.get(skill.ordinal()) + gearSkillPoints.getOrDefault(skill, 0));
        }

        Optional<Integer> maxLevel = items.stream().map(wynnItem -> {
            if (wynnItem instanceof LeveledItemProperty leveled) {
                return leveled.getLevel();
            }
            return 0;
        }).max(Comparator.naturalOrder());

        return new Build(
                items,
                precise,
                totalSp,
                assigned,
                Math.max(loadout.getMaxLevel(), maxLevel.orElse(0)),
                loadout.tomes(),
                loadout.abilityTree(),
                loadout.aspects()
        );
    }

    public EncodingBitVector encodeBuild() {

        List<BitVector> vectors = List.of(
                Version.encode(WYNN_VERSION_ID),
                Equipment.encode(equipment, precise),
                Tome.encode(tomes),
                SkillPoints.encode(finalSp, assigned),
                Level.encode(level),
                Aspect.encode(aspects),
                AbilityTree.encode(abilityTree)
        );

        EncodingBitVector finalVec = new EncodingBitVector(0, 0);
        finalVec.merge(vectors);
        return finalVec;
    }

    public String generateUrl() {

        String hash = encodeBuild().toB64();

        return WynnBuild.BUILDER_DOMAIN + hash;
    }

    public void display() {
        WynnBuild.message(createBuild().finalText());
    }

    public BuildState createBuild() {

        return new BuildState(
                generateUrl(),
                abilityTree.abilities().size(),
                !abilityTree.abilities().isEmpty(),
                equipment.stream().map(wynnitem ->
                        wynnitem instanceof GearItem || wynnitem instanceof CraftedGearItem).toList()
        );
    }

    public static class AspectSegment {

        public SavableAspectSet getAspects() {

            List<String> aspectNames = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Optional<String> iasp = Models.Aspect.getEquippedAspect(i);
                if (iasp.isEmpty()) break;

                aspectNames.add(iasp.get());
            }
            return new SavableAspectSet(aspectNames, Models.Character.getClassType());
        }

        public List<Pair<Integer, Integer>> transform(SavableAspectSet aspects) {
            List<Pair<Integer, Integer>> idAndTier = new ArrayList<>();
            for (String aspect : aspects.aspectNames()) {

                Optional<Integer> tier = Models.Aspect.getAspectTierByName(aspect);
                if (tier.isEmpty())
                    throw new RuntimeException("Equipped aspect couldn't be found in owned aspects: " + aspect);

                Integer id = Providers.Aspects.getClassAspects(aspects.classType()).get(aspect);
                if (id == null) {
                    WynnBuild.error("Cannot find aspect: " + aspect + " in class: " + aspects.classType().getName());
                    continue;
                }
                idAndTier.add(new Pair<>(id, tier.get()));

            }

            return idAndTier;
        }

        public EncodingBitVector encode(SavableAspectSet aspectSet) {

            List<Pair<Integer, Integer>> aspects = transform(aspectSet);

            EncodingBitVector aspectVec = new EncodingBitVector(0, 0);

            if (aspects.isEmpty()) {
                aspectVec.appendFlag(ENC.ASPECTS_FLAG(), ENC.ASPECTS_FLAG().NO_ASPECTS);
                return aspectVec;
            }

            aspectVec.appendFlag(ENC.ASPECTS_FLAG(), ENC.ASPECTS_FLAG().HAS_ASPECTS);

            for (int i = 0; i < AspectsContainer.getEquippedSlots().size(); i++) {

                if (i >= aspects.size()) {
                    aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().UNUSED);
                    continue;
                }

                Pair<Integer, Integer> aspect = aspects.get(i);

                aspectVec.appendFlag(ENC.ASPECT_SLOT_FLAG(), ENC.ASPECT_SLOT_FLAG().USED);
                aspectVec.append(aspect.a(), ENC.ASPECT_ID_BITLEN());
                aspectVec.append(aspect.b() - 1, ENC.ASPECT_TIER_BITLEN());
            }

            return aspectVec;
        }

    }

    public static class TomeSegment {

        public static final List<TomeType> TOME_ORDER = List.of(
                TomeType.WEAPON_TOME,
                TomeType.ARMOUR_TOME,
                TomeType.GUILD_TOME,
                TomeType.LOOTRUN_TOME,
                TomeType.MARATHON_TOME,
                TomeType.MYSTICISM_TOME,
                TomeType.EXPERTISE_TOME);

        public static int tomeAmount(TomeType type) {
            return switch (type) {
                case WEAPON_TOME, MARATHON_TOME, MYSTICISM_TOME, EXPERTISE_TOME -> 2;
                case ARMOUR_TOME -> 4;
                case GUILD_TOME, LOOTRUN_TOME -> 1;
            };
        }

        public static int emptyTomeId(TomeType type) {
            return switch (type) {
                case WEAPON_TOME -> 61;
                case ARMOUR_TOME -> 62;
                case GUILD_TOME -> 63;
                case LOOTRUN_TOME -> 93;
                case MARATHON_TOME -> 162;
                case MYSTICISM_TOME -> 163;
                case EXPERTISE_TOME -> 164;
            };
        }

        public List<@NotNull Integer> transform(SavableTomeSet tomes) {
            List<Integer> ids = new ArrayList<>();
            for (TomeType type : TOME_ORDER) {
                List<SavableTome> typeTomes = tomes.getTomes(type);

                for (SavableTome tome : typeTomes) {
                    ids.add(Providers.Tomes.data().get(tome.itemName()));
                }

                // append remaining empty tome ids
                for (int i = 0; i < tomeAmount(type) - typeTomes.size(); i++) {
                    ids.add(emptyTomeId(type));
                }

            }
            return ids;
        }

        public EncodingBitVector encode(SavableTomeSet tomeSet) {

            List<@NotNull Integer> tomes = transform(tomeSet);

            EncodingBitVector tomesVec = new EncodingBitVector(0, 0);
            if (tomes.isEmpty()) {
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

    }

    public static class LevelSegment {

        public EncodingBitVector encode(int level) {
            EncodingBitVector levelVec = new EncodingBitVector(0, 0);
            if (level == ENC.MAX_LEVEL()) {
                levelVec.appendFlag(ENC.LEVEL_FLAG(), ENC.LEVEL_FLAG().MAX);
            } else {
                levelVec.appendFlag(ENC.LEVEL_FLAG(), ENC.LEVEL_FLAG().OTHER);
                levelVec.append(level, ENC.LEVEL_BITLEN());
            }
            return levelVec;
        }

    }

    public static class VersionSegment {

        public EncodingBitVector encode(int version) {
            EncodingBitVector headerVec = new EncodingBitVector(0, 0);

            headerVec.append(VECTOR_FLAG, 6);
            headerVec.append(version, VERSION_BITLEN);
            return headerVec;
        }

    }

    public static class EquipmentSegment {

        public static Map<Powder, Integer> collectPowders(List<Powder> powders) {

            Map<Powder, Integer> countingMap = new HashMap<>();
            for (Powder powder : powders) {
                countingMap.putIfAbsent(powder, 1);
                countingMap.put(powder, countingMap.get(powder) + 1);
            }
            return countingMap;
        }

        private static Optional<WynnItem> fromSavable(SavableGear savableGear) {
             ErrorOr<WynnItem> errItem = Models.ItemEncoding.decodeItem(EncodedByteBuffer.fromBase64String(savableGear.encoded()), savableGear.itemName());
             if (errItem.hasError()) return Optional.empty();

             WynnItem item = errItem.getValue();
             if (!(item instanceof GearItem) && !(item instanceof CraftedGearItem)) return Optional.empty();
             return Optional.of(item);
        }

        public EncodingBitVector encodePowders(List<Powder> powders) {
            EncodingBitVector powderVec = new EncodingBitVector(0, 0);

            if (powders.isEmpty()) {
                powderVec.appendFlag(ENC.EQUIPMENT_POWDERS_FLAG(), ENC.EQUIPMENT_POWDERS_FLAG().NO_POWDERS);
                return powderVec;
            }

            Map<Powder, Integer> collectedPowders = collectPowders(powders);

            powderVec.appendFlag(ENC.EQUIPMENT_POWDERS_FLAG(), ENC.EQUIPMENT_POWDERS_FLAG().HAS_POWDERS);

            AtomicReference<Integer> previousPowder = new AtomicReference<>(-1);
            collectedPowders.forEach((powder, amount) -> {
                int powderId = PowderUtil.getId(powder, getConfig().getDefaultPowderLevel());
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

        public List<List<Powder>> getPowders(List<WynnItem> items) {
            return POWDERABLES.keySet().stream().sorted().map(integer -> {
                WynnItem item = items.get(integer);
                if (item instanceof GearItem gearItem) {
                    return gearItem.getPowders();
                }
                if (item instanceof CraftedGearItem craftedGearItem) {
                    return craftedGearItem.getPowders();
                }
                return List.<Powder>of();
            }).toList();
        }

        public EncodingBitVector encode(List<@Nullable WynnItem> gearItems, boolean precise) {
            if (gearItems.size() != 9) {
                throw new IllegalArgumentException("gearItems.size() != 9");
            }
            EncodingBitVector equipmentVec = new EncodingBitVector(0, 0);

            for (int idx = 0; idx < gearItems.size(); idx++) {

                WynnItem item = gearItems.get(idx);
                GearItem gear = item instanceof GearItem ? (GearItem) item : null;
                CraftedGearItem crafted = item instanceof CraftedGearItem ? (CraftedGearItem) item : null;

                int equipmentKind;
                if (gear != null && gear.getItemInfo().metaInfo().preIdentified()) {
                    equipmentKind = ENC.EQUIPMENT_KIND().NORMAL;
                } else if (crafted != null || (gear != null && precise)) {
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
                    equipmentVec.merge(Arrays.asList(new EncodingBitVector[]{encodePowders(getPowders(gearItems).get(POWDERABLES.get(idx)))}));
                }
            }

            return equipmentVec;
        }

    }

    public static class AbilityTreeSegment {

        public Set<Integer> transform(SavableAbilityTree tree) {
            return tree.abilities().stream()
                    .map(ability_name ->
                            Ability.idFromNode(Models.AbilityTree.getNodeFromNameAndClass(ability_name, tree.classType()), tree.classType()))
                    .collect(Collectors.toSet());
        }

        public BitVector encode(SavableAbilityTree abilityTree) {
            Set<Integer> state = transform(abilityTree);

            return AtreeCoder.getAtreeCoder(abilityTree.classType()).encode_atree(state);
        }

        public SavableAbilityTree decode(String tree, ClassType classType) {
            AtreeCoder classCoder = AtreeCoder.getAtreeCoder(classType);
            Set<Integer> nodeIds = classCoder.decode_atree(tree);
            Set<Integer> validIds = classCoder.validate(nodeIds);
            if (validIds.size() != nodeIds.size()) {
                WynnBuild.displayErr("The ability code was invalid, a valid cropped version was imported.");
            }
            List<String> abilityNames = validIds.stream().map(id -> Ability.getById(id, classType).displayName()).toList();
            return new SavableAbilityTree(abilityNames, classType);
        }

    }

    public static class SkillPointsSegment {

        public BitVector encode(List<Integer> finalSp, List<Integer> assigned) {

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

    }

}
