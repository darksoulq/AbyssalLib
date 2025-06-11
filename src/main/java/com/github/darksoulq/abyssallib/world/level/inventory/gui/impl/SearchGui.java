package com.github.darksoulq.abyssallib.world.level.inventory.gui.impl;

import com.github.darksoulq.abyssallib.server.event.context.gui.GuiCloseContext;
import com.github.darksoulq.abyssallib.server.resource.glyph.GuiTexture;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.AbstractGui;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.slot.StaticSlot;
import com.github.darksoulq.abyssallib.world.level.item.Items;
import net.kyori.adventure.text.Component;
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

    private final Map<Player, String> texts = new HashMap<>();
    private final ItemStack invisItem = Items.INVISIBLE_ITEM.get().stack();
    private final StaticSlot inputSlot;

    /**
     * Constructs a new SearchGui with a player and a {@link GuiTexture} for the GUI's texture.
     *
     * @param texture the texture that defines the GUI title and appearance
     */
    public SearchGui(GuiTexture texture) {
        super(texture.getTitle(), MenuType.ANVIL);

        invisItem.editMeta((itemMeta -> {
            itemMeta.itemName(Component.text().build());
        }));
        inputSlot = new StaticSlot(0, invisItem);
    }
    /**
     * Constructs a new SearchGui with a player and a custom title.
     *
     * @param title the title of the GUI
     */
    public SearchGui(Component title) {
        super(title, MenuType.ANVIL);

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
    public String text(Player player) {
        return texts.getOrDefault(player, "");
    }

    /**
     * Initializes the GUI by setting up the input slot and backing up the player's inventory.
     *
     * @param player the player who is interacting with the GUI
     */
    @Override
    public void init(Player player) {
        texts.put(player, "");

        ItemStack[] originalContents = inventory(player, Type.BOTTOM).getContents();
        backupMap.put(player, Arrays.copyOf(originalContents, originalContents.length));

        if (allowInput()) {
            slot(Type.TOP, inputSlot);
        }

        inventory(player, Type.BOTTOM).clear();
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
        getSlotList(ctx.player, Type.TOP).remove(inputSlot);
        inventory(ctx.player, Type.TOP).setContents(new ItemStack[] {null, null, null});
        restoreBottomMenu(ctx.player);
        viewers().remove(ctx.player);
        _onClose(ctx);
    }

    /**
     * Restores the player's original inventory from the backup.
     */
    public void restoreBottomMenu(Player player) {
        if (backupMap.containsKey(player)) {
            player.getInventory().setContents(backupMap.get(player));
            backupMap.remove(player);
        }
    }
}