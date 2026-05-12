package com.github.darksoulq.abyssallib.server.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.function.Supplier;

public class TaskDispatcher {
    private static final boolean IS_FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (Exception ignored) {}
        IS_FOLIA = folia;
    }

    private final Plugin plugin;

    public TaskDispatcher(Plugin plugin) {
        this.plugin = plugin;
    }

    public ScheduledTask dispatch(Runnable action, boolean async, Entity entity, Location location, long delay, Clock delayClock, long period, Clock periodClock, Supplier<Boolean> until, Supplier<Boolean> whileCond) {
        boolean isAsync = async || delayClock == Clock.REALTIME || periodClock == Clock.REALTIME;
        AbstractScheduledTask abstractTask = new AbstractScheduledTask(until, whileCond, entity);
        
        long delayTicks = delayClock == Clock.REALTIME ? delay / 50 : delay;
        long periodTicks = periodClock == Clock.REALTIME ? period / 50 : period;
        long delayMillis = delayClock == Clock.REALTIME ? delay : delay * 50;
        long periodMillis = periodClock == Clock.REALTIME ? period : period * 50;

        if (isAsync && !IS_FOLIA) {
            return RealtimeTaskDispatcher.schedule(action, delayMillis, periodMillis, abstractTask);
        }

        if (IS_FOLIA) {
            return FoliaTaskDispatcher.schedule(plugin, action, isAsync, entity, location, delayTicks, periodTicks, delayMillis, periodMillis, abstractTask);
        } else {
            return BukkitTaskDispatcher.schedule(plugin, action, isAsync, delayTicks, periodTicks, delayMillis, periodMillis, abstractTask);
        }
    }
}