package io.github.darksoulq.abyssalLib.event.internal;

import io.github.darksoulq.abyssalLib.AbyssalLib;
import io.github.darksoulq.abyssalLib.command.CommandBus;
import io.github.darksoulq.abyssalLib.command.InternalCommand;
import io.github.darksoulq.abyssalLib.event.SubscribeEvent;
import io.github.darksoulq.abyssalLib.recipe.RecipeRegistrar;
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
