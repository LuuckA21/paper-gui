package me.luucka.papergui.menu;

import lombok.Getter;
import lombok.Setter;
import me.luucka.papergui.PaperGUI;
import me.luucka.papergui.buttons.PGButton;
import me.luucka.papergui.pagination.PGPaginationButtonBuilder;
import me.luucka.papergui.pagination.PGPaginationButtonType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class PGMenu implements InventoryHolder {

    @Getter
    private final JavaPlugin owner;
    private final PaperGUI paperGUI;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int rowsPerPage;

    private final Map<Integer, PGButton> items;
    private final Set<Integer> stickiedSlots;

    @Getter
    private int currentPage;
    @Getter
    @Setter
    private Boolean blockDefaultInteractions;
    @Getter
    @Setter
    private Boolean enableAutomaticPagination;

    @Getter
    @Setter
    private PGPaginationButtonBuilder paginationButtonBuilder;
    @Getter
    @Setter
    private Consumer<PGMenu> onClose;
    @Getter
    @Setter
    private Consumer<PGMenu> onPageChange;

    public PGMenu(JavaPlugin owner, PaperGUI paperGUI, String name, int rowsPerPage) {
        this.owner = owner;
        this.paperGUI = paperGUI;
        this.name = name;
        this.rowsPerPage = rowsPerPage;

        this.items = new HashMap<>();
        this.stickiedSlots = new HashSet<>();

        this.currentPage = 0;
    }

    public int getPageSize() {
        return rowsPerPage * 9;
    }

    public void addButton(PGButton button) {
        if (getHighestFilledSlot() == 0 && getButton(0) == null) {
            setButton(0, button);
            return;
        }
        setButton(getHighestFilledSlot() + 1, button);
    }

    public void addButtons(PGButton... buttons) {
        for (PGButton button : buttons) addButton(button);
    }

    public void setButton(int slot, PGButton button) {
        items.put(slot, button);
    }

    public void setButton(int page, int slot, PGButton button) {
        if (slot < 0 || slot > getPageSize()) return;
        setButton((page * getPageSize()) + slot, button);
    }

    public void removeButton(int slot) {
        items.remove(slot);
    }

    public void removeButton(int page, int slot) {
        if (slot < 0 || slot > getPageSize()) return;
        removeButton((page * getPageSize()) + slot);
    }

    public PGButton getButton(int slot) {
        if (slot < 0 || slot > getHighestFilledSlot()) return null;
        return items.get(slot);
    }

    public PGButton getButton(int page, int slot) {
        if (slot < 0 || slot > getPageSize()) return null;
        return getButton((page * getPageSize()) + slot);
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        if (this.onPageChange != null) this.onPageChange.accept(this);
    }

    public int getMaxPage() {
        return (int) Math.ceil(((double) getHighestFilledSlot() + 1) / ((double) getPageSize()));
    }

    public int getHighestFilledSlot() {
        int slot = 0;
        for (int nextSlot : items.keySet()) {
            if (items.get(nextSlot) != null && nextSlot > slot) slot = nextSlot;
        }
        return slot;
    }

    public void nextPage(HumanEntity viewer) {
        if (currentPage < getMaxPage() - 1) {
            currentPage++;
            refreshInventory(viewer);
            if (this.onPageChange != null) this.onPageChange.accept(this);
        }
    }

    public void previousPage(HumanEntity viewer) {
        if (currentPage > 0) {
            currentPage--;
            refreshInventory(viewer);
            if (this.onPageChange != null) this.onPageChange.accept(this);
        }
    }

    public void stickSlot(int slot) {
        if (slot < 0 || slot >= getPageSize())
            return;

        this.stickiedSlots.add(slot);
    }

    public void unstickSlot(int slot) {
        this.stickiedSlots.remove(slot);
    }

    public void clearStickiedSlots() {
        this.stickiedSlots.clear();
    }

    public boolean isStickiedSlot(int slot) {
        if (slot < 0 || slot >= getPageSize())
            return false;

        return this.stickiedSlots.contains(slot);
    }

    public void clearAllButStickiedSlots() {
        this.currentPage = 0;
        items.entrySet().removeIf(item -> !isStickiedSlot(item.getKey()));
    }

    public void refreshInventory(HumanEntity viewer) {
        // If the open inventory isn't an PGMenu - or if it isn't this inventory, do nothing.
        if (!(viewer.getOpenInventory().getTopInventory().getHolder() instanceof PGMenu)
                || viewer.getOpenInventory().getTopInventory().getHolder() != this
        ) return;

        // If the new size is different, we'll need to open a new inventory.
        if (viewer.getOpenInventory().getTopInventory().getSize() != getPageSize() + (getMaxPage() > 0 ? 9 : 0)) {
            viewer.openInventory(getInventory());
            return;
        }

        // If the name has changed, we'll need to open a new inventory.
        String newName = name.replace("{currentPage}", String.valueOf(currentPage + 1))
                .replace("{maxPage}", String.valueOf(getMaxPage()));

        if (!MiniMessage.miniMessage().serialize(viewer.getOpenInventory().title()).equals(newName)) {
            viewer.openInventory(getInventory());
            return;
        }

        // Otherwise, we can refresh the contents without re-opening the inventory.
        viewer.getOpenInventory().getTopInventory().setContents(getInventory().getContents());
    }

    @Override
    public @NotNull Inventory getInventory() {
        boolean isAutomaticPaginationEnabled = paperGUI.isEnableAutomaticPagination();
        if (getEnableAutomaticPagination() != null) {
            isAutomaticPaginationEnabled = getEnableAutomaticPagination();
        }

        boolean needsPagination = getMaxPage() > 0 && isAutomaticPaginationEnabled;

        Inventory inventory = Bukkit.createInventory(this,
                (needsPagination) ? getPageSize() + 9 : getPageSize(),
                MiniMessage.miniMessage().deserialize(name.replace("{currentPage}", String.valueOf(currentPage + 1))
                        .replace("{maxPage}", String.valueOf(getMaxPage())))
        );

        // Add the main inventory items.
        for (int key = currentPage * getPageSize(); key < (currentPage + 1) * getPageSize(); key++) {
            // If we've already reached the maximum assigned slot, stop assigning
            // slots.
            if (key > getHighestFilledSlot()) break;

            if (items.containsKey(key)) {
                inventory.setItem(key - (currentPage * getPageSize()), items.get(key).getIcon());
            }
        }

        // Update the stickied slots.
        for (int stickiedSlot : stickiedSlots) {
            inventory.setItem(stickiedSlot, items.get(stickiedSlot).getIcon());
        }

        // Render the pagination items.
        if (needsPagination) {
            PGPaginationButtonBuilder paginationButtonBuilder = paperGUI.getDefaultPaginationButtonBuilder();
            if (getPaginationButtonBuilder() != null) {
                paginationButtonBuilder = getPaginationButtonBuilder();
            }

            int pageSize = getPageSize();
            for (int i = pageSize; i < pageSize + 9; i++) {
                int offset = i - pageSize;

                PGButton paginationButton = paginationButtonBuilder.buildPaginationButton(
                        PGPaginationButtonType.forSlot(offset), this
                );
                inventory.setItem(i, paginationButton != null ? paginationButton.getIcon() : null);
            }
        }

        return inventory;
    }
}
