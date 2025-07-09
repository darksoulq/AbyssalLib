package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.internal.MapLoader;
import com.github.darksoulq.abyssallib.world.level.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.level.entity.internal.EntityManager;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerEvents {
    @SubscribeEvent
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    MapLoader.load();
                    CommandBus.register(AbyssalLib.MODID, new InternalCommand());
                    BlockManager.load();
                    EntityManager.load();
                    EntityAttributes.init();
                }
            }.runTaskLater(AbyssalLib.getInstance(), 10);
        }
    }
}
