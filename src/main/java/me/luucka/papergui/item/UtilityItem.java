package me.luucka.papergui.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class UtilityItem {

    private final Material material;
    private final ItemMeta meta;
    private final int amount;

    public UtilityItem(final Material material) {
        this(material, 1);
    }

    public UtilityItem(final Material material, final int amount) {
        this.material = material;
        this.meta = Bukkit.getItemFactory().getItemMeta(material);
        this.amount = amount <= 0 ? 1 : amount;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.material, this.amount);
        item.setItemMeta(this.meta);
        return item;
    }

    public UtilityItem setDisplayName(final Component name) {
        meta.displayName(name);
        return this;
    }

    public UtilityItem setLore(final List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    public UtilityItem setLore(final Component... lore) {
        return setLore(Arrays.asList(lore));
    }
}
