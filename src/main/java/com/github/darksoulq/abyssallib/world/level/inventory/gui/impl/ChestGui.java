package com.github.darksoulq.abyssallib.world.level.inventory.gui.impl;

import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.MenuType;

/**
 * Represents a chest-like GUI with customizable rows and an optional texture.
 * <p>
 * This class is a concrete implementation of {@link AbstractGui} and provides
 * functionality for creating chest-style inventories with a specified number of rows.
 */
public abstract class ChestGui extends AbstractGui {

    /**
     * Constructs a new ChestGui with a given title and specified number of rows.
     *
     * @param title the title of the GUI
     * @param rows  the number of rows for the inventory (1 to 6)
     */
    public ChestGui(Component title, int rows) {
        super(title, typeByRows(rows));
    }

    @Override
    public boolean shouldHandle(Type type) {
        return type == Type.TOP;
    }

    /**
     * Returns the corresponding {@link MenuType} for a specified number of rows.
     *
     * @param rows the number of rows (1 to 6)
     * @return the corresponding {@link MenuType} for the chest inventory
     */
    private static MenuType typeByRows(int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9X1;
            case 2 -> MenuType.GENERIC_9X2;
            case 3 -> MenuType.GENERIC_9X3;
            case 4 -> MenuType.GENERIC_9X4;
            case 5 -> MenuType.GENERIC_9X5;
            case 6 -> MenuType.GENERIC_9X6;
            default -> throw new IllegalArgumentException("Rows must be between 1 and 6.");
        };
    }
}
