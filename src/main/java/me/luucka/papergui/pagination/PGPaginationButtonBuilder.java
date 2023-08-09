package me.luucka.papergui.pagination;

import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.menu.PGMenu;

public interface PGPaginationButtonBuilder {

    PGButton buildPaginationButton(PGPaginationButtonType type, PGMenu inventory);

}
