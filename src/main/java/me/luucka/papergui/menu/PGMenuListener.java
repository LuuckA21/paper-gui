package me.luucka.papergui.menu;

import lombok.RequiredArgsConstructor;
import me.luucka.papergui.PaperGUI;
import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.pagination.PGPaginationButtonBuilder;
import me.luucka.papergui.pagination.PGPaginationButtonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class PGMenuListener implements Listener {

    private final JavaPlugin owner;
    private final PaperGUI paperGUI;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        // Determine if the clicked inventory was a PaperGUI.
        if (event.getClickedInventory() != null
                && event.getClickedInventory().getHolder() != null
                && event.getClickedInventory().getHolder() instanceof PGMenu clickedGui) {

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the PGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!clickedGui.getOwner().equals(owner)) return;

            // If the default action is to cancel the event (block default interactions)
            // we'll do that now.
            // The inventory's value is checked first, so it can be overridden on a
            // per-inventory basis. If the inventory's value is null, the plugin's
            // default value is checked.
            if (clickedGui.getBlockDefaultInteractions() != null) {
                event.setCancelled(clickedGui.getBlockDefaultInteractions());
            } else {
                // Note that this can be overridden by a call to #setCancelled(false) in
                // the button's event handler.
                if (paperGUI.isBlockDefaultInteractions())
                    event.setCancelled(true);
            }

            // If the slot is on the pagination row, get the appropriate pagination handler.
            if (event.getSlot() > clickedGui.getPageSize()) {
                int offset = event.getSlot() - clickedGui.getPageSize();
                PGPaginationButtonBuilder paginationButtonBuilder = paperGUI.getDefaultPaginationButtonBuilder();

                if (clickedGui.getPaginationButtonBuilder() != null) {
                    paginationButtonBuilder = clickedGui.getPaginationButtonBuilder();
                }

                PGPaginationButtonType buttonType = PGPaginationButtonType.forSlot(offset);
                PGButton paginationButton = paginationButtonBuilder.buildPaginationButton(buttonType, clickedGui);
                if (paginationButton != null) paginationButton.getListener().onClick(event);
                return;
            }

            // If the slot is a stickied slot, get the button from page 0.
            if (clickedGui.isStickiedSlot(event.getSlot())) {
                PGButton button = clickedGui.getButton(0, event.getSlot());
                if (button != null && button.getListener() != null) button.getListener().onClick(event);
                return;
            }

            // Otherwise, get the button normally.
            PGButton button = clickedGui.getButton(clickedGui.getCurrentPage(), event.getSlot());
            if (button != null && button.getListener() != null) {
                button.getListener().onClick(event);
            }

        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        // Determine if the inventory was a PaperGUI.
        if (event.getInventory().getHolder() != null
                && event.getInventory().getHolder() instanceof PGMenu clickedGui) {

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the PGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!clickedGui.getOwner().equals(owner)) return;

            // If all the above is true and the inventory's onClose is not null,
            // call it.
            if (clickedGui.getOnClose() != null)
                clickedGui.getOnClose().accept(clickedGui);

        }

    }

}
