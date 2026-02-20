package com.github.darksoulq.abyssallib.world.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.MenuType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract base implementation of a Graphical User Interface (GUI).
 * This class serves as a template for creating custom menus by providing
 * lifecycle hooks and simplified methods for managing elements, flags, and layers.
 */
public abstract class AbstractGui extends Gui {

    /**
     * Constructs a new AbstractGui with the specified menu type and title.
     * Initializes internal collections including a HashMap for elements and
     * LinkedLists for layers and animations.
     *
     * @param menuType
     * The {@link MenuType} defining the size and shape of the inventory.
     * @param title
     * The {@link Component} representing the display name of the menu.
     */
    public AbstractGui(MenuType menuType, Component title) {
        super(menuType, title, new HashMap<>(), new LinkedList<>(), new LinkedList<>(), EnumSet.noneOf(GuiFlag.class), null, null);
        init();
        this.onOpen = this::onOpen;
        this.onClose = this::onClose;
    }

    /**
     * Initializes the GUI components.
     * This method is called during construction and should be used to
     * populate the GUI with initial elements and configuration.
     */
    protected abstract void init();

    /**
     * Logic to execute when a player opens this GUI.
     *
     * @param view
     * The {@link GuiView} representing the interaction between the player and the GUI.
     */
    protected abstract void onOpen(GuiView view);

    /**
     * Logic to execute when a player closes this GUI.
     *
     * @param view
     * The {@link GuiView} representing the interaction between the player and the GUI.
     */
    protected abstract void onClose(GuiView view);

    /**
     * Assigns a {@link GuiElement} to a specific position within the GUI grid.
     *
     * @param pos
     * The {@link SlotPosition} where the element should be placed.
     * @param element
     * The {@link GuiElement} to be rendered at the specified position.
     */
    public void set(SlotPosition pos, GuiElement element) {
        this.getElements().put(pos, element);
    }

    /**
     * Enables a specific behavior or restriction flag for this GUI.
     *
     * @param flag
     * The {@link GuiFlag} to be added to the GUI configuration.
     */
    public void addFlag(GuiFlag flag) {
        this.getFlags().add(flag);
    }

    /**
     * Enables multiple behavior or restriction flags for this GUI.
     *
     * @param flags
     * A varargs array of {@link GuiFlag} constants to be added.
     */
    public void addFlags(GuiFlag... flags) {
        this.getFlags().addAll(List.of(flags));
    }

    /**
     * Adds a functional layer to the GUI.
     * Layers are processed to determine element visibility and interaction priority.
     *
     * @param layer
     * The {@link GuiLayer} to append to the GUI's layer stack.
     */
    public void addLayer(GuiLayer layer) {
        this.getLayers().add(layer);
    }
}