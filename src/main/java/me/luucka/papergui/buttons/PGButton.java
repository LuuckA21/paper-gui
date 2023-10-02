package me.luucka.papergui.buttons;

import org.bukkit.inventory.ItemStack;

public final class PGButton {

    private PGButtonListener listener;

    private ItemStack icon;

    public PGButton(ItemStack icon) {
        this.icon = icon;
    }

    public PGButton withListener(PGButtonListener listener) {
        this.listener = listener;
        return this;
    }

    public PGButtonListener getListener() {
        return listener;
    }

    public void setListener(PGButtonListener listener) {
        this.listener = listener;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
}
