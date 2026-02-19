package com.github.darksoulq.abyssallib.world.gui.element;

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

/**
 * A GUI element that updates its visual state based on server ticks.
 * <p>
 * This allows for frame-based animations or dynamic item rendering that
 * changes over time.
 */
public class GuiAnimatedItem implements GuiElement {

    /** The function responsible for determining the item to render based on ticks. */
    private final BiFunction<GuiView, Integer, ItemStack> renderer;

    /**
     * Constructs a new GuiAnimatedItem with a custom renderer.
     *
     * @param renderer the animation rendering function
     */
    public GuiAnimatedItem(BiFunction<GuiView, Integer, ItemStack> renderer) {
        this.renderer = renderer;
    }

    /**
     * Renders the item based on the current server tick.
     *
     * @param view the current GUI view
     * @param slot the slot index
     * @return the item stack for the current frame
     */
    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        return renderer.apply(view, Bukkit.getCurrentTick());
    }

    /**
     * Cancels the click action.
     *
     * @param ctx the click context
     * @return {@link ActionResult#CANCEL}
     */
    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        return ActionResult.CANCEL;
    }

    /**
     * Cancels the drag action.
     *
     * @param ctx the drag context
     * @return {@link ActionResult#CANCEL}
     */
    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        return ActionResult.CANCEL;
    }

    /**
     * Static factory method to create a GuiAnimatedItem with a custom renderer.
     *
     * @param renderer the rendering function
     * @return a new GuiAnimatedItem instance
     */
    public static GuiAnimatedItem of(BiFunction<GuiView, Integer, ItemStack> renderer) {
        return new GuiAnimatedItem(renderer);
    }

    /**
     * Creates an animated item from a list of frames.
     *
     * @param frames   the list of item stacks for animation
     * @param interval the tick interval between frame changes
     * @return a new looping GuiAnimatedItem
     */
    public static GuiAnimatedItem of(List<ItemStack> frames, int interval) {
        return of(frames, interval, true);
    }

    /**
     * Creates an animated item from a list of frames.
     *
     * @param frames   the list of item stacks for animation
     * @param interval the tick interval between frame changes
     * @param loop     whether the animation should repeat
     * @return a new GuiAnimatedItem instance
     * @throws IllegalArgumentException if frames list is empty
     */
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