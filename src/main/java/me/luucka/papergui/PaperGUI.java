package me.luucka.papergui;

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

    private static PaperGUI instance;

    private final JavaPlugin plugin;

    private String prevButtonName = "<green><bold>← Previous Page";
    private final List<String> prevButtonLore = new ArrayList<>() {{
        add("<green>Click to move back to");
        add("<green>page {PAGE}.");
    }};
    private String currentButtonName = "<gray><bold>Page {PAGE} of {MAX_PAGE}";
    private final List<String> currentButtonLore = new ArrayList<>() {{
        add("<gray>You are currently viewing");
        add("<gray>page {PAGE}.");
    }};
    private String nextButtonName = "<green><bold>Next Page →";
    private final List<String> nextButtonLore = new ArrayList<>() {{
        add("<green>Click to move forward to");
        add("<green>page {PAGE}.");
    }};

    private boolean blockDefaultInteractions = true;
    private boolean enableAutomaticPagination = true;

    private final PGPaginationButtonBuilder defaultPaginationButtonBuilder = (type, inventory) -> {
        switch (type) {
            case PREV_BUTTON -> {
                if (inventory.getCurrentPage() > 0) {
                    return new PGButton(new UtilityItem(Material.ARROW)
                            .setDisplayName(MiniMessage.miniMessage().deserialize(
                                    prevButtonName.replace(
                                            "{PAGE}",
                                            String.valueOf(inventory.getCurrentPage())
                                    )
                            ))
                            .setLore(convertLore(prevButtonLore, inventory.getCurrentPage()))
                            .build()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.previousPage(event.getWhoClicked());
                    });
                } else {
                    return null;
                }
            }
            case CURRENT_BUTTON -> {
                return new PGButton(new UtilityItem(Material.NAME_TAG)
                        .setDisplayName(MiniMessage.miniMessage().deserialize(
                                currentButtonName
                                        .replace(
                                                "{PAGE}",
                                                String.valueOf(inventory.getCurrentPage() + 1)
                                        )
                                        .replace(
                                                "{MAX_PAGE}",
                                                String.valueOf(inventory.getMaxPage())
                                        )
                        ))
                        .setLore(convertLore(currentButtonLore, inventory.getCurrentPage() + 1))
                        .build()
                ).withListener(event -> event.setCancelled(true));
            }
            case NEXT_BUTTON -> {
                if (inventory.getCurrentPage() < inventory.getMaxPage() - 1) {
                    return new PGButton(new UtilityItem(Material.ARROW)
                            .setDisplayName(MiniMessage.miniMessage().deserialize(
                                    nextButtonName.replace(
                                            "{PAGE}",
                                            String.valueOf(inventory.getCurrentPage() + 2)
                                    )
                            ))
                            .setLore(convertLore(nextButtonLore, inventory.getCurrentPage() + 2)).build()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.nextPage(event.getWhoClicked());
                    });
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }
    };

    private PaperGUI(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(
                new PGMenuListener(plugin, this), plugin
        );
    }

    public static PaperGUI getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new PaperGUI(plugin);
        }
        return instance;
    }

    public PaperGUI prevButtonName(String prevButtonName) {
        this.prevButtonName = prevButtonName;
        return this;
    }

    public PaperGUI prevButtonLore(List<String> prevButtonLore) {
        this.prevButtonLore.clear();
        this.prevButtonLore.addAll(prevButtonLore);
        return this;
    }

    public PaperGUI currentButtonName(String currentButtonName) {
        this.currentButtonName = currentButtonName;
        return this;
    }

    public PaperGUI currentButtonLore(List<String> currentButtonLore) {
        this.currentButtonLore.clear();
        this.currentButtonLore.addAll(currentButtonLore);
        return this;
    }

    public PaperGUI nextButtonName(String nextButtonName) {
        this.nextButtonName = nextButtonName;
        return this;
    }

    public PaperGUI nextButtonLore(List<String> nextButtonLore) {
        this.nextButtonLore.clear();
        this.nextButtonLore.addAll(nextButtonLore);
        return this;
    }

    public boolean isBlockDefaultInteractions() {
        return blockDefaultInteractions;
    }

    public PaperGUI blockDefaultInteractions(boolean blockDefaultInteractions) {
        this.blockDefaultInteractions = blockDefaultInteractions;
        return this;
    }

    public boolean isEnableAutomaticPagination() {
        return enableAutomaticPagination;
    }

    public PaperGUI enableAutomaticPagination(boolean enableAutomaticPagination) {
        this.enableAutomaticPagination = enableAutomaticPagination;
        return this;
    }

    public PGPaginationButtonBuilder getDefaultPaginationButtonBuilder() {
        return defaultPaginationButtonBuilder;
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
