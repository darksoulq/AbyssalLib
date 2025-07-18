package com.github.darksoulq.abyssallib.world.level.inventory.gui.impl;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.GuiView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

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

    public static GuiAnimatedItem of(BiFunction<GuiView, Integer, ItemStack> renderer) {
        return new GuiAnimatedItem(renderer);
    }
}
