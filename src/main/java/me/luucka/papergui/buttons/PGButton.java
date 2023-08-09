package me.luucka.papergui.buttons;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class PGButton {

    @Getter
    @Setter
    private PGButtonListener listener;

    @Getter
    @Setter
    private ItemStack icon;

    public PGButton(ItemStack icon) {
        this.icon = icon;
    }

    public PGButton withListener(PGButtonListener listener) {
        this.listener = listener;
        return this;
    }
}
