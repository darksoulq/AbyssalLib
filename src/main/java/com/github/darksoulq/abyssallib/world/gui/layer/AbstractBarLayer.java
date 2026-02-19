package com.github.darksoulq.abyssallib.world.gui.layer;

import com.github.darksoulq.abyssallib.common.util.StructureArray;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiLayer;
import com.github.darksoulq.abyssallib.world.gui.GuiView;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemName;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ModelData;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An abstract foundation for multi-slot progress bars using Custom Model Data.
 * <p>
 * This layer manages a "structure" of items across multiple slots. It calculates how
 * many slots should be "filled" based on a 0-100 percentage and updates individual
 * item states using a custom model data string property: {@code bar_progress=[state]}.
 */
public abstract class AbstractBarLayer implements GuiLayer {

    /** The top-left starting position of the bar in the inventory. */
    protected final SlotPosition origin;

    /** The arrangement of item templates that form the visual bar. */
    protected final StructureArray<Item> structure;

    /** The number of visual increments per single slot/item. */
    protected final int progressMax;

    /** Internal GUI elements representing each piece of the bar. */
    protected final GuiItem[] guiItems;

    /** Precomputed slot positions for each segment of the bar. */
    protected final SlotPosition[] slots;

    /** The order in which slots are filled (e.g., Left-to-Right, Top-to-Bottom). */
    protected final int[] fillOrder;

    /** Cache of the previous state for each slot to minimize unnecessary updates. */
    protected final int[] lastStates;

    /** Custom string-based metadata injected into the Custom Model Data. */
    protected final Map<String, String> customStates = new LinkedHashMap<>();

    /** Current progress percentage (0.0 to 100.0). */
    protected float progress = -1;

    /** The absolute fill value calculated from the last render. */
    protected int lastFilled = -1;

    /** Custom display name for the bar segments. */
    protected Component customName;

    /** Cache of the last rendered name. */
    protected Component lastName;

    /**
     * Internal constructor to initialize the bar's structural metadata.
     *
     * @param origin      The starting position.
     * @param structure   The item layout.
     * @param orientation The filling direction.
     * @param progressMax Increments per slot.
     */
    protected AbstractBarLayer(SlotPosition origin, StructureArray<Item> structure, StructureArray.Orientation orientation, int progressMax) {
        this.origin = origin;
        this.structure = structure;
        this.progressMax = progressMax;
        this.fillOrder = structure.iterationOrder(orientation);

        int size = structure.size();
        this.guiItems = new GuiItem[size];
        this.slots = new SlotPosition[size];
        this.lastStates = new int[size];

        Item[] raw = structure.toFlatArray();
        for (int i = 0; i < size; i++) {
            guiItems[i] = GuiItem.of(raw[i].clone().getStack());
            lastStates[i] = -1;
        }

        precomputeSlots();
    }

    /**
     * Maps the 1D structure array indices to 2D inventory slots relative to the origin.
     */
    private void precomputeSlots() {
        int width = structure.width();
        for (int i = 0; i < structure.size(); i++) {
            int x = i % width;
            int y = i / width;
            int offset = y * 9 + x;
            slots[i] = new SlotPosition(origin.segment(), origin.index() + offset);
        }
    }

    /**
     * Sets the fill percentage of the bar.
     *
     * @param progress A float between 0 and 100.
     */
    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(100, progress));
    }

    /**
     * Sets a custom display name for all item segments in the bar.
     *
     * @param name The text component to display on hover.
     */
    public void setName(Component name) {
        this.customName = name;
    }

    /**
     * Recalculates and renders the bar's state to the GUI.
     * <p>
     * Only updates slots whose specific progress state or name has changed.
     *
     * @param view The active GUI view.
     */
    @Override
    public void renderTo(GuiView view) {
        int totalStates = structure.size() * progressMax;
        int filled = Math.round((progress / 100f) * totalStates);
        boolean nameChanged = !Objects.equals(customName, lastName);

        if (filled == lastFilled && !nameChanged) return;

        lastFilled = filled;
        lastName = customName;

        Gui gui = view.getGui();

        for (int step = 0; step < fillOrder.length; step++) {
            int index = fillOrder[step];
            int state = Math.min(progressMax, Math.max(0, filled - step * progressMax));

            if (lastStates[index] == state && !nameChanged) continue;

            lastStates[index] = state;
            applyState(index, state);
            gui.getElements().put(slots[index], guiItems[index]);
        }
    }

    /**
     * Removes the bar from the GUI and clears cache.
     *
     * @param view The active view.
     */
    @Override
    public void cleanup(GuiView view) {
        Inventory inv = origin.segment() == GuiView.Segment.TOP ? view.getTop() : view.getBottom();
        for (int i = 0; i < slots.length; i++) {
            view.getGui().getElements().remove(slots[i]);
            inv.setItem(slots[i].index(), null);
            lastStates[i] = -1;
        }
        lastFilled = -1;
    }

    /**
     * Injects state data into an item's Custom Model Data.
     * <p>
     * Updates {@code bar_progress} and any user-defined {@code customStates}
     * within the item's string list metadata.
     *
     * @param index The structure index.
     * @param state The local progress state (0 to progressMax).
     * @return The modified Item.
     */
    protected Item applyState(int index, int state) {
        Item item = Item.resolve(guiItems[index].render(null, 1));
        if (item == null) return null;

        if (customName != null) {
            item.setData(new ItemName(customName));
        }

        List<String> current = new ArrayList<>();
        ModelData modelDataComponent = item.getData(ModelData.TYPE);
        CustomModelData data = modelDataComponent != null ? modelDataComponent.getValue() : null;

        if (data != null && data.strings() != null) {
            current.addAll(data.strings());
        }

        for (String key : customStates.keySet()) {
            current.removeIf(s -> s.startsWith(key + "="));
            current.add(key + "=" + customStates.get(key));
        }

        current.removeIf(s -> s.startsWith("bar_progress="));
        current.add("bar_progress=" + state);

        List<Float> floats = data != null ? data.floats() : null;
        List<Boolean> flags = data != null ? data.flags() : null;
        List<Color> colors = data != null ? data.colors() : null;

        item.setData(new ModelData(CustomModelData.customModelData()
            .addFlags(flags)
            .addColors(colors)
            .addFloats(floats)
            .addStrings(current).build()));
        return item;
    }
}