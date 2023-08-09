package me.luucka.papergui.pagination;

import lombok.Getter;

public enum PGPaginationButtonType {

    PREV_BUTTON(3),
    CURRENT_BUTTON(4),
    NEXT_BUTTON(5),
    UNASSIGNED(0);

    @Getter
    private final int slot;

    PGPaginationButtonType(int slot) {
        this.slot = slot;
    }

    public static PGPaginationButtonType forSlot(int slot) {
        for (PGPaginationButtonType buttonType : PGPaginationButtonType.values()) {
            if (buttonType.slot == slot) return buttonType;
        }

        return PGPaginationButtonType.UNASSIGNED;
    }
}
