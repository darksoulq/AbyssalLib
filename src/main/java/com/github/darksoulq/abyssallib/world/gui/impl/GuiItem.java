package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GuiItem implements GuiElement {

    private final ItemStack item;

    public GuiItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return item;
    }

    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    public static GuiItem of(ItemStack item) {
        return new GuiItem(item);
    }
}