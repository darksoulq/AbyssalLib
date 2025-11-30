package com.github.darksoulq.abyssallib.server.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskUtil {
    public static BukkitTask delayedTask(Plugin plugin, int delay, Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(plugin, delay);
    }
}
