package com.github.darksoulq.abyssallib.common.energy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNetworkTransferEvent;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeAddEvent;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class EnergyNetwork {

    private EnergyNetwork() {}

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Set<EnergyNode> NODES = new CopyOnWriteArraySet<>();
    private static final Set<EnergyNode> ACTIVE_NODES = new CopyOnWriteArraySet<>();
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "energy_network.db"));

    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                distribute();
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(AbyssalLib.getInstance(), 20 * 60 * 2, 20 * 60 * 5);

        try {
            DATABASE.connect();
            DATABASE.executor().table("energy_nodes").create()
                .ifNotExists()
                .column("id", "TEXT")
                .column("json", "TEXT")
                .primaryKey("id")
                .execute();
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to init energy network DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void register(EnergyNode node) {
        EnergyNodeAddEvent event = new EnergyNodeAddEvent(node, !Bukkit.isPrimaryThread());
        EventBus.post(event);
        if (event.isCancelled()) return;
        NODES.add(node);
        if (node.canProvide() || node.canReceive()) ACTIVE_NODES.add(node);
    }

    public static void unregister(EnergyNode node) {
        EnergyNodeRemoveEvent event = new EnergyNodeRemoveEvent(node, !Bukkit.isPrimaryThread());
        EventBus.post(event);
        if (event.isCancelled()) return;
        NODES.remove(node);
        ACTIVE_NODES.remove(node);
        node.getConnections().forEach(n -> n.disconnect(node));
    }

    public static Set<EnergyNode> getNodes() { return NODES; }

    public static void markActive(EnergyNode node) {
        if (node.canProvide() || node.canReceive()) ACTIVE_NODES.add(node);
        else ACTIVE_NODES.remove(node);
    }

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

                double space = target.getCapacity() - target.getEnergy();
                double toTransfer = Math.min(available, space);
                if (toTransfer <= 0) continue;

                EnergyNetworkTransferEvent event = new EnergyNetworkTransferEvent(source, target, toTransfer, !Bukkit.isPrimaryThread());
                EventBus.post(event);
                if (event.isCancelled()) continue;

                toTransfer = event.getAmount();
                if (toTransfer <= 0) continue;

                double extracted = source.extract(toTransfer);
                double inserted = target.insert(extracted);
                if (extracted > inserted) source.insert(extracted - inserted);

                markActive(target);

                available -= inserted;
                if (available <= 0) break;
            }
            markActive(source);
        }
    }

    public static void save() {
        try {
            DATABASE.transaction(executor -> {
                for (EnergyNode node : NODES) {
                    String id = node.getClass().getName() + "@" + node.hashCode();
                    JsonNode json = node.serialize(JsonOps.INSTANCE);
                    executor.table("energy_nodes").replace()
                        .value("id", id)
                        .value("json", json.toString())
                        .execute();
                }
            });
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to save energy network: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            List<EnergyNode> loadedNodes = DATABASE.executor().table("energy_nodes").select(rs -> {
                String jsonStr = rs.getString("json");
                JsonNode nodeJson = JSON_MAPPER.readTree(jsonStr);
                return EnergyNode.deserialize(JsonOps.INSTANCE, nodeJson);
            });
            loadedNodes.forEach(EnergyNetwork::register);

        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load energy network: " + e.getMessage());
            e.printStackTrace();
        }
    }
}