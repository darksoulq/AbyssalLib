package me.darksoul.abyssallib.event.internal;

import me.darksoul.abyssallib.AbyssalLib;
import me.darksoul.abyssallib.command.CommandBus;
import me.darksoul.abyssallib.command.InternalCommand;
import me.darksoul.abyssallib.event.SubscribeEvent;
import me.darksoul.abyssallib.recipe.RecipeRegistrar;
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
