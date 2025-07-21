package com.github.darksoulq.abyssallib.world.level.inventory.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.MenuType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGui extends Gui {

    public AbstractGui(MenuType menuType, Component title) {
        super(menuType, title, new HashMap<>(), new LinkedList<>(), new LinkedList<>(), EnumSet.noneOf(GuiFlag.class),null, null);
        init();
        this.onOpen = this::onOpen;
        this.onClose = this::onClose;
    }

    protected abstract void init();
    protected abstract void onOpen(GuiView view);
    protected abstract void onClose(GuiView view);

    public void set(SlotPosition pos, GuiElement element) {
        this.getElements().put(pos, element);
    }
    public void addFlag(GuiFlag flag) {
        this.getFlags().add(flag);
    }
    public void addFlags(GuiFlag... flags) {
        this.getFlags().addAll(List.of(flags));
    }
    public void addLayer(GuiLayer layer) {
        this.getLayers().add(layer);
    }
}
