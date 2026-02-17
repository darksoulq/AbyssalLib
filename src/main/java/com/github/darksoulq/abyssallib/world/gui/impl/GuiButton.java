package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GuiButton implements GuiElement {

    private final ItemStack item;
    private final Consumer<GuiClickContext> action;

    public GuiButton(ItemStack item, Consumer<GuiClickContext> action) {
        this.item = item;
        this.action = action;
    }

    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return item;
    }

    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        action.accept(ctx);
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    public static GuiButton of(ItemStack item, Consumer<GuiClickContext> action) {
        return new GuiButton(item, action);
    }
}