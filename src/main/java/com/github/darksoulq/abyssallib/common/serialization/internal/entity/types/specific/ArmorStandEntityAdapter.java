package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.math.Rotations;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorStandEntityAdapter extends EntityAdapter<ArmorStand> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ArmorStand;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ArmorStand value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_base_plate"), Codecs.BOOLEAN.encode(ops, value.hasBasePlate()));
        map.put(ops.createString("is_visible"), Codecs.BOOLEAN.encode(ops, value.isVisible()));
        map.put(ops.createString("has_arms"), Codecs.BOOLEAN.encode(ops, value.hasArms()));
        map.put(ops.createString("is_small"), Codecs.BOOLEAN.encode(ops, value.isSmall()));
        map.put(ops.createString("is_marker"), Codecs.BOOLEAN.encode(ops, value.isMarker()));
        map.put(ops.createString("can_move"), Codecs.BOOLEAN.encode(ops, value.canMove()));
        map.put(ops.createString("can_tick"), Codecs.BOOLEAN.encode(ops, value.canTick()));

        map.put(ops.createString("head_rotations"), Codecs.VECTOR_F.encode(ops, toVector(value.getHeadRotations())));
        map.put(ops.createString("body_rotations"), Codecs.VECTOR_F.encode(ops, toVector(value.getBodyRotations())));
        map.put(ops.createString("left_arm_rotations"), Codecs.VECTOR_F.encode(ops, toVector(value.getLeftArmRotations())));
        map.put(ops.createString("right_arm_rotations"), Codecs.VECTOR_F.encode(ops, toVector(value.getRightArmRotations())));
        map.put(ops.createString("left_leg_rotations"), Codecs.VECTOR_F.encode(ops, toVector(value.getLeftLegRotations())));
        map.put(ops.createString("right_leg_rotations"), Codecs.VECTOR_F.encode(ops, toVector(value.getRightLegRotations())));

        List<String> disabledSlots = new ArrayList<>();
        value.getDisabledSlots().forEach(slot -> disabledSlots.add(slot.name()));
        map.put(ops.createString("disabled_slots"), Codecs.STRING.list().encode(ops, disabledSlots));

        Map<D, D> locksMap = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            List<String> activeLocks = new ArrayList<>();
            for (ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                if (value.hasEquipmentLock(slot, lockType)) activeLocks.add(lockType.name());
            }
            if (!activeLocks.isEmpty()) {
                locksMap.put(ops.createString(slot.name()), Codecs.STRING.list().encode(ops, activeLocks));
            }
        }
        if (!locksMap.isEmpty()) {
            map.put(ops.createString("equipment_locks"), ops.createMap(locksMap));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ArmorStand stand)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_base_plate")))).onSuccess(stand::setBasePlate);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_visible")))).onSuccess(stand::setVisible);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_arms")))).onSuccess(stand::setArms);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_small")))).onSuccess(stand::setSmall);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_marker")))).onSuccess(stand::setMarker);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_move")))).onSuccess(stand::setCanMove);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_tick")))).onSuccess(stand::setCanTick);

        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("head_rotations")))).onSuccess(v -> stand.setHeadRotations(toRotations(v)));
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("body_rotations")))).onSuccess(v -> stand.setBodyRotations(toRotations(v)));
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("left_arm_rotations")))).onSuccess(v -> stand.setLeftArmRotations(toRotations(v)));
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("right_arm_rotations")))).onSuccess(v -> stand.setRightArmRotations(toRotations(v)));
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("left_leg_rotations")))).onSuccess(v -> stand.setLeftLegRotations(toRotations(v)));
        Try.of(() -> Codecs.VECTOR_F.decode(ops, map.get(ops.createString("right_leg_rotations")))).onSuccess(v -> stand.setRightLegRotations(toRotations(v)));

        D disabledSlotsData = map.get(ops.createString("disabled_slots"));
        if (disabledSlotsData != null) {
            Try.of(() -> Codecs.STRING.list().decode(ops, disabledSlotsData)).onSuccess(list -> {
                for (String slotName : list) {
                    Try.run(() -> stand.addDisabledSlots(EquipmentSlot.valueOf(slotName)));
                }
            });
        }

        D locksData = map.get(ops.createString("equipment_locks"));
        if (locksData != null) {
            Try.of(() -> ops.getMap(locksData).orElse(new HashMap<>())).onSuccess(locksMap -> {
                for (Map.Entry<D, D> entry : locksMap.entrySet()) {
                    Try.run(() -> {
                        EquipmentSlot slot = EquipmentSlot.valueOf(ops.getStringValue(entry.getKey()).orElseThrow());
                        List<String> activeLocks = Codecs.STRING.list().decode(ops, entry.getValue());
                        for (String lockName : activeLocks) {
                            stand.addEquipmentLock(slot, ArmorStand.LockType.valueOf(lockName));
                        }
                    });
                }
            });
        }
    }

    private Vector toVector(Rotations rot) {
        return new Vector(rot.x(), rot.y(), rot.z());
    }

    private Rotations toRotations(Vector vector) {
        return Rotations.ofDegrees(vector.getX(), vector.getY(), vector.getZ());
    }
}