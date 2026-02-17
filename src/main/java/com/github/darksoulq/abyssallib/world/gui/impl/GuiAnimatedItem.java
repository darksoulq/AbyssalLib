package com.github.darksoulq.abyssallib.world.gui.impl;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.world.gui.GuiElement;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public class GuiAnimatedItem implements GuiElement {

    private final BiFunction<GuiView, Integer, ItemStack> renderer;

    public GuiAnimatedItem(BiFunction<GuiView, Integer, ItemStack> renderer) {
        this.renderer = renderer;
    }

    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return renderer.apply(view, Bukkit.getCurrentTick());
    }

    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    public static GuiAnimatedItem of(BiFunction<GuiView, Integer, ItemStack> renderer) {
        return new GuiAnimatedItem(renderer);
    }

    public static GuiAnimatedItem of(List<ItemStack> frames, int interval) {
        return of(frames, interval, true);
    }

    public static GuiAnimatedItem of(List<ItemStack> frames, int interval, boolean loop) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Animation frames cannot be empty");
        }
        return new GuiAnimatedItem((view, tick) -> {
            int safeInterval = Math.max(1, interval);
            long currentFrame = tick / safeInterval;

            if (loop) {
                int index = (int) (currentFrame % frames.size());
                return frames.get(index);
            } else {
                int index = (int) Math.min(currentFrame, frames.size() - 1);
                return frames.get(index);
            }
        });
    }
}