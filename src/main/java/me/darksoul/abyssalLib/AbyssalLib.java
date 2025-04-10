package me.darksoul.abyssalLib;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.darksoul.abyssalLib.command.InternalCommand;
import me.darksoul.abyssalLib.event.EventBus;
import me.darksoul.abyssalLib.event.ItemEvents;
import me.darksoul.abyssalLib.item.test.TestItems;
import me.darksoul.abyssalLib.mod.AbyssalMod;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import org.bukkit.plugin.java.JavaPlugin;

@AbyssalMod(name="basemod")
public final class AbyssalLib extends JavaPlugin {
    private static AbyssalLib instance;

    public static AbyssalLib getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        EventBus bus = new EventBus(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(InternalCommand.createCommand().build());
        });
        bus.register(new ItemEvents());
        BuiltinRegistries.MODS.registerMod(this.getClass());

        // Apply registries
        TestItems.ITEMS.apply();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
