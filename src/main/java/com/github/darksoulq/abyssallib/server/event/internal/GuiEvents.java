package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiEvents {

    @SubscribeEvent(ignoreCancelled = false)
    public void onDropPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            GuiView view = GuiManager.openViews.get(player.getOpenInventory());
            if (view == null) return;
            if (view.getGui().hasFlag(GuiFlag.DISABLE_ITEM_PICKUP)) event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onCriterionGrant(PlayerAdvancementCriterionGrantEvent event) {
        GuiView view = GuiManager.openViews.get(event.getPlayer().getOpenInventory());
        if (view == null) return;
        if (view.getGui().hasFlag(GuiFlag.DISABLE_ADVANCEMENTS)) {
            event.setCancelled(true);
        };
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onClick(InventoryClickEvent event) {
        GuiView view = GuiManager.openViews.get(event.getView());
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
        GuiView view = GuiManager.openViews.get(event.getView());
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
        GuiView view = GuiManager.openViews.remove(event.getView());
        if (view != null) {
            view.getGui().getOnClose().accept(view);
        }
    }
}
