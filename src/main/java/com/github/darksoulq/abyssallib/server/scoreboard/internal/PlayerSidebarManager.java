package com.github.darksoulq.abyssallib.server.scoreboard.internal;

import com.github.darksoulq.abyssallib.server.scoreboard.Sidebar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PlayerSidebarManager {

    private static final Map<UUID, PlayerSidebarManager> MANAGERS = new ConcurrentHashMap<>();

    private final Player player;
    private final PlayerSidebarView view;
    private final Component[] lineBuffer = new Component[15];

    private final PriorityQueue<Sidebar> activeSidebars = new PriorityQueue<>(
        Comparator.comparingInt(Sidebar::getPriority).reversed()
    );

    private Sidebar currentVisible;

    private PlayerSidebarManager(Player player) {
        this.player = player;
        this.view = new PlayerSidebarView(player);
    }

    public static PlayerSidebarManager get(Player player) {
        return MANAGERS.computeIfAbsent(player.getUniqueId(), k -> new PlayerSidebarManager(player));
    }

    public static void remove(Player player) {
        PlayerSidebarManager manager = MANAGERS.remove(player.getUniqueId());
        if (manager != null) {
            manager.view.hide();
        }
    }

    public static void destroySidebar(Sidebar sidebar) {
        for (PlayerSidebarManager manager : MANAGERS.values()) {
            manager.removeSidebar(sidebar);
        }
    }

    public static void closeAll() {
        for (PlayerSidebarManager manager : MANAGERS.values()) {
            manager.view.hide();
        }
        MANAGERS.clear();
    }

    public static void updateAll() {
        for (PlayerSidebarManager manager : MANAGERS.values()) {
            if (manager.currentVisible != null) {
                manager.update();
            }
        }
    }

    public void addSidebar(Sidebar sidebar) {
        if (!activeSidebars.contains(sidebar)) {
            activeSidebars.add(sidebar);
            reevaluate();
        }
    }

    public void removeSidebar(Sidebar sidebar) {
        if (activeSidebars.remove(sidebar)) {
            reevaluate();
        }
    }

    private void reevaluate() {
        Sidebar highest = activeSidebars.peek();

        if (highest == null) {
            view.hide();
            currentVisible = null;
            return;
        }

        if (highest != currentVisible) {
            currentVisible = highest;
            update();
        }
    }

    public void update() {
        if (currentVisible == null) return;

        Component title = currentVisible.getTitle(player);
        Function<Player, Component>[] providers = currentVisible.getLines();

        for (int i = 0; i < 15; i++) {
            Function<Player, Component> provider = providers[i];
            lineBuffer[i] = (provider != null) ? provider.apply(player) : null;
        }

        view.update(title, lineBuffer, currentVisible.isShowNumbers());
    }
}