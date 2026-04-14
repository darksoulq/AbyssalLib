package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.bootstrap.*;
import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.permission.PermissionManager;
import com.github.darksoulq.abyssallib.server.permission.internal.PermissionWebServer;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AbyssalLib extends JavaPlugin {
    public static final String PLUGIN_ID = "abyssallib";
    private static AbyssalLib INSTANCE;
    public static Logger LOGGER;
    public static PluginConfig CONFIG;
    public static PackServer PACK_SERVER;
    public static EventBus EVENT_BUS;
    public static PermissionManager PERMISSION_MANAGER;
    public static PermissionWebServer PERMISSION_WEB_SERVER;

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    private final BukkitMetrics.Factory metrics = BukkitMetrics.factory()
        .token("f47c8167cf57b94da51af557fc7e3005")
        .errorTracker(ERROR_TRACKER);

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();

        FileSetup.init(this);
        ContentRegistry.init();

        CONFIG = new PluginConfig();
        CONFIG.cfg.save();

        RecipeSetup.init(this);
        PermissionSetup.init(this);
        EventRegistry.init(this);
        ServiceStarter.init();
        PackSetup.init(this);

        if (CONFIG.metrics.get()) {
            Metrics metricsImpl = metrics.create(this);
            metricsImpl.ready();
        }
    }

    @Override
    public void onDisable() {
        PluginShutdown.execute();
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }
}