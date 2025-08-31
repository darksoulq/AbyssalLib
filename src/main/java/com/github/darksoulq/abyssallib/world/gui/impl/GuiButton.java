package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public class GuiButton implements GuiElement {
    private final ItemStack item;
    private final BiConsumer<GuiView, ClickType> onClick;

    public GuiButton(ItemStack item, BiConsumer<GuiView, ClickType> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    @Override
    public ItemStack render(GuiView view, int slot) {
        return item;
    }

    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, ItemStack cursor, ItemStack current) {
        onClick.accept(view, click);
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        return ActionResult.CANCEL;
    }

    public static GuiButton of(ItemStack item, BiConsumer<GuiView, ClickType> onClick) {
        return new GuiButton(item, onClick);
    }
}
