package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiFlag;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiView;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiEvents {

    @SubscribeEvent
    public void onDropPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            GuiView view = GuiManager.openViews.get(player.getOpenInventory());
            if (view == null) return;
            if (!view.getGui().hasFlag(GuiFlag.DISABLE_BOTTOM)) event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onCriterionGrant(PlayerAdvancementCriterionGrantEvent event) {
        GuiView view = GuiManager.openViews.get(event.getPlayer().getOpenInventory());
        if (view == null) return;
        System.out.println(view.getGui().getFlags());
        if (view.getGui().hasFlag(GuiFlag.DISABLE_ADVANCEMENTS)) {
            event.setCancelled(true);
            AbyssalLib.getInstance().getLogger().info(String.valueOf(event.isCancelled()));
        };
    }

    @SubscribeEvent
    public void onClick(InventoryClickEvent event) {
        GuiView view = GuiManager.openViews.get(event.getView());
        if (view == null) return;

        int rawSlot = event.getRawSlot();
        boolean top = rawSlot < view.getTop().getSize();
        GuiView.Segment segment = top ? GuiView.Segment.TOP : GuiView.Segment.BOTTOM;
        if (top && view.getGui().hasFlag(GuiFlag.DISABLE_TOP)) return;
        if (!top && view.getGui().hasFlag(GuiFlag.DISABLE_BOTTOM)) return;
        int slot = event.getSlot();

        GuiElement element = view.getElementAt(segment, slot);
        if (element == null) {
            event.setCancelled(true);
            return;
        }

        ActionResult result = element.onClick(view, slot, event.getClick(), event.getCursor(), event.getCurrentItem());
        event.setCancelled(result == ActionResult.CANCEL);
    }

    @SubscribeEvent
    public void onDrag(InventoryDragEvent event) {
        GuiView view = GuiManager.openViews.get(event.getView());
        if (view == null) return;

        boolean cancel = false;
        for (int rawSlot : event.getRawSlots()) {
            boolean top = rawSlot < view.getTop().getSize();
            GuiView.Segment segment = top ? GuiView.Segment.TOP : GuiView.Segment.BOTTOM;
            if (top && view.getGui().hasFlag(GuiFlag.DISABLE_TOP)) continue;
            if (!top && view.getGui().hasFlag(GuiFlag.DISABLE_BOTTOM)) continue;
            int slot = view.getInventoryView().convertSlot(rawSlot);

            GuiElement el = view.getElementAt(segment, slot);
            if (el == null || el.onDrag(view, event.getNewItems()) == ActionResult.CANCEL) {
                cancel = true;
            }
        }

        event.setCancelled(cancel);
    }

    @SubscribeEvent
    public void onClose(InventoryCloseEvent event) {
        GuiView view = GuiManager.openViews.remove(event.getView());
        if (view != null) {
            view.getGui().getOnClose().accept(view);
        }
    }
}
