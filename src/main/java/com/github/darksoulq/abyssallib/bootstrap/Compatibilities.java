package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.bootstrap.compat.BrewingPlaceholderProvider;
import com.github.darksoulq.abyssallib.bootstrap.compat.BrewingProjectProvider;
import com.github.darksoulq.abyssallib.server.scheduler.Clock;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.integration.IntegrationTypes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Compatibilities {
    public static void brewery() {
        RegisteredServiceProvider<TheBrewingProjectApi> tbpProvider = Bukkit.getServicesManager().getRegistration(TheBrewingProjectApi.class);
        if (tbpProvider != null) {
            BrewingProjectProvider integration = new BrewingProjectProvider();
            BrewingPlaceholderProvider placeholderProvider = new BrewingPlaceholderProvider();
            AbyssalLib.SCHEDULER.schedule(() -> {
                integration.initialized.complete(null);
                placeholderProvider.initialized.complete(null);
            }).after(10L, Clock.TICKS).once();
            TheBrewingProjectApi tbp = tbpProvider.getProvider();
            tbp.getIntegrationManager().register(IntegrationTypes.ITEM, integration);
            tbp.getIntegrationManager().register(IntegrationTypes.PLACEHOLDER, placeholderProvider);
        }

    }
}
