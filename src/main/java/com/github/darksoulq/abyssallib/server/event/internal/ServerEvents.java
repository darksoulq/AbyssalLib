package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.internal.MapLoader;
import com.github.darksoulq.abyssallib.world.level.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.level.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.RecipeRegistrar;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerEvents {
    @SubscribeEvent
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            MapLoader.load();
            CommandBus.register(AbyssalLib.MODID, new InternalCommand());
            RecipeRegistrar.registerAll();
            BlockManager.load();
            EntityManager.load();
            EntityAttributes.init();
        }
    }
}
