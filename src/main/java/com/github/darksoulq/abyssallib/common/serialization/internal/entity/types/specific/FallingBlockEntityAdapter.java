package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;

import java.util.Map;

public class FallingBlockEntityAdapter extends EntityAdapter<FallingBlock> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof FallingBlock;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, FallingBlock value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("block_data"), Codecs.STRING.encode(ops, value.getBlockData().getAsString()));
        map.put(ops.createString("drop_item"), Codecs.BOOLEAN.encode(ops, value.getDropItem()));
        map.put(ops.createString("cancel_drop"), Codecs.BOOLEAN.encode(ops, value.getCancelDrop()));
        map.put(ops.createString("hurt_entities"), Codecs.BOOLEAN.encode(ops, value.canHurtEntities()));
        map.put(ops.createString("damage_per_block"), Codecs.FLOAT.encode(ops, value.getDamagePerBlock()));
        map.put(ops.createString("max_damage"), Codecs.INT.encode(ops, value.getMaxDamage()));
        map.put(ops.createString("auto_expire"), Codecs.BOOLEAN.encode(ops, value.doesAutoExpire()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof FallingBlock block)) return;

        D blockDataStr = map.get(ops.createString("block_data"));
        if (blockDataStr != null) {
            Try.of(() -> Codecs.STRING.decode(ops, blockDataStr)).onSuccess(s -> block.setBlockData(Bukkit.createBlockData(s)));
        }

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("drop_item")))).onSuccess(block::setDropItem);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("cancel_drop")))).onSuccess(block::setCancelDrop);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("hurt_entities")))).onSuccess(block::setHurtEntities);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("damage_per_block")))).onSuccess(block::setDamagePerBlock);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_damage")))).onSuccess(block::setMaxDamage);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("auto_expire")))).onSuccess(block::shouldAutoExpire);
    }
}