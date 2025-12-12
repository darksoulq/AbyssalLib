package com.github.darksoulq.abyssallib.common.energy;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.Set;

public interface EnergyNode {

    double getEnergy();

    double getCapacity();

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

    Codec<? extends EnergyNode> getCodec();

    @SuppressWarnings("unchecked")
    default <T> T serialize(DynamicOps<T> ops) {
        try {
            return ((Codec<EnergyNode>) getCodec()).encode(ops, this);
        } catch (Codec.CodecException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> EnergyNode deserialize(DynamicOps<T> ops, T input) {
        try {
            T classValue = ops.getMap(input)
                    .orElseThrow(() -> new RuntimeException("Input is not a map"))
                    .get(ops.createString("class"));
            String className = ops.getStringValue(classValue)
                    .orElseThrow(() -> new RuntimeException("Missing class field"));
            Class<?> clazz = Class.forName(className);
            Codec<?> codec = (Codec<?>) clazz.getField("CODEC").get(null);
            return (EnergyNode) codec.decode(ops, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
