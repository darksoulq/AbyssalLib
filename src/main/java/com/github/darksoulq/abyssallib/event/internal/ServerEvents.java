package com.github.darksoulq.abyssallib.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.command.CommandBus;
import com.github.darksoulq.abyssallib.command.InternalCommand;
import com.github.darksoulq.abyssallib.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.recipe.RecipeRegistrar;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerEvents {
    @SubscribeEvent
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            CommandBus.INSTANCE.register(AbyssalLib.MODID, new InternalCommand());
            RecipeRegistrar.registerAll();
        }
    }
}
