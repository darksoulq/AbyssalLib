package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;

public class GuiAnimatedItem implements GuiElement {
    private final BiFunction<GuiView, Integer, ItemStack> renderer;

    public GuiAnimatedItem(BiFunction<GuiView, Integer, ItemStack> renderer) {
        this.renderer = renderer;
    }

    @Override
    public ItemStack render(GuiView view, int slot) {
        int tick = Bukkit.getCurrentTick();
        return renderer.apply(view, tick);
    }

    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        return ActionResult.CANCEL;
    }

    public static GuiAnimatedItem of(BiFunction<GuiView, Integer, ItemStack> renderer) {
        return new GuiAnimatedItem(renderer);
    }
}
