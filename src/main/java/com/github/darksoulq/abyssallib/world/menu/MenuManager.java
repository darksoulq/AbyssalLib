package com.github.darksoulq.abyssallib.world.menu;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import com.github.darksoulq.abyssallib.server.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Experimental
public class MenuManager {
    private static final Map<InventoryView, AbstractContainerMenu> OPEN_VIEWS = new ConcurrentHashMap<>();
    private static final Map<AbstractContainerMenu, ScheduledTask> TICK_TASKS = new ConcurrentHashMap<>();
    private static final Map<Container, List<AbstractContainerMenu>> CONTAINER_MENUS = new ConcurrentHashMap<>();

    public static void registerMenu(InventoryView view, AbstractContainerMenu menu) {
        OPEN_VIEWS.put(view, menu);
        if (menu.requiresTick() && !TICK_TASKS.containsKey(menu)) {
            ScheduledTask task = AbyssalLib.SCHEDULER.schedule(() -> {
                try {
                    menu.tick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).repeatEvery(1, Clock.TICKS);
            TICK_TASKS.put(menu, task);
        }
    }

    public static void removeMenu(InventoryView view) {
        AbstractContainerMenu menu = OPEN_VIEWS.remove(view);
        if (menu != null) {
            Player viewer = view.getPlayer() instanceof Player p ? p : null;
            if (viewer != null) {
                try {
                    menu.removeViewer(viewer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (menu.getViewers().isEmpty()) {
                ScheduledTask task = TICK_TASKS.remove(menu);
                if (task != null) {
                    task.cancel();
                }
                try {
                    menu.removed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static AbstractContainerMenu getMenu(InventoryView view) {
        return OPEN_VIEWS.get(view);
    }

    public static void openMenu(Player player, MenuProvider provider) {
        try {
            AbstractContainerMenu menu = provider.createMenu(player);
            InventoryView view = menu.open(player, provider.getDisplayName());
            if (view != null) {
                registerMenu(view, menu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void linkMenuContainer(AbstractContainerMenu menu, Container container) {
        CONTAINER_MENUS.computeIfAbsent(container, k -> new ArrayList<>(1)).add(menu);
    }

    public static void unlinkMenu(AbstractContainerMenu menu) {
        for (List<AbstractContainerMenu> menus : CONTAINER_MENUS.values()) {
            menus.remove(menu);
        }
    }

    public static void invalidateContainer(Container container) {
        List<AbstractContainerMenu> menus = CONTAINER_MENUS.remove(container);
        if (menus != null) {
            for (AbstractContainerMenu menu : menus) {
                try {
                    for (Player player : menu.getViewers()) {
                        player.closeInventory();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}