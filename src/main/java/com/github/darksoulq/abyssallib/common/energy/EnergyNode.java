package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a node within the energy network capable of storing,
 * receiving, and providing energy.
 */
public interface EnergyNode {

    /**
     * @return current stored energy
     */
    double getEnergy();

    /**
     * @return maximum energy capacity
     */
    double getCapacity();

    /**
     * @return max energy accepted per tick
     */
    double getMaxInsert();

    /**
     * @return max energy extracted per tick
     */
    double getMaxExtract();

    /**
     * @return unit used by this node
     */
    EnergyUnit getUnit();

    /**
     * @return node type for serialization
     */
    EnergyNodeType<?> getType();

    /**
     * @return physical location, or null if not bound to the world
     */
    @Nullable Location getLocation();

    /**
     * Inserts energy into this node.
     *
     * @param side   input side (nullable)
     * @param amount requested amount
     * @param action execution mode
     * @return amount accepted
     */
    double insert(@Nullable BlockFace side, double amount, Action action);

    /**
     * Extracts energy from this node.
     *
     * @param side   output side (nullable)
     * @param amount requested amount
     * @param action execution mode
     * @return amount extracted
     */
    double extract(@Nullable BlockFace side, double amount, Action action);

    /**
     * @return map of connected nodes by face
     */
    Map<BlockFace, EnergyNode> getConnections();

    /**
     * Connects this node to another node.
     */
    default void connect(@Nullable BlockFace side, EnergyNode other, @Nullable BlockFace otherSide) {
        if (side != null) getConnections().put(side, other);
        if (otherSide != null) other.getConnections().put(otherSide, this);
    }

    /**
     * Disconnects this node from another node.
     */
    default void disconnect(@Nullable BlockFace side, EnergyNode other, @Nullable BlockFace otherSide) {
        if (side != null) getConnections().remove(side);
        if (otherSide != null) other.getConnections().remove(otherSide);
    }

    /**
     * @return whether this node can receive energy
     */
    default boolean canReceive(@Nullable BlockFace side) {
        return getEnergy() < getCapacity();
    }

    /**
     * @return whether this node can provide energy
     */
    default boolean canProvide(@Nullable BlockFace side) {
        return getEnergy() > 0;
    }

    /**
     * Polymorphic codec for all energy node types.
     */
    Codec<EnergyNode> CODEC = Codec.dispatch(
        EnergyNode.class,
        "type",
        Codecs.STRING,
        node -> {
            String typeId = Registries.ENERGY_NODE_TYPES.getId(node.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered energy node type");
            }
            return typeId;
        },
        typeId -> {
            EnergyNodeType<?> type = Registries.ENERGY_NODE_TYPES.get(typeId);
            if (type == null) {
                return Codec.error("Unknown energy node type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("EnergyNode");
}