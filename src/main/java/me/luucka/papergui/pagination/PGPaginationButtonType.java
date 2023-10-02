package me.luucka.papergui.pagination;

import java.util.Arrays;

public enum PGPaginationButtonType {

    PREV_BUTTON(3),
    CURRENT_BUTTON(4),
    NEXT_BUTTON(5),
    UNASSIGNED(0);

    private final int slot;

    PGPaginationButtonType(int slot) {
        this.slot = slot;
    }

    public static PGPaginationButtonType forSlot(int slot) {
        return Arrays.stream(values())
                .filter(buttonType -> buttonType.slot == slot)
                .findFirst()
                .orElse(UNASSIGNED);
    }
}
