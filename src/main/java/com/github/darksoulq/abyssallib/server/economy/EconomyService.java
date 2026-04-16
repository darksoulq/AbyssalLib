package com.github.darksoulq.abyssallib.server.economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import java.util.Optional;

public final class EconomyService {
    private EconomyService() {}

    public static void register(Plugin plugin, EconomyProvider provider, ServicePriority priority) {
        Bukkit.getServicesManager().register(EconomyProvider.class, provider, plugin, priority);
    }

    public static void unregister(EconomyProvider provider) {
        Bukkit.getServicesManager().unregister(EconomyProvider.class, provider);
    }

    public static void unregisterAll(Plugin plugin) {
        Bukkit.getServicesManager().unregisterAll(plugin);
    }

    public static Optional<EconomyProvider> get() {
        RegisteredServiceProvider<EconomyProvider> rsp = Bukkit.getServicesManager().getRegistration(EconomyProvider.class);
        return rsp != null ? Optional.of(rsp.getProvider()) : Optional.empty();
    }

    public static boolean isAvailable() {
        return Bukkit.getServicesManager().getRegistration(EconomyProvider.class) != null;
    }

    public static <T> Optional<T> capability(Class<T> capabilityClass) {
        return get().flatMap(provider -> provider.capability(capabilityClass));
    }
}