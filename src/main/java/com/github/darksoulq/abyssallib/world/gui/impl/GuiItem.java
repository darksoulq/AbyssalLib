package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GuiItem implements GuiElement {
    private final ItemStack item;

    public GuiItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack render(GuiView view, int slot) {
        return item;
    }

    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        return ActionResult.CANCEL;
    }

    public static GuiItem of(ItemStack item) {
        return new GuiItem(item);
    }
}
