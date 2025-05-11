package me.darksoul.abyssallib.gui.impl;

import me.darksoul.abyssallib.gui.AbstractGui;
import me.darksoul.abyssallib.resource.glyph.GuiTexture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;

/**
 * Represents a chest-like GUI with customizable rows and an optional texture.
 * <p>
 * This class is a concrete implementation of {@link AbstractGui} and provides
 * functionality for creating chest-style inventories with a specified number of rows.
 */
public abstract class ChestGui extends AbstractGui {

    /**
     * Constructs a new ChestGui with a player, title, and specified number of rows.
     *
     * @param player the player who will view the GUI
     * @param title the title of the GUI
     * @param rows the number of rows for the inventory (1 to 6)
     */
    public ChestGui(Player player, Component title, int rows) {
        super(player, title, typeByRows(rows));
    }
    /**
     * Constructs a new ChestGui with a player, a {@link GuiTexture} for the title, and a specified number of rows.
     *
     * @param player the player who will view the GUI
     * @param texture the texture that defines the GUI title and any other visual elements
     * @param rows the number of rows for the inventory (1 to 6)
     */
    public ChestGui(Player player, GuiTexture texture, int rows) {
        super(player, texture.getTitle(), typeByRows(rows));
        init(player);
    }

    /**
     * Returns the corresponding {@link MenuType} for a specified number of rows.
     *
     * @param rows the number of rows (1 to 6)
     * @return the corresponding {@link MenuType} for the chest inventory
     */
    private static MenuType typeByRows(int rows) {
        switch (rows) {
            case 1 -> {
                return MenuType.GENERIC_9X1;
            }
            case 2 -> {
                return MenuType.GENERIC_9X2;
            }
            case 3 -> {
                return MenuType.GENERIC_9X3;
            }
            case 4 -> {
                return MenuType.GENERIC_9X4;
            }
            case 5 -> {
                return MenuType.GENERIC_9X5;
            }
            case 6 -> {
                return MenuType.GENERIC_9X6;
            }
        }
        return MenuType.GENERIC_9X6;
    }
}
