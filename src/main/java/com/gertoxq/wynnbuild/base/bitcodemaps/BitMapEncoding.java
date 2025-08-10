package com.gertoxq.wynnbuild.base.bitcodemaps;

import com.gertoxq.wynnbuild.base.Powder;

import java.util.Arrays;
import java.util.List;

public interface BitMapEncoding {

    default EquimpentKind EQUIPMENT_KIND() {
        return new EquimpentKind();
    }

    default EquipmentPowdersFlag EQUIPMENT_POWDERS_FLAG() {
        return new EquipmentPowdersFlag();
    }

    default int EQUIPMENT_NUM() {
        return 9;
    }

    default int POWDERABLE_EQUIPMENT_NUM() {
        return 5;
    }

    default List<String> POWDER_ELEMENTS() {
        return Arrays.stream(Powder.Element.values()).map(element -> element.name().substring(0, 1)).toList();
    }

    default int POWDER_TIERS() {
        return Powder.MAX_POWDER_LEVEL;
    }

    default int POWDER_WRAPPER_BITLEN() {
        return 2;
    }

    default int POWDER_ID_BITLEN() {
        return 5;
    }

    default PowderRepeatOp POWDER_REPEAT_OP() {
        return new PowderRepeatOp();
    }

    default PowderRepeatTierOp POWDER_REPEAT_TIER_OP() {
        return new PowderRepeatTierOp();
    }

    default PowderChangeOp POWDER_CHANGE_OP() {
        return new PowderChangeOp();
    }

    default TomesFlag TOMES_FLAG() {
        return new TomesFlag();
    }

    default TomeSlotFlag TOME_SLOT_FLAG() {
        return new TomeSlotFlag();
    }

    default int TOME_NUM() {
        return 14;
    }

    default int ASPECT_TIERS() {
        return 4;
    }

    default int NUM_ASPECTS() {
        return 5;
    }

    default int ASPECT_TIER_BITLEN() {
        return 2;
    }

    default AspectsFlag ASPECTS_FLAG() {
        return new AspectsFlag();
    }

    default AspectSlotFlag ASPECT_SLOT_FLAG() {
        return new AspectSlotFlag();
    }

    default int MAX_SP() {
        return 2048;
    }

    default int SP_TYPES() {
        return 5;
    }

    default int MAX_SP_BITLEN() {
        return 12;
    }

    default SpFlag SP_FLAG() {
        return new SpFlag();
    }

    default SpElementFlag SP_ELEMENT_FLAG() {
        return new SpElementFlag();
    }

    default LevelFlag LEVEL_FLAG() {
        return new LevelFlag();
    }

    default int MAX_LEVEL() {
        return 106;
    }

    default int LEVEL_BITLEN() {
        return 7;
    }

    default int ITEM_ID_BITLEN() {
        return 13;
    }

    default int TOME_ID_BITLEN() {
        return 8;
    }

    default int ASPECT_ID_BITLEN() {
        return 5;
    }

    interface BitSized {
        int BITLEN();
    }

    class EquimpentKind implements BitSized {
        public final int NORMAL = 0;
        public final int CRAFTED = 1;
        public final int CUSTOM = 2;

        @Override
        public int BITLEN() {
            return 2;
        }
    }

    class EquipmentPowdersFlag implements BitSized {
        public final int NO_POWDERS = 0;
        public final int HAS_POWDERS = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class PowderRepeatOp implements BitSized {
        public final int REPEAT = 0;
        public final int NO_REPEAT = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class PowderRepeatTierOp implements BitSized {
        public final int REPEAT_TIER = 0;
        public final int CHANGE_POWDER = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class PowderChangeOp implements BitSized {
        public final int NEW_POWDER = 0;
        public final int NEW_ITEM = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class TomesFlag implements BitSized {
        public final int NO_TOMES = 0;
        public final int HAS_TOMES = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class TomeSlotFlag implements BitSized {
        public final int UNUSED = 0;
        public final int USED = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class AspectsFlag implements BitSized {
        public final int NO_ASPECTS = 0;
        public final int HAS_ASPECTS = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class AspectSlotFlag implements BitSized {
        public final int UNUSED = 0;
        public final int USED = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class SpFlag implements BitSized {
        public final int ASSIGNED = 0;
        public final int AUTOMATIC = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class SpElementFlag implements BitSized {
        public final int ELEMENT_UNASSIGNED = 0;
        public final int ELEMENT_ASSIGNED = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }

    class LevelFlag implements BitSized {
        public final int MAX = 0;
        public final int OTHER = 1;

        @Override
        public int BITLEN() {
            return 1;
        }
    }
}
