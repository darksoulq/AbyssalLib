package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ArmorStand value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_base_plate", Codecs.BOOLEAN, value.hasBasePlate())
            .write("is_visible", Codecs.BOOLEAN, value.isVisible())
            .write("has_arms", Codecs.BOOLEAN, value.hasArms())
            .write("is_small", Codecs.BOOLEAN, value.isSmall())
            .write("is_marker", Codecs.BOOLEAN, value.isMarker())
            .write("can_move", Codecs.BOOLEAN, value.canMove())
            .write("can_tick", Codecs.BOOLEAN, value.canTick())
            .write("head_rotations", Codecs.VECTOR_F, toVector(value.getHeadRotations()))
            .write("body_rotations", Codecs.VECTOR_F, toVector(value.getBodyRotations()))
            .write("left_arm_rotations", Codecs.VECTOR_F, toVector(value.getLeftArmRotations()))
            .write("right_arm_rotations", Codecs.VECTOR_F, toVector(value.getRightArmRotations()))
            .write("left_leg_rotations", Codecs.VECTOR_F, toVector(value.getLeftLegRotations()))
            .write("right_leg_rotations", Codecs.VECTOR_F, toVector(value.getRightLegRotations()));

        List<String> disabledSlots = new ArrayList<>();
        value.getDisabledSlots().forEach(slot -> disabledSlots.add(slot.name()));
        ctx.write("disabled_slots", Codecs.STRING.list(), disabledSlots);

        Map<D, D> locksMap = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            List<String> activeLocks = new ArrayList<>();
            for (ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                if (value.hasEquipmentLock(slot, lockType)) activeLocks.add(lockType.name());
            }
            if (!activeLocks.isEmpty()) {
                DataResult<D> lockRes = Codecs.STRING.list().encode(ops, activeLocks);
                if (lockRes.isSuccess()) locksMap.put(ops.createString(slot.name()), lockRes.getOrThrow());
            }
        }
        if (!locksMap.isEmpty()) {
            map.put(ops.createString("equipment_locks"), ops.createMap(locksMap));
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ArmorStand stand)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_base_plate", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setBasePlate))
            .readOptional("is_visible", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setVisible))
            .readOptional("has_arms", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setArms))
            .readOptional("is_small", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setSmall))
            .readOptional("is_marker", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setMarker))
            .readOptional("can_move", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setCanMove))
            .readOptional("can_tick", Codecs.BOOLEAN, opt -> opt.ifPresent(stand::setCanTick))
            .readOptional("head_rotations", Codecs.VECTOR_F, opt -> opt.ifPresent(vec -> stand.setHeadRotations(toRotations(vec))))
            .readOptional("body_rotations", Codecs.VECTOR_F, opt -> opt.ifPresent(vec -> stand.setBodyRotations(toRotations(vec))))
            .readOptional("left_arm_rotations", Codecs.VECTOR_F, opt -> opt.ifPresent(vec -> stand.setLeftArmRotations(toRotations(vec))))
            .readOptional("right_arm_rotations", Codecs.VECTOR_F, opt -> opt.ifPresent(vec -> stand.setRightArmRotations(toRotations(vec))))
            .readOptional("left_leg_rotations", Codecs.VECTOR_F, opt -> opt.ifPresent(vec -> stand.setLeftLegRotations(toRotations(vec))))
            .readOptional("right_leg_rotations", Codecs.VECTOR_F, opt -> opt.ifPresent(vec -> stand.setRightLegRotations(toRotations(vec))))
            .readOptional("disabled_slots", Codecs.STRING.list(), opt -> opt.ifPresent(slots -> {
                for (String slotName : slots) {
                    try {
                        stand.addDisabledSlots(EquipmentSlot.valueOf(slotName));
                    } catch (Exception ignored) {
                    }
                }
            }));

        D locksData = map.get(ops.createString("equipment_locks"));
        if (locksData != null) {
            ops.getMap(locksData).ifPresent(locksMap -> {
                for (Map.Entry<D, D> entry : locksMap.entrySet()) {
                    String slotStr = ops.getStringValue(entry.getKey()).orElse("");
                    try {
                        EquipmentSlot slot = EquipmentSlot.valueOf(slotStr);
                        DataResult<List<String>> res = Codecs.STRING.list().decode(ops, entry.getValue());
                        if (res.isSuccess()) {
                            for (String lockName : res.getOrThrow()) {
                                try {
                                    stand.addEquipmentLock(slot, ArmorStand.LockType.valueOf(lockName));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        }

        return ctx.result();
    }

    private Vector toVector(Rotations rot) {
        return new Vector(rot.x(), rot.y(), rot.z());
    }

    private Rotations toRotations(Vector vector) {
        return Rotations.ofDegrees(vector.getX(), vector.getY(), vector.getZ());
    }
}