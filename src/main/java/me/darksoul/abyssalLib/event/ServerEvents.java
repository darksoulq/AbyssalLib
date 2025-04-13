package me.darksoul.abyssalLib.event;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.command.CommandBus;
import me.darksoul.abyssalLib.command.InternalCommand;
import me.darksoul.abyssalLib.recipe.RecipeRegistrar;
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
