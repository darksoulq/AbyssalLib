package com.github.darksoulq.abyssallib.world.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.MenuType;

import java.util.*;
import java.util.function.Consumer;

public class Gui {
    private final MenuType menuType;
    private final Component title;
    private final Map<SlotPosition, GuiElement> elements = new HashMap<>();
    private final List<GuiLayer> layers = new ArrayList<>();
    private final List<Consumer<GuiView>> tickers = new ArrayList<>();
    private final EnumSet<GuiFlag> flags;
    Consumer<GuiView> onOpen;
    Consumer<GuiView> onClose;

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

    public MenuType getMenuType() {
        return menuType;
    }

    public Consumer<GuiView> getOnClose() {
        return onClose;
    }

    public Consumer<GuiView> getOnOpen() {
        return onOpen;
    }

    public List<Consumer<GuiView>> getTickers() {
        return tickers;
    }

    public List<GuiLayer> getLayers() {
        return layers;
    }

    public Map<SlotPosition, GuiElement> getElements() {
        return elements;
    }

    public boolean hasFlag(GuiFlag flag) {
        return flags.contains(flag);
    }

    public EnumSet<GuiFlag> getFlags() {
        return flags;
    }

    public Component getTitle() {
        return title;
    }

    public static Builder builder(MenuType type, Component title) {
        return new Builder(type, title);
    }

    public static class Builder {
        private final MenuType menuType;
        private final Component title;
        private final Map<SlotPosition, GuiElement> elements = new HashMap<>();
        private final List<GuiLayer> layers = new ArrayList<>();
        private final List<Consumer<GuiView>> tickers = new ArrayList<>();
        private final EnumSet<GuiFlag> flags = EnumSet.noneOf(GuiFlag.class);
        private Consumer<GuiView> onOpen = v -> {};
        private Consumer<GuiView> onClose = v -> {};

        public Builder(MenuType menuType, Component title) {
            this.menuType = menuType;
            this.title = title;
        }

        public Builder set(SlotPosition pos, GuiElement element) {
            elements.put(pos, element);
            return this;
        }

        public Builder addLayer(GuiLayer layer) {
            layers.add(layer);
            return this;
        }

        public Builder onTick(Consumer<GuiView> tick) {
            tickers.add(tick);
            return this;
        }

        public Builder onOpen(Consumer<GuiView> handler) {
            this.onOpen = handler;
            return this;
        }

        public Builder onClose(Consumer<GuiView> handler) {
            this.onClose = handler;
            return this;
        }

        public Builder addFlag(GuiFlag flag) {
            this.flags.add(flag);
            return this;
        }

        public Builder addFlags(GuiFlag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public Gui build() {
            return new Gui(menuType, title, elements, layers, tickers, flags, onOpen, onClose);
        }
    }
}
