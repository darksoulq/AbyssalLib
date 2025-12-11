package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.internal.MapLoader;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.tag.TagLoader;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ServerEvents {
    @SubscribeEvent(ignoreCancelled = false)
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    MapLoader.load();
                    CommandBus.register(AbyssalLib.MODID, new InternalCommand());
                    BlockManager.load();
                    EntityManager.load();
                    EntityManager.restoreEntities();
                    EntityAttributes.init();
                    PlayerStatistics.init();
                    RecipeLoader.reload();
                    TagLoader.loadTags();
                }
            }.runTaskLater(AbyssalLib.getInstance(), 10);
        }
    }
}
