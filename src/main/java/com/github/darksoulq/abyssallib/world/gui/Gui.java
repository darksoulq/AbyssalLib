package com.github.darksoulq.abyssallib.world.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.MenuType;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a template for a custom Graphical User Interface.
 * <p>
 * A Gui instance stores the menu configuration, including its type, title,
 * static elements, visual layers, and event handlers. It is intended to
 * be constructed via its {@link Builder}.
 */
public class Gui {
    /** The Bukkit MenuType defining the inventory size and shape. */
    private final MenuType menuType;

    /** The display title of the inventory. */
    private final Component title;

    /** A map of positions to the elements rendered at those locations. */
    private final Map<SlotPosition, GuiElement> elements = new HashMap<>();

    /** A list of visual layers that render across the inventory. */
    private final List<GuiLayer> layers = new ArrayList<>();

    /** A list of consumers executed every tick for active views. */
    private final List<Consumer<GuiView>> tickers = new ArrayList<>();

    /** A set of configuration flags affecting GUI behavior. */
    private final EnumSet<GuiFlag> flags;

    /** Logic executed when the GUI is opened. */
    Consumer<GuiView> onOpen;

    /** Logic executed when the GUI is closed. */
    Consumer<GuiView> onClose;

    /**
     * Constructs a new Gui configuration.
     *
     * @param menuType the type of inventory
     * @param title    the inventory title
     * @param elements the static element map
     * @param layers   the list of rendering layers
     * @param tickers  the list of tick handlers
     * @param flags    the behavior flags
     * @param onOpen   the open handler
     * @param onClose  the close handler
     */
    public Gui(MenuType menuType, Component title,
               Map<SlotPosition, GuiElement> elements,
               List<GuiLayer> layers,
               List<Consumer<GuiView>> tickers,
               EnumSet<GuiFlag> flags,
               Consumer<GuiView> onOpen,
               Consumer<GuiView> onClose) {
        this.menuType = menuType;
        this.title = title;
        this.elements.putAll(elements);
        this.layers.addAll(layers);
        this.tickers.addAll(tickers);
        this.flags = flags;
        this.onOpen = onOpen;
        this.onClose = onClose;
    }

    /**
     * Gets the MenuType of this GUI.
     *
     * @return the menu type
     */
    public MenuType getMenuType() {
        return menuType;
    }

    /**
     * Gets the closure event handler.
     *
     * @return the close consumer
     */
    public Consumer<GuiView> getOnClose() {
        return onClose;
    }

    /**
     * Gets the opening event handler.
     *
     * @return the open consumer
     */
    public Consumer<GuiView> getOnOpen() {
        return onOpen;
    }

    /**
     * Gets the list of per-tick update handlers.
     *
     * @return the list of tickers
     */
    public List<Consumer<GuiView>> getTickers() {
        return tickers;
    }

    /**
     * Gets the list of visual layers.
     *
     * @return the list of layers
     */
    public List<GuiLayer> getLayers() {
        return layers;
    }

    /**
     * Gets the map of slot positions to GUI elements.
     *
     * @return the element map
     */
    public Map<SlotPosition, GuiElement> getElements() {
        return elements;
    }

    /**
     * Checks if a specific behavior flag is set.
     *
     * @param flag the flag to check
     * @return true if the flag is present
     */
    public boolean hasFlag(GuiFlag flag) {
        return flags.contains(flag);
    }

    /**
     * Gets all behavior flags associated with this GUI.
     *
     * @return the enum set of flags
     */
    public EnumSet<GuiFlag> getFlags() {
        return flags;
    }

    /**
     * Gets the title component of this GUI.
     *
     * @return the title
     */
    public Component getTitle() {
        return title;
    }

    /**
     * Creates a new builder for a Gui.
     *
     * @param type  the menu type
     * @param title the menu title
     * @return a new builder instance
     */
    public static Builder builder(MenuType type, Component title) {
        return new Builder(type, title);
    }

    /**
     * A fluent builder for creating Gui configurations.
     */
    public static class Builder {
        private final MenuType menuType;
        private final Component title;
        private final Map<SlotPosition, GuiElement> elements = new HashMap<>();
        private final List<GuiLayer> layers = new ArrayList<>();
        private final List<Consumer<GuiView>> tickers = new ArrayList<>();
        private final EnumSet<GuiFlag> flags = EnumSet.noneOf(GuiFlag.class);
        private Consumer<GuiView> onOpen = v -> {};
        private Consumer<GuiView> onClose = v -> {};

        /**
         * Constructs the builder with required fields.
         *
         * @param menuType the menu type
         * @param title    the title component
         */
        public Builder(MenuType menuType, Component title) {
            this.menuType = menuType;
            this.title = title;
        }

        /**
         * Assigns an element to a specific slot position.
         *
         * @param pos     the position in the top or bottom segment
         * @param element the element to render
         * @return this builder
         */
        public Builder set(SlotPosition pos, GuiElement element) {
            elements.put(pos, element);
            return this;
        }

        /**
         * Adds a visual layer to the GUI.
         *
         * @param layer the layer instance
         * @return this builder
         */
        public Builder addLayer(GuiLayer layer) {
            layers.add(layer);
            return this;
        }

        /**
         * Adds a logic handler to be executed every tick.
         *
         * @param tick the tick consumer
         * @return this builder
         */
        public Builder onTick(Consumer<GuiView> tick) {
            tickers.add(tick);
            return this;
        }

        /**
         * Sets the handler for when the GUI is opened.
         *
         * @param handler the open consumer
         * @return this builder
         */
        public Builder onOpen(Consumer<GuiView> handler) {
            this.onOpen = handler;
            return this;
        }

        /**
         * Sets the handler for when the GUI is closed.
         *
         * @param handler the close consumer
         * @return this builder
         */
        public Builder onClose(Consumer<GuiView> handler) {
            this.onClose = handler;
            return this;
        }

        /**
         * Adds a behavioral flag to the GUI.
         *
         * @param flag the flag to add
         * @return this builder
         */
        public Builder addFlag(GuiFlag flag) {
            this.flags.add(flag);
            return this;
        }

        /**
         * Adds multiple behavioral flags to the GUI.
         *
         * @param flags the flags to add
         * @return this builder
         */
        public Builder addFlags(GuiFlag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        /**
         * Constructs the Gui instance from the builder configuration.
         *
         * @return the built Gui
         */
        public Gui build() {
            return new Gui(menuType, title, elements, layers, tickers, flags, onOpen, onClose);
        }
    }
}