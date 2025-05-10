package io.github.darksoulq.abyssalLib.gui.impl;

import io.github.darksoulq.abyssalLib.event.context.gui.GuiCloseContext;
import io.github.darksoulq.abyssalLib.gui.AbstractGui;
import io.github.darksoulq.abyssalLib.gui.slot.StaticSlot;
import io.github.darksoulq.abyssalLib.resource.glyph.GuiTexture;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A GUI that allows users to perform a search operation by entering text.
 * <p>
 * This class provides a search interface for players with an optional input slot for entering search terms.
 * It backs up the player's original inventory and restores it after closing the GUI.
 */
public abstract class SearchGui extends AbstractGui {
    private static final Map<Player, ItemStack[]> backupMap = new HashMap<>();

    private String text;
    private final ItemStack invisItem = new ItemStack(Material.PAPER);
    private final StaticSlot inputSlot;

    /**
     * Constructs a new SearchGui with a player and a {@link GuiTexture} for the GUI's texture.
     *
     * @param player the player who will view the GUI
     * @param texture the texture that defines the GUI title and appearance
     */
    public SearchGui(Player player, GuiTexture texture) {
        super(player, texture.getTitle(), MenuType.ANVIL);

        invisItem.editMeta((itemMeta -> {
            itemMeta.itemName(Component.text().build());
        }));
        inputSlot = new StaticSlot(0, invisItem);
    }
    /**
     * Constructs a new SearchGui with a player and a custom title.
     *
     * @param player the player who will view the GUI
     * @param title the title of the GUI
     */
    public SearchGui(Player player, Component title) {
        super(player, title, MenuType.ANVIL);

        invisItem.editMeta((itemMeta -> {
            itemMeta.itemName(Component.text().build());
        }));
        inputSlot = new StaticSlot(0, invisItem);
    }
    /**
     * Initializes the search GUI with custom logic. This method must be implemented in subclasses.
     *
     * @param player the player who is interacting with the GUI
     */
    public abstract void _init(Player player);

    /**
     * Gets the current search text entered by the player.
     *
     * @return the search text
     */
    public String text() {
        return text;
    }

    /**
     * Initializes the GUI by setting up the input slot and backing up the player's inventory.
     *
     * @param player the player who is interacting with the GUI
     */
    @Override
    public void init(Player player) {
        text = "";

        ItemStack[] originalContents = inventory(Type.BOTTOM).getContents();
        backupMap.put(player, Arrays.copyOf(originalContents, originalContents.length));

        if (allowInput()) {
            slot(Type.TOP, inputSlot);
        }

        inventory(Type.BOTTOM).clear();
        _init(player);
    }

    /**
     * Determines whether the input slot should be included in the GUI.
     *
     * @return true if input is allowed, false otherwise
     */
    public boolean allowInput() {
        return true;
    }

    /**
     * Handles additional custom close logic when the GUI is closed.
     * This method is called after the player's inventory has been restored.
     *
     * @param ctx the context of the GUI close event
     */
    public void _onClose(GuiCloseContext ctx) {}
    @Override
    public void onClose(GuiCloseContext ctx) {
        slots.TOP.remove(inputSlot);
        inventory(Type.TOP).setContents(new ItemStack[] {null, null, null});
        restoreBottomMenu();
        _onClose(ctx);
    }

    /**
     * Restores the player's original inventory from the backup.
     */
    public void restoreBottomMenu() {
        Player player = (Player) view().getPlayer();

        if (backupMap.containsKey(player)) {
            player.getInventory().setContents(backupMap.get(player));
            backupMap.remove(player);
        }
    }
}