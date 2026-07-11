package com.github.darksoulq.abyssallib;

import com.github.darksoulq.abyssallib.bootstrap.*;
import com.github.darksoulq.abyssallib.common.config.internal.PluginConfig;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.permission.PermissionManager;
import com.github.darksoulq.abyssallib.server.permission.internal.PermissionWebServer;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.PackServer;
import com.github.darksoulq.abyssallib.server.scheduler.Scheduler;
import com.github.darksoulq.abyssallib.server.util.Integrations;
import com.github.darksoulq.abyssallib.server.util.UpdateChecker;
import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.ErrorTracker;
import dev.faststats.core.Metrics;
import dev.faststats.core.data.Metric;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class AbyssalLib extends JavaPlugin {
    public static final String PLUGIN_ID = "abyssallib";
    private static AbyssalLib INSTANCE;
    public static Logger LOGGER;
    public static PluginConfig CONFIG;
    public static PackServer PACK_SERVER;
    public static EventBus EVENT_BUS;
    public static Scheduler SCHEDULER;
    public static PermissionManager PERMISSION_MANAGER;
    public static PermissionWebServer PERMISSION_WEB_SERVER;

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    private final BukkitMetrics.Factory metrics = BukkitMetrics.factory()
        .token("f47c8167cf57b94da51af557fc7e3005")
        .errorTracker(ERROR_TRACKER)
        .addMetric(Metric.number("items", () -> Registries.ITEMS.getAll().size()))
        .addMetric(Metric.number("blocks", () -> Registries.BLOCKS.getAll().size()))
        .addMetric(Metric.number("entities", () -> Registries.ENTITIES.getAll().size()))
        .addMetric(Metric.number("advancements", () -> Registries.ADVANCEMENTS.getAll().size()))
        .addMetric(Metric.number("placeholders", () -> Registries.PLACEHOLDERS.getAll().size()));

    @Override
    public void onEnable() {

        INSTANCE = this;
        LOGGER = getLogger();
        SCHEDULER = new Scheduler(this);

        FileSetup.init(this);
        Content.init();

        CONFIG = new PluginConfig();
        CONFIG.cfg.save();

        RecipeSetup.init(this);
        Permissions.init(this);
        Events.init(this);
        Services.init();
        PackSetup.init(this);
        Integrations.when("TheBrewingProject", (ignored) -> Compatibilities.brewery());

        if (CONFIG.metrics.get()) {
            Metrics metricsImpl = metrics.create(this);
            metricsImpl.ready();
        }

        new UpdateChecker(this, "abyssallib", true, "-alpha").check(result -> {
            getLogger().warning("A new update is available: " + result.version().toString());
            getLogger().warning("Download at: " + result.link());
        });
    }

    @Override
    public void onDisable() {
        PluginShutdown.execute();
    }

    public static AbyssalLib getInstance() {
        return INSTANCE;
    }
}