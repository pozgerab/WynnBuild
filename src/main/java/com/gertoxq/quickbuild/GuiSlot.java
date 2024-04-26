package com.gertoxq.quickbuild;

public enum GuiSlot {
    ATREE_UP(59),
    ATREE_DOWN(57),
    ATREE_BACK(63),
    CI_OPEN_ATREE(9);
    final public int slot;
    GuiSlot(int slot) {
        this.slot = slot;
    }
}
