package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.scheduler.ScheduledTask;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.menu.AbstractContainerMenu;
import com.github.darksoulq.abyssallib.world.menu.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GuiEvents {

    @SubscribeEvent(ignoreCancelled = false)
    public void onDropPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            GuiView view = GuiManager.OPEN_VIEWS.get(player.getOpenInventory());
            if (view == null) return;
            if (view.getGui().hasFlag(GuiFlag.DISABLE_ITEM_PICKUP)) event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onCriterionGrant(PlayerAdvancementCriterionGrantEvent event) {
        GuiView view = GuiManager.OPEN_VIEWS.get(event.getPlayer().getOpenInventory());
        if (view == null) return;
        if (view.getGui().hasFlag(GuiFlag.DISABLE_ADVANCEMENTS)) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onClick(InventoryClickEvent event) {
        GuiView view = GuiManager.OPEN_VIEWS.get(event.getView());
        if (view == null) return;

        int rawSlot = event.getRawSlot();
        boolean top = rawSlot < view.getTop().getSize();
        GuiView.Segment segment = top ? GuiView.Segment.TOP : GuiView.Segment.BOTTOM;
        if (top && view.getGui().hasFlag(GuiFlag.DISABLE_TOP)) return;
        if (!top && view.getGui().hasFlag(GuiFlag.DISABLE_BOTTOM)) return;
        int slot = event.getSlot();

        GuiClickContext ctx = new GuiClickContext(event.getWhoClicked(), view, event.getClickedInventory(), event.getCurrentItem(),
            event.getCursor(), slot, event.getRawSlot(), event.getClick(), event.getAction(), event.getSlotType(), event.getHotbarButton());

        GuiElement element = view.getElementAt(segment, slot);
        event.setCancelled(element == null || element.onClick(ctx) == ActionResult.CANCEL);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onDrag(InventoryDragEvent event) {
        GuiView view = GuiManager.OPEN_VIEWS.get(event.getView());
        if (view == null) return;

        for (int rawSlot : event.getRawSlots()) {
            boolean top = rawSlot < view.getTop().getSize();
            GuiView.Segment segment = top ? GuiView.Segment.TOP : GuiView.Segment.BOTTOM;
            if (top && view.getGui().hasFlag(GuiFlag.DISABLE_TOP)) continue;
            if (!top && view.getGui().hasFlag(GuiFlag.DISABLE_BOTTOM)) continue;
            int slot = view.getInventoryView().convertSlot(rawSlot);

            GuiDragContext ctx = new GuiDragContext(event.getWhoClicked(), view, event.getCursor(), event.getOldCursor(), event.getType(),
                event.getNewItems(), event.getRawSlots(), event.getInventorySlots());

            GuiElement el = view.getElementAt(segment, slot);
            event.setCancelled(el == null || el.onDrag(ctx) == ActionResult.CANCEL);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onClose(InventoryCloseEvent event) {
        GuiView view = GuiManager.OPEN_VIEWS.remove(event.getView());
        if (view != null) {
            ScheduledTask task = GuiManager.TICK_VIEWS.remove(view);
            if (task != null) task.cancel();
            view.getGui().getOnClose().accept(view);
        }

        if (!(event.getPlayer() instanceof Player)) return;
        MenuManager.removeMenu(event.getView());
    }

    @SubscribeEvent
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbstractContainerMenu menu = MenuManager.getMenu(event.getView());
        if (menu == null) return;

        event.setCancelled(true);

        if (!menu.stillValid(player)) {
            player.closeInventory();
            return;
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot == -999) {
            menu.clicked(-1, event.getClick(), player);
        } else if (rawSlot >= 0 && rawSlot < menu.slots.size()) {
            if (event.getClick() == ClickType.NUMBER_KEY) {
                menu.hotbarSwap(rawSlot, event.getHotbarButton(), player);
            } else if (event.getClick() == ClickType.SWAP_OFFHAND) {
                menu.offhandSwap(rawSlot, player);
            } else {
                menu.clicked(rawSlot, event.getClick(), player);
            }
        }

        menu.broadcastChanges();

        AbyssalLib.SCHEDULER.schedule(() -> {
            if (player.isOnline()) {
                player.setItemOnCursor(menu.getCarried(player));
                player.updateInventory();
            }
        }).after(1, Clock.TICKS).once();
    }

    @SubscribeEvent
    public void onMenuDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        AbstractContainerMenu menu = MenuManager.getMenu(event.getView());
        if (menu != null) {
            event.setCancelled(true);
            menu.dragged(player, event.getType(), event.getRawSlots());
            menu.broadcastChanges();

            AbyssalLib.SCHEDULER.schedule(() -> {
                if (player.isOnline()) {
                    player.setItemOnCursor(menu.getCarried(player));
                    player.updateInventory();
                }
            }).after(1, Clock.TICKS).once();
        }
    }

    @SubscribeEvent
    public void onQuit(PlayerQuitEvent event) {
        MenuManager.removeMenu(event.getPlayer().getOpenInventory());
    }
}
