package com.github.darksoulq.abyssallib.server.scoreboard;

import com.github.darksoulq.abyssallib.server.scoreboard.internal.PlayerSidebarManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class Sidebar {

    private final Key id;
    private final int priority;

    private Function<Player, Component> titleProvider = p -> Component.empty();

    @SuppressWarnings("unchecked")
    private final Function<Player, Component>[] lines = new Function[15];

    private boolean showNumbers = false;

    public Sidebar(Key id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    public Key getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public void setTitle(Component title) {
        this.titleProvider = p -> title;
    }

    public void setTitle(Function<Player, Component> titleProvider) {
        this.titleProvider = titleProvider;
    }

    public void setLine(int index, Component line) {
        if (index >= 0 && index < 15) this.lines[index] = p -> line;
    }

    public void setLine(int index, Function<Player, Component> lineProvider) {
        if (index >= 0 && index < 15) this.lines[index] = lineProvider;
    }

    public void removeLine(int index) {
        if (index >= 0 && index < 15) this.lines[index] = null;
    }

    public void clearLines() {
        for (int i = 0; i < 15; i++) {
            this.lines[i] = null;
        }
    }

    public void setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
    }

    public void addViewer(Player player) {
        PlayerSidebarManager.get(player).addSidebar(this);
    }

    public void removeViewer(Player player) {
        PlayerSidebarManager.get(player).removeSidebar(this);
    }

    public void destroy() {
        PlayerSidebarManager.destroySidebar(this);
    }

    public Component getTitle(Player player) {
        return titleProvider.apply(player);
    }

    public Function<Player, Component>[] getLines() {
        return lines;
    }

    public boolean isShowNumbers() {
        return showNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sidebar sidebar)) return false;
        return id.equals(sidebar.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}