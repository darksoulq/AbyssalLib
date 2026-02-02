package com.github.darksoulq.abyssallib.common.energy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.sql.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNetworkTransferEvent;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeAddEvent;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The central manager for all energy-related operations.
 * Handles energy distribution across connected nodes, persistence to SQLite,
 * and the main tick loop for energy transfers.
 */
public final class EnergyNetwork {

    private EnergyNetwork() {}

    /** Shared Jackson ObjectMapper for JSON processing. */
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    /** Set of all registered nodes in the network. */
    private static final Set<EnergyNode> NODES = new CopyOnWriteArraySet<>();
    /** Subset of nodes currently capable of providing or receiving energy. */
    private static final Set<EnergyNode> ACTIVE_NODES = new CopyOnWriteArraySet<>();
    /** Internal database for persisting energy node states. */
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "energy_network.db"));

    /**
     * Initializes the energy network.
     * Sets up distribution tasks, periodic saving, and the SQLite database schema.
     */
    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() { distribute(); }
        }.runTaskTimer(AbyssalLib.getInstance(), 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() { save(); }
        }.runTaskTimer(AbyssalLib.getInstance(), 2400L, 6000L);

        try {
            DATABASE.connect();
            DATABASE.executor().create("energy_nodes")
                .ifNotExists()
                .column("id", "TEXT")
                .column("json", "TEXT")
                .primaryKey("id")
                .execute();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to init energy network DB: " + e.getMessage());
        }
    }

    /**
     * Registers a node into the network.
     * @param node The {@link EnergyNode} to register.
     */
    public static void register(EnergyNode node) {
        EnergyNodeAddEvent event = new EnergyNodeAddEvent(node, node.getUnit(), !Bukkit.isPrimaryThread());
        EventBus.post(event);
        if (event.isCancelled()) return;
        NODES.add(node);
        if (node.canProvide() || node.canReceive()) ACTIVE_NODES.add(node);
    }

    /**
     * Removes a node from the network and disconnects all its connections.
     * @param node The {@link EnergyNode} to unregister.
     */
    public static void unregister(EnergyNode node) {
        EnergyNodeRemoveEvent event = new EnergyNodeRemoveEvent(node, node.getUnit(), !Bukkit.isPrimaryThread());
        EventBus.post(event);
        if (event.isCancelled()) return;
        NODES.remove(node);
        ACTIVE_NODES.remove(node);
        node.getConnections().forEach(n -> n.disconnect(node));
    }

    /** @return An unmodifiable view of all registered nodes. */
    public static Set<EnergyNode> getNodes() { return NODES; }

    /**
     * Updates a node's presence in the active set based on its energy levels.
     * @param node The node to evaluate.
     */
    public static void markActive(EnergyNode node) {
        if (node.canProvide() || node.canReceive()) ACTIVE_NODES.add(node);
        else ACTIVE_NODES.remove(node);
    }

    /**
     * The core distribution logic. Iterates through active nodes and attempts to
     * balance energy between connected nodes, handling unit conversion and events.
     */
    public static void distribute() {
        if (ACTIVE_NODES.isEmpty()) return;
        Set<EnergyNode> processed = new CopyOnWriteArraySet<>(ACTIVE_NODES);
        for (EnergyNode source : processed) {
            if (source.getEnergy() <= 0) {
                ACTIVE_NODES.remove(source);
                continue;
            }

            double available = source.getEnergy();
            for (EnergyNode target : source.getConnections()) {
                if (target.getEnergy() >= target.getCapacity()) continue;

                double spaceTarget = target.getCapacity() - target.getEnergy();
                double spaceInSourceUnit = target.getUnit().convert(spaceTarget, source.getUnit());

                double toTransfer = Math.min(available, spaceInSourceUnit);
                if (toTransfer <= 0) continue;

                EnergyNetworkTransferEvent event = new EnergyNetworkTransferEvent(source, target, source.getUnit(), toTransfer, !Bukkit.isPrimaryThread());
                EventBus.post(event);
                if (event.isCancelled()) continue;

                toTransfer = event.getAmount();
                double extracted = source.extract(toTransfer);
                double convertedInsert = source.getUnit().convert(extracted, target.getUnit());
                double inserted = target.insert(convertedInsert);

                if (inserted < convertedInsert) {
                    double refund = target.getUnit().convert(convertedInsert - inserted, source.getUnit());
                    source.insert(refund);
                    available -= (extracted - refund);
                } else {
                    available -= extracted;
                }

                markActive(target);
                if (available <= 0) break;
            }
            markActive(source);
        }
    }

    /**
     * Serializes all registered nodes and saves them to the database asynchronously.
     */
    public static void save() {
        BatchQuery batch = DATABASE.executor().table("energy_nodes").batch("id", "json").replace();
        for (EnergyNode node : NODES) {
            try {
                String id = node.getClass().getName() + "@" + node.hashCode();
                JsonNode json = EnergyNode.CODEC.encode(JsonOps.INSTANCE, node);
                batch.add(id, json.toString());
            } catch (Exception e) { e.printStackTrace(); }
        }
        batch.executeAsync().exceptionally(e -> {
            AbyssalLib.getInstance().getLogger().severe("Failed to save energy network: " + e.getMessage());
            return 0;
        });
    }

    /**
     * Loads energy nodes from the database and registers them back into the network.
     */
    public static void load() {
        DATABASE.executor().table("energy_nodes").selectAsync(rs -> {
            try {
                String jsonStr = rs.getString("json");
                JsonNode nodeJson = JSON_MAPPER.readTree(jsonStr);
                return EnergyNode.CODEC.decode(JsonOps.INSTANCE, nodeJson);
            } catch (Exception e) { return null; }
        }).thenAccept(loadedNodes -> {
            for (EnergyNode node : loadedNodes) if (node != null) register(node);
        });
    }
}