package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.ClickType;

import javax.annotation.Nullable;
import java.util.Map;

public interface GuiElement {
    @Nullable
    ItemStack render(GuiView view, int slot);

    default ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        return ActionResult.PASS;
    }

    default ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        return ActionResult.PASS;
    }
}
