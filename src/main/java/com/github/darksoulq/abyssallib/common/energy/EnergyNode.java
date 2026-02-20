package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Map;
import java.util.Set;

/**
 * Represents a node capable of storing, receiving, and providing energy.
 * Nodes can be connected to form a distribution network for power management.
 */
public interface EnergyNode {

    /**
     * Retrieves the current amount of energy currently stored within this node.
     *
     * @return The current energy level in the node's local {@link EnergyUnit}.
     */
    double getEnergy();

    /**
     * Retrieves the maximum amount of energy this node is capable of holding.
     *
     * @return The maximum storage capacity in the node's local {@link EnergyUnit}.
     */
    double getCapacity();

    /**
     * Retrieves the measurement system and conversion rules used by this specific node.
     *
     * @return The {@link EnergyUnit} instance associated with this node.
     */
    EnergyUnit getUnit();

    /**
     * Retrieves the type definition that identifies the implementation logic of this node.
     *
     * @return The {@link EnergyNodeType} characterizing this instance.
     */
    EnergyNodeType<?> getType();

    /**
     * Attempts to insert a specified amount of energy into the node.
     *
     * @param amount The amount of energy to insert, measured in the node's local units.
     * @return The amount of energy that was actually accepted by the node.
     */
    double insert(double amount);

    /**
     * Attempts to extract a specified amount of energy from the node.
     *
     * @param amount The amount of energy to extract, measured in the node's local units.
     * @return The amount of energy that was actually removed from the node.
     */
    double extract(double amount);

    /**
     * Retrieves a set of all other energy nodes currently connected to this instance.
     *
     * @return A {@link Set} containing all adjacent {@link EnergyNode} instances.
     */
    Set<EnergyNode> getConnections();

    /**
     * Establishes a bidirectional link between this node and another node.
     * Adding a node to this set should ideally trigger the same action in the target node.
     *
     * @param other The target node to connect to this instance.
     */
    default void connect(EnergyNode other) {
        getConnections().add(other);
        other.getConnections().add(this);
    }

    /**
     * Severs the bidirectional connection between this node and another node.
     *
     * @param other The target node to disconnect from this instance.
     */
    default void disconnect(EnergyNode other) {
        getConnections().remove(other);
        other.getConnections().remove(this);
    }

    /**
     * Checks if the node is capable of receiving any more energy based on its capacity.
     *
     * @return True if the current energy is strictly less than the maximum capacity.
     */
    default boolean canReceive() {
        return getEnergy() < getCapacity();
    }

    /**
     * Checks if the node has any energy available to be extracted.
     *
     * @return True if the current energy level is strictly greater than zero.
     */
    default boolean canProvide() {
        return getEnergy() > 0;
    }

    /**
     * A polymorphic {@link Codec} implementation used for serializing and deserializing
     * various EnergyNode implementations by leveraging their registered {@link EnergyNodeType}.
     */
    Codec<EnergyNode> CODEC = new Codec<>() {
        /**
         * Decodes an EnergyNode from a serialized format. It identifies the implementation
         * type via a "type" field and delegates to the type-specific codec.
         *
         * @param <D>   The type of the serialized data.
         * @param ops   The provider for reading data of type D.
         * @param input The raw serialized data.
         * @return The reconstructed EnergyNode instance.
         * @throws CodecException If the type is missing, unknown, or the data is malformed.
         */
        @Override
        public <D> EnergyNode decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for EnergyNode"));
            String typeId = ops.getStringValue(map.get(ops.createString("type")))
                .orElseThrow(() -> new CodecException("Missing type"));

            EnergyNodeType<?> type = Registries.ENERGY_NODE_TYPES.get(typeId);
            if (type == null) throw new CodecException("Unknown energy node type: " + typeId);

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes an EnergyNode into a serialized format. It serializes the node's data
         * and injects the "type" identifier for future polymorphism.
         *
         * @param <D>   The target type of the serialized data.
         * @param ops   The provider for creating data of type D.
         * @param value The EnergyNode instance to serialize.
         * @return The serialized data representation.
         * @throws CodecException If the node type is unregistered or the codec fails.
         */
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