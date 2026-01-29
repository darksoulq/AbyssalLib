package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.Map;
import java.util.Set;

public interface EnergyNode {

    double getEnergy();

    double getCapacity();

    EnergyUnit getUnit();

    EnergyNodeType<?> getType();

    double insert(double amount);

    double extract(double amount);

    Set<EnergyNode> getConnections();

    default void connect(EnergyNode other) {
        getConnections().add(other);
        other.getConnections().add(this);
    }

    default void disconnect(EnergyNode other) {
        getConnections().remove(other);
        other.getConnections().remove(this);
    }

    default boolean canReceive() {
        return getEnergy() < getCapacity();
    }

    default boolean canProvide() {
        return getEnergy() > 0;
    }

    Codec<EnergyNode> CODEC = new Codec<>() {
        @Override
        public <D> EnergyNode decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for EnergyNode"));
            String typeId = ops.getStringValue(map.get(ops.createString("type"))).orElseThrow(() -> new CodecException("Missing type"));

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