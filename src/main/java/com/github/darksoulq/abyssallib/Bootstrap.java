package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.server.command.CommandBus;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class Bootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands -> {
            CommandBus.init(commands.registrar().getDispatcher());
        });
    }
}
