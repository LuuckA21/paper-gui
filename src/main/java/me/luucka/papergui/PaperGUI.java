package me.luucka.papergui;

import lombok.Getter;
import lombok.Setter;
import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.item.UtilityItem;
import me.luucka.papergui.menu.PGMenu;
import me.luucka.papergui.menu.PGMenuListener;
import me.luucka.papergui.pagination.PGPaginationButtonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class PaperGUI {

    private final JavaPlugin plugin;

    @Setter
    private String prevButtonName = "<green><bold>← Previous Page";
    @Setter
    private List<String> prevButtonLore = List.of("<green>Click to move back to", "<green>page {PAGE}.");

    @Setter
    private String currentButtonName = "<gray><bold>Page {PAGE} of {MAX_PAGE}";
    @Setter
    private List<String> currentButtonLore = List.of("<gray>You are currently viewing", "<gray>page {PAGE}.");

    @Setter
    private String nextButtonName = "<green><bold>Next Page →";
    @Setter
    private List<String> nextButtonLore = List.of("<green>Click to move forward to", "<green>page {PAGE}.");

    @Getter
    @Setter
    private boolean blockDefaultInteractions = true;
    @Getter
    @Setter
    private boolean enableAutomaticPagination = true;
    @Getter
    @Setter
    private PGPaginationButtonBuilder defaultPaginationButtonBuilder = (type, inventory) -> {
        switch (type) {
            case PREV_BUTTON -> {
                if (inventory.getCurrentPage() > 0) return new PGButton(new UtilityItem(Material.ARROW)
                        .setDisplayName(MiniMessage.miniMessage().deserialize(prevButtonName.replace("{PAGE}", String.valueOf(inventory.getCurrentPage()))))
                        .setLore(convertLore(prevButtonLore, inventory.getCurrentPage()))
                        .toItemStack()
                ).withListener(event -> {
                    event.setCancelled(true);
                    inventory.previousPage(event.getWhoClicked());
                });
                else return null;
            }
            case CURRENT_BUTTON -> {
                return new PGButton(new UtilityItem(Material.NAME_TAG)
                        .setDisplayName(MiniMessage.miniMessage().deserialize(currentButtonName.replace("{PAGE}", String.valueOf(inventory.getCurrentPage() + 1)).replace("{MAX_PAGE}", String.valueOf(inventory.getMaxPage()))))
                        .setLore(convertLore(currentButtonLore, inventory.getCurrentPage() + 1))
                        .toItemStack()
                ).withListener(event -> event.setCancelled(true));
            }
            case NEXT_BUTTON -> {
                if (inventory.getCurrentPage() < inventory.getMaxPage() - 1)
                    return new PGButton(new UtilityItem(Material.ARROW)
                            .setDisplayName(MiniMessage.miniMessage().deserialize(nextButtonName.replace("{PAGE}", String.valueOf(inventory.getCurrentPage() + 2))))
                            .setLore(convertLore(nextButtonLore, inventory.getCurrentPage() + 2)).toItemStack()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.nextPage(event.getWhoClicked());
                    });
                else return null;
            }
            default -> {
                return null;
            }
        }
    };

    public PaperGUI(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(
                new PGMenuListener(plugin, this), plugin
        );
    }

    public PGMenu create(String name, int rows) {
        return new PGMenu(plugin, this, name, rows);
    }

    private List<Component> convertLore(List<String> lore, int page) {
        List<Component> components = new ArrayList<>();
        for (String s : lore) {
            components.add(MiniMessage.miniMessage().deserialize(s.replace("{PAGE}", String.valueOf(page))));
        }
        return components;
    }

}
