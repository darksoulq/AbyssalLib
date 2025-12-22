package com.github.darksoulq.abyssallib.server.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Utility class for scheduling Bukkit tasks.
 */
public class TaskUtil {

    /**
     * Runs a task once after a specified delay.
     *
     * @param plugin the plugin instance
     * @param delay  delay in ticks before running the task
     * @param task   the task to run
     * @return the BukkitTask instance
     */
    public static BukkitTask delayedTask(Plugin plugin, int delay, Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(plugin, delay);
    }

    /**
     * Runs a task repeatedly with a delay and interval.
     *
     * @param plugin   the plugin instance
     * @param delay    delay in ticks before first run
     * @param interval interval in ticks between runs
     * @param task     the task to run
     * @return the BukkitTask instance
     */
    public static BukkitTask repeatingTask(Plugin plugin, int delay, int interval, Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskTimer(plugin, delay, interval);
    }

    /**
     * Runs a task asynchronously once after a specified delay.
     *
     * @param plugin the plugin instance
     * @param delay  delay in ticks before running the task
     * @param task   the task to run
     * @return the BukkitTask instance
     */
    public static BukkitTask delayedAsyncTask(Plugin plugin, int delay, Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLaterAsynchronously(plugin, delay);
    }

    /**
     * Runs a task asynchronously repeatedly with a delay and interval.
     *
     * @param plugin   the plugin instance
     * @param delay    delay in ticks before first run
     * @param interval interval in ticks between runs
     * @param task     the task to run
     * @return the BukkitTask instance
     */
    public static BukkitTask repeatingAsyncTask(Plugin plugin, int delay, int interval, Runnable task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskTimerAsynchronously(plugin, delay, interval);
    }

    /**
     * Cancels a BukkitTask safely.
     *
     * @param task the task to cancel
     */
    public static void cancelTask(BukkitTask task) {
        if (task != null) {
            task.cancel();
        }
    }
}
