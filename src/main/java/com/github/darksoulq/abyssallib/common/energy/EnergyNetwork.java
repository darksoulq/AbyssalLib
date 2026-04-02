package com.github.darksoulq.abyssallib.common.energy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.database.relational.sql.BatchQuery;
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNetworkTransferEvent;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeAddEvent;
import com.github.darksoulq.abyssallib.server.event.custom.energy.EnergyNodeRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager responsible for handling all energy node interactions.
 * <p>
 * This includes:
 * <ul>
 *     <li>Registration and lifecycle management of nodes</li>
 *     <li>Energy distribution across connected graphs</li>
 *     <li>Persistence and restoration of node state</li>
 * </ul>
 *
 * <p>
 * The distribution system uses a breadth-first traversal to locate valid sinks
 * and distribute energy evenly across them.
 */
public final class EnergyNetwork {

    private EnergyNetwork() {}

    /**
     * Shared JSON mapper used for persistence.
     */
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * All registered energy nodes.
     */
    private static final Set<EnergyNode> NODES = ConcurrentHashMap.newKeySet();

    /**
     * Nodes that are currently active (able to send or receive energy).
     */
    private static final Set<EnergyNode> ACTIVE_NODES = ConcurrentHashMap.newKeySet();

    /**
     * Backing database used for persistence.
     */
    private static final Database DATABASE = new Database(new File(AbyssalLib.getInstance().getDataFolder(), "energy_network.db"));

    /**
     * Thread-local pools used to avoid allocations during traversal.
     */
    private static final ThreadLocal<Set<EnergyNode>> VISITED_POOL = ThreadLocal.withInitial(HashSet::new);
    private static final ThreadLocal<Queue<EnergyNode>> QUEUE_POOL = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Map<EnergyNode, BlockFace>> SINK_POOL = ThreadLocal.withInitial(HashMap::new);

    /**
     * Initializes the network scheduler and database.
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
     *
     * @param node the node to register
     */
    public static void register(EnergyNode node) {
        EnergyNodeAddEvent event = new EnergyNodeAddEvent(node, node.getUnit(), !Bukkit.isPrimaryThread());
        EventBus.post(event);
        if (event.isCancelled()) return;

        NODES.add(node);

        if (node.canProvide(null) || node.canReceive(null)) {
            ACTIVE_NODES.add(node);
        }
    }

    /**
     * Unregisters a node and disconnects it from all neighbors.
     *
     * @param node the node to remove
     */
    public static void unregister(EnergyNode node) {
        EnergyNodeRemoveEvent event = new EnergyNodeRemoveEvent(node, node.getUnit(), !Bukkit.isPrimaryThread());
        EventBus.post(event);
        if (event.isCancelled()) return;

        NODES.remove(node);
        ACTIVE_NODES.remove(node);

        node.getConnections().forEach((face, connected) ->
            connected.disconnect(face != null ? face.getOppositeFace() : null, node, face)
        );
    }

    /**
     * @return all registered nodes
     */
    public static Set<EnergyNode> getNodes() {
        return NODES;
    }

    /**
     * Updates whether a node should be considered active.
     *
     * @param node the node to evaluate
     */
    public static void markActive(EnergyNode node) {
        if (node.canProvide(null) || node.canReceive(null)) {
            ACTIVE_NODES.add(node);
        } else {
            ACTIVE_NODES.remove(node);
        }
    }

    /**
     * Performs energy distribution across the network.
     * <p>
     * Uses BFS traversal to locate sinks and evenly distribute energy.
     */
    public static void distribute() {
        if (ACTIVE_NODES.isEmpty()) return;

        Set<EnergyNode> visited = VISITED_POOL.get();
        Queue<EnergyNode> queue = QUEUE_POOL.get();
        Map<EnergyNode, BlockFace> sinks = SINK_POOL.get();

        for (EnergyNode source : ACTIVE_NODES) {
            if (!source.canProvide(null)) {
                ACTIVE_NODES.remove(source);
                continue;
            }

            double available = source.extract(null, source.getMaxExtract(), Action.SIMULATE);
            if (available <= 0) continue;

            visited.clear();
            queue.clear();
            sinks.clear();

            visited.add(source);
            queue.add(source);

            while (!queue.isEmpty()) {
                EnergyNode current = queue.poll();

                for (Map.Entry<BlockFace, EnergyNode> entry : current.getConnections().entrySet()) {
                    EnergyNode neighbor = entry.getValue();
                    BlockFace faceToNeighbor = entry.getKey();
                    BlockFace faceFromNeighbor =
                        faceToNeighbor != null ? faceToNeighbor.getOppositeFace() : null;

                    if (visited.add(neighbor)) {
                        if (neighbor instanceof EnergyConductor) {
                            queue.add(neighbor);
                        } else if (neighbor.canReceive(faceFromNeighbor)) {
                            sinks.put(neighbor, faceFromNeighbor);
                        }
                    }
                }
            }

            if (sinks.isEmpty()) continue;

            double energyPerSink = available / sinks.size();
            double totalExtracted = 0;

            for (Map.Entry<EnergyNode, BlockFace> entry : sinks.entrySet()) {
                EnergyNode sink = entry.getKey();
                BlockFace face = entry.getValue();

                double simulatedInsertSpace =
                    sink.insert(face, sink.getMaxInsert(), Action.SIMULATE);

                double spaceInSourceUnit =
                    sink.getUnit().convert(simulatedInsertSpace, source.getUnit());

                double toTransfer = Math.min(energyPerSink, spaceInSourceUnit);

                if (toTransfer > 0) {
                    EnergyNetworkTransferEvent event =
                        new EnergyNetworkTransferEvent(source, sink, source.getUnit(),
                            toTransfer, !Bukkit.isPrimaryThread());

                    EventBus.post(event);
                    if (event.isCancelled()) continue;

                    toTransfer = event.getAmount();

                    double extracted =
                        source.extract(null, toTransfer, Action.EXECUTE);

                    double convertedInsert =
                        source.getUnit().convert(extracted, sink.getUnit());

                    sink.insert(face, convertedInsert, Action.EXECUTE);

                    totalExtracted += extracted;
                    markActive(sink);
                }
            }

            if (totalExtracted > 0) {
                markActive(source);
            } else if (!(source instanceof EnergyConductor)) {
                ACTIVE_NODES.remove(source);
            }
        }
    }

    /**
     * Saves all nodes asynchronously to the database.
     */
    public static void save() {
        BatchQuery batch = DATABASE.executor()
            .table("energy_nodes")
            .batch("id", "json")
            .replace();

        for (EnergyNode node : NODES) {
            try {
                String id = node.getClass().getName() + "@" + node.hashCode();
                JsonNode json = EnergyNode.CODEC.encode(JsonOps.INSTANCE, node);
                batch.add(id, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        batch.executeAsync().exceptionally(e -> {
            AbyssalLib.getInstance().getLogger()
                .severe("Failed to save energy network: " + e.getMessage());
            return 0;
        });
    }

    /**
     * Loads nodes from persistent storage.
     */
    public static void load() {
        DATABASE.executor().table("energy_nodes").selectAsync(rs -> {
            try {
                String jsonStr = rs.getString("json");
                JsonNode nodeJson = JSON_MAPPER.readTree(jsonStr);
                return EnergyNode.CODEC.decode(JsonOps.INSTANCE, nodeJson);
            } catch (Exception e) {
                return null;
            }
        }).thenAccept(loadedNodes -> {
            for (EnergyNode node : loadedNodes) {
                if (node != null) register(node);
            }
        });
    }
}