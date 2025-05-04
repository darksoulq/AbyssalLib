package me.darksoul.abyssalLib.event.context;

import me.darksoul.abyssalLib.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiClickContext {
    private final Player player;
    private final InventoryClickEvent event;
    private final ItemStack clickedItem;
    private final int slotIndex;
    private final ClickType clickType;
    private final AbstractGui gui;

    public GuiClickContext(AbstractGui gui, InventoryClickEvent event) {
        this.gui = gui;
        this.event = event;
        this.player = (Player) event.getWhoClicked();
        this.clickedItem = event.getCurrentItem();
        this.slotIndex = event.getSlot();
        this.clickType = event.getClick();
    }

    public Player player() {
        return player;
    }

    public ClickType clickType() {
        return clickType;
    }

    public ItemStack clickedItem() {
        return clickedItem;
    }

    public int slotIndex() {
        return slotIndex;
    }

    public boolean isShiftClick() {
        return event.isShiftClick();
    }

    public boolean isRightClick() {
        return clickType.isRightClick();
    }

    public boolean isLeftClick() {
        return clickType.isLeftClick();
    }

    public boolean isNumberKey() {
        return clickType == ClickType.NUMBER_KEY;
    }

    public int hotbarButton() {
        return event.getHotbarButton();
    }

    public AbstractGui Gui() {
        return gui;
    }

    public void cancel() {
        event.setCancelled(true);
    }

    public InventoryClickEvent event() {
        return event;
    }
}
