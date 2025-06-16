package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.data.Datapack;
import com.github.darksoulq.abyssallib.world.level.entity.DamageType;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.event.RegistryEvents;

public class Bootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands -> {
            CommandBus.init(commands.registrar().getDispatcher());
        });

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY,
                datapacks -> {
            AbyssalLib.DATAPACK_REGISTRAR = new Datapack.Registrar(datapacks.registrar());
        });

        context.getLifecycleManager().registerEventHandler(RegistryEvents.DAMAGE_TYPE.freeze()
                .newHandler(event -> {
                    AbyssalLib.DAMAGE_TYPE_REGISTRAR = new DamageType.Registrar(event);
                })
        );
    }
}
