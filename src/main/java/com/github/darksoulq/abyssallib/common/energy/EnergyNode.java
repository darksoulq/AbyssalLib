package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Map;
import java.util.Set;

/**
 * Represents a node capable of storing, receiving, and providing energy.
 * Nodes can be connected to form a distribution network.
 */
public interface EnergyNode {

    /** @return The current energy stored in this node. */
    double getEnergy();

    /** @return The maximum energy capacity of this node. */
    double getCapacity();

    /** @return The {@link EnergyUnit} measurement system used by this node. */
    EnergyUnit getUnit();

    /** @return The {@link EnergyNodeType} definition for this node. */
    EnergyNodeType<?> getType();

    /**
     * Inserts energy into this node.
     * @param amount The amount to insert in local units.
     * @return The amount actually accepted.
     */
    double insert(double amount);

    /**
     * Extracts energy from this node.
     * @param amount The amount to extract in local units.
     * @return The amount actually removed.
     */
    double extract(double amount);

    /** @return A set of all nodes connected to this one. */
    Set<EnergyNode> getConnections();

    /**
     * Establishes a bidirectional connection between this node and another.
     * @param other The target node.
     */
    default void connect(EnergyNode other) {
        getConnections().add(other);
        other.getConnections().add(this);
    }

    /**
     * Severs the connection between this node and another.
     * @param other The target node.
     */
    default void disconnect(EnergyNode other) {
        getConnections().remove(other);
        other.getConnections().remove(this);
    }

    /** @return True if the node has remaining capacity. */
    default boolean canReceive() { return getEnergy() < getCapacity(); }

    /** @return True if the node has energy available for extraction. */
    default boolean canProvide() { return getEnergy() > 0; }

    /**
     * A polymorphic {@link Codec} for serializing and deserializing different
     * implementations of EnergyNode using their registered types.
     */
    Codec<EnergyNode> CODEC = new Codec<>() {
        @Override
        public <D> EnergyNode decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for EnergyNode"));
            String typeId = ops.getStringValue(map.get(ops.createString("type")))
                .orElseThrow(() -> new CodecException("Missing type"));

            EnergyNodeType<?> type = Registries.ENERGY_NODE_TYPES.get(typeId);
            if (type == null) throw new CodecException("Unknown energy node type: " + typeId);

            return type.codec().decode(ops, input);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, EnergyNode value) throws CodecException {
            EnergyNodeType<EnergyNode> type = (EnergyNodeType<EnergyNode>) value.getType();
            String id = Registries.ENERGY_NODE_TYPES.getId(type);
            if (id == null) throw new CodecException("Unregistered energy node type");

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Codec must return map"));
            map.put(ops.createString("type"), ops.createString(id));
            return ops.createMap(map);
        }
    };
}